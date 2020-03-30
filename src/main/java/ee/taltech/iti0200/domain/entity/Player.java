package ee.taltech.iti0200.domain.entity;

import ee.taltech.iti0200.domain.World;
import ee.taltech.iti0200.graphics.Camera;
import ee.taltech.iti0200.graphics.Shader;
import ee.taltech.iti0200.physics.BoundingBox;
import ee.taltech.iti0200.physics.Vector;
import org.joml.Matrix4f;

import static ee.taltech.iti0200.graphics.Graphics.DEFAULT;

public class Player extends Living {

    private static final long serialVersionUID = 1L;

    public static final int JUMP_AMOUNT_LIMIT = 2;

    private static final Vector SIZE = new Vector(1.0, 1.0);
    private static final double MASS = 70.0;
    private static final double ELASTICITY = 0.25;
    private static final double FRICTION_COEFFICIENT = 0.99;
    private static final double JUMP_DELTA_V = 10.0;
    private static final int MAX_HEALTH = 200;

    private int jumpsLeft;
    private Gun gun;
    private Vector lookingAt;

    public Player(Vector position, World world) {
        super(MASS, new BoundingBox(position, SIZE), world, MAX_HEALTH);
        this.elasticity = ELASTICITY;
        this.jumpsLeft = JUMP_AMOUNT_LIMIT;
        this.frictionCoefficient = FRICTION_COEFFICIENT;
        this.lookingAt = new Vector(1f, 0f);
    }

    public Player setGun(Gun gun) {
        this.gun = gun;
        gun.setOwner(this);
        return this;
    }

    public Gun getGun() {
        return gun;
    }

    public void setLookingAt(Vector targetPosition) {
        targetPosition.sub(boundingBox.getCentre());
        targetPosition.normalize();
        lookingAt = targetPosition;
        gun.setPointedAt(lookingAt);
    }

    public Vector getLookingAt() {
        return lookingAt;
    }

    public double getJumpDeltaV() {
        return JUMP_DELTA_V;
    }

    public void setJumpsLeft(int jumpsLeft) {
        this.jumpsLeft = jumpsLeft;
    }

    public int getJumpsLeft() {
        return this.jumpsLeft;
    }

    @Override
    public void render(Shader shader, Camera camera, long tick, Matrix4f rotation) {
        String renderer = jumpsLeft < JUMP_AMOUNT_LIMIT ? "jump" : DEFAULT;
        renderers.get(renderer).render(shader, camera, tick, rotation);
    }

}
