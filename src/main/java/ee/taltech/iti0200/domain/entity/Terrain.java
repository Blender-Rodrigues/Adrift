package ee.taltech.iti0200.domain.entity;

import ee.taltech.iti0200.graphics.*;
import ee.taltech.iti0200.physics.BoundingBox;
import ee.taltech.iti0200.physics.Vector;

public class Terrain extends Damageable implements Drawable {

    private static final long serialVersionUID = 1L;

    private static final Vector SIZE = new Vector(2.00, 2.00);
    private static final double MASS = Double.POSITIVE_INFINITY;
    private static final double ELASTICITY = 0.9;
    private static final double FRICTION_COEFFICIENT = 0.9;
    private static final int MAX_HEALTH = 100;

    public static final double TERRAIN_BLOCK_RESOLUTION = 100;

    private Model model;
    private Transform transform;
    private Texture texture;

    public Terrain(Vector position) {
        super(MASS, new BoundingBox(position.rounded(), SIZE), MAX_HEALTH);
        this.elasticity = ELASTICITY;
        this.frictionCoefficient = FRICTION_COEFFICIENT;
        this.collideable = true;
    }

    public int getIntegerWidth() {
        return (int) getBoundingBox().getSize().getX() * (int) TERRAIN_BLOCK_RESOLUTION;
    }

    @Override
    public void setModel(Model model) {
        this.model = model;
    }

    @Override
    public Model getModel() {
        return model;
    }

    @Override
    public void setTransform(Transform transform) {
        this.transform = transform;
    }

    @Override
    public Transform getTransform() {
        return transform;
    }

    @Override
    public void setTexture(Texture texture) {
        this.texture = texture;
    }

    @Override
    public Texture getTexture() {
        return texture;
    }

    //for testing purposes only
    @Override
    public void initializeGraphics() {
        initializeGraphicsTest();
    }

    @Override
    public void render(Shader shader, Camera camera, long tick) {
        renderTest(shader, camera, tick);
    }

}
