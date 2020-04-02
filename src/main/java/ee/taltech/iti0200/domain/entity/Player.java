package ee.taltech.iti0200.domain.entity;

import ee.taltech.iti0200.domain.World;
import ee.taltech.iti0200.graphics.Camera;
import ee.taltech.iti0200.graphics.Shader;
import ee.taltech.iti0200.physics.BoundingBox;
import ee.taltech.iti0200.physics.Vector;

import static ee.taltech.iti0200.graphics.Graphics.DEFAULT;

public class Player extends Living {

    private static final long serialVersionUID = 1L;

    public static final int JUMP_AMOUNT_LIMIT = 2;

    private static final Vector SIZE = new Vector(1.0, 1.0);
    private static final double MASS = 70.0;
    private static final double ELASTICITY = 0.15;
    private static final double FRICTION_COEFFICIENT = 0.99;
    private static final double JUMP_DELTA_V = 15.0;
    private static final int MAX_HEALTH = 200;
    private static final double PERMEABILITY = 1;

    private int jumpsLeft;
    private Vector lookingAt;

    public Player(Vector position, World world) {
        super(MASS, new BoundingBox(position, SIZE), world, MAX_HEALTH);
        this.elasticity = ELASTICITY;
        this.jumpsLeft = JUMP_AMOUNT_LIMIT;
        this.frictionCoefficient = FRICTION_COEFFICIENT;
        this.lookingAt = new Vector(1f, 0f);
        this.permeability = PERMEABILITY;
    }

    public void setLookingAt(Vector targetPosition) {
        targetPosition.sub(boundingBox.getCentre());
        targetPosition.normalize();
        lookingAt = targetPosition;
        gun.setRotation(lookingAt);
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
    public void render(Shader shader, Camera camera, long tick) {
        String renderer = jumpsLeft < JUMP_AMOUNT_LIMIT ? "jump" : DEFAULT;
        renderers.get(renderer).render(shader, camera, tick);
    }

}
