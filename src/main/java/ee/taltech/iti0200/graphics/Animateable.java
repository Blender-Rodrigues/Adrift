package ee.taltech.iti0200.graphics;

import ee.taltech.iti0200.physics.BoundingBox;
import org.joml.Vector3f;

public interface Animateable {

    default void initializeGraphicsTest() {
        float[] vertices = new float[]{
            -1f, 1f, 0,
            1f, 1f, 0,
            1f, -1f, 0,
            -1f, -1f, 0
        };

        float[] texture = new float[]{
            0, 0,
            1, 0,
            1, 1,
            0, 1
        };

        int[] indices = new int[]{
            0, 1, 2,
            2, 3, 0
        };

        setModel(new Model(vertices, texture, indices));
//        this.texture = defaultTexture;
        setAnimation(new Animation(6, "anim"));

        Transform transform = new Transform();
        transform.scale = new Vector3f((float) getBoundingBox().getSize().getX(), (float) getBoundingBox().getSize().getY(), 1);
        setTransform(transform);
    }

//    public void changeTexture(String filename) {
//        this.texture = new Texture(filename);
//    }

    default void renderTest(Shader shader, Camera camera, long tick) {
        getTransform().pos.set(new Vector3f((float) getBoundingBox().getCentre().x, (float) getBoundingBox().getCentre().y, 0));

        shader.bind();
        shader.setUniform("sampler", 0);
        shader.setUniform("projection", getTransform().getProjection(camera.getProjection()));
        getAnimation().bind(tick);
        getModel().render();
    }

    BoundingBox getBoundingBox();

    void setModel(Model model);

    void setAnimation(Animation animation);

    void setTransform(Transform transform);

    Transform getTransform();

    Model getModel();

    Animation getAnimation();

}
