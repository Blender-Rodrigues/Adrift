package ee.taltech.iti0200.graphics;

import org.joml.Matrix4f;

public class Animateable extends Renderer {

    private transient Animation animation;

    public Animateable(Animation animation, CoordinateConverter converter) {
        super(converter);
        this.animation = animation;
    }

    @Override
    public void render(Shader shader, Camera camera, long tick, Matrix4f rotation) {
        super.render(shader, camera, tick, rotation);
        animation.bind(tick);
        model.render();
    }

}
