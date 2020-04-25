package ee.taltech.iti0200.ai;

import ee.taltech.iti0200.domain.World;
import ee.taltech.iti0200.domain.entity.Bot;
import ee.taltech.iti0200.domain.entity.Entity;
import ee.taltech.iti0200.domain.entity.equipment.Gun;
import ee.taltech.iti0200.domain.entity.Player;
import ee.taltech.iti0200.domain.event.EventBus;
import ee.taltech.iti0200.domain.event.entity.GunShot;
import ee.taltech.iti0200.physics.Vector;

import java.util.Random;

import static ee.taltech.iti0200.ai.Sensor.AUDIO;
import static ee.taltech.iti0200.ai.Sensor.DAMAGE;
import static ee.taltech.iti0200.ai.Sensor.VISUAL;
import static ee.taltech.iti0200.network.message.Receiver.SERVER;

/**
 * Tries to follow and shoot a player
 */
public class LookForPlayer extends Goal {

    private static final double LOOK_ANGLE = 0.2;
    private static final Vector SPEED = new Vector(0.75, 0.2);
    private static final double JUMP_SPEED = 10;
    private static final int JUMP_DELAY = 50;
    private static final double AIR_SPEED_MODIFIER = 0.15;
    private static final double TARGET_LOOK_INTENSITY = 0.9;
    private static final int LOOK_DELAY = 5;
    private static final int GUNSHOT_LOOK_DISTANCE = 20;
    private static final int ADRENALINE_GUN_SHOT = 20;
    private static final int ADRENALINE_DAMAGE = 50;
    private static final int ADRENALINE_SPOT_PLAYER = 20;
    private static final int Y_LIMIT_FOR_JUMP = 3;

    private Memory memory;
    private boolean targetChanged;
    private double lastDistanceSquared;

    public LookForPlayer(Bot bot, World world, EventBus eventBus, Memory memory, Random random) {
        super(bot, world, eventBus, random);
        this.memory = memory;
        this.targetChanged = false;
        this.lastDistanceSquared = Double.POSITIVE_INFINITY;
    }

    @Override
    public void execute(long tick) {
        Vector target = getTarget();
        Vector targetDirection;

        if (target.equals(new Vector())) {
            targetDirection = new Vector(random.nextDouble() - 0.5, (random.nextDouble() - 0.45) / 5);
        } else {
            targetDirection = getLookingDirectionFromTarget(target);
        }

        jumpIfNecessary(tick, targetDirection);
        targetDirection.normalize();
        bot.lookTowards(targetDirection, TARGET_LOOK_INTENSITY);

        targetDirection.elementWiseMultiple(SPEED);
        if (!bot.isOnFloor()) {
            targetDirection.setX(targetDirection.getX() * AIR_SPEED_MODIFIER);
        }

        move(targetDirection);

        lookAndShoot(tick);
    }

    @Override
    public long react(long tick, Sensor sensor, Vector location, Vector direction, Entity other) {
        if (sensor == VISUAL && other instanceof Player) {
            if (bot.canShoot(tick)) {
                eventBus.dispatch(new GunShot(bot.getActiveGun(), direction, SERVER));
            }
            memory.addTarget(location, TargetType.OPPONENT);
            targetChanged = true;
            return ADRENALINE_SPOT_PLAYER;
        }

        if (sensor == AUDIO && other instanceof Gun) {
            double distance = bot.getBoundingBox().getCentre().distance(location);
            if (distance < GUNSHOT_LOOK_DISTANCE) {
                lookFor(direction, LOOK_ANGLE, Player.class);
                memory.addTarget(location, TargetType.GUNSHOT);
                targetChanged = true;
            }
            return (long) Math.max(ADRENALINE_GUN_SHOT - distance, 0);
        }

        if (sensor == DAMAGE) {
            return ADRENALINE_DAMAGE;
        }

        return 0;
    }

    private void jumpIfNecessary(long tick, Vector targetDirection) {
        if (targetChanged) {
            targetChanged = false;
            lastDistanceSquared = targetDirection.lengthSquared();
        } else {
            double thisDistance = targetDirection.lengthSquared();
            if (tick % JUMP_DELAY == 0 && (thisDistance >= lastDistanceSquared || targetDirection.getY() > Y_LIMIT_FOR_JUMP)) {
                bot.accelerate(new Vector(0, JUMP_SPEED));
            }
            lastDistanceSquared = thisDistance;
        }
    }

    private void lookAndShoot(long tick) {
        if (tick % LOOK_DELAY == 0) {
            confirmNoTargets();
            lookFor(bot.getLookingAt(), LOOK_ANGLE, Player.class);
            shootIfNecessary(tick);
        }
    }

    private void shootIfNecessary(long tick) {
        if (bot.canShoot(tick) && memory.getTargets().size() != 0) {
            eventBus.dispatch(new GunShot(bot.getActiveGun(), bot.getLookingAt(), SERVER));
        }
    }

    private Vector getTarget() {
        int targetsAmount = memory.getTargets().size();
        if (targetsAmount == 0) {
            return new Vector();
        }
        return memory.getTargets().get(targetsAmount - 1).getLeft();
    }

    private Vector getLookingDirectionFromTarget(Vector target) {
        Vector direction = new Vector(target);
        direction.sub(bot.getBoundingBox().getCentre());
        return direction;
    }

    private void confirmNoTargets() {
        memory.getTargets().removeIf(location -> {
            Vector targetLocation = new Vector(location.getLeft());
            targetLocation.sub(bot.getBoundingBox().getCentre());
            return targetLocation.angle(bot.getLookingAt()) < LOOK_ANGLE && visible(location.getLeft());
        });
    }

}
