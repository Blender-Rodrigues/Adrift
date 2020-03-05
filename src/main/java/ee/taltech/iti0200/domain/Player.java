package ee.taltech.iti0200.domain;

import ee.taltech.iti0200.graphics.Camera;
import ee.taltech.iti0200.graphics.Model;
import ee.taltech.iti0200.graphics.Shader;
import ee.taltech.iti0200.graphics.Texture;
import ee.taltech.iti0200.graphics.Transform;
import ee.taltech.iti0200.physics.Body;
import ee.taltech.iti0200.physics.Vector;
import org.joml.Vector3f;

public class Player extends Entity {

    private static final Vector size = new Vector(1.5, 1.5);
    private static final double mass = 70.0;
    private static final double elasticity = 0.25;

    private Model model;
    private Texture texture;
    private Transform transform;

    public Player(Vector position) {
        super(new Body(mass, new Vector(size), position, true, true), false);
        setElasticity(elasticity);
    }

}
