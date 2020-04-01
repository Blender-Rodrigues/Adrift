package ee.taltech.iti0200.graphics;

import org.joml.Matrix4f;

public class Drawable extends Renderer {

    private transient Texture texture;

    public Drawable(Texture texture, CoordinateConverter converter) {
        this.texture = texture;
        super.converter = converter;
    }

    @Override
    public void render(Shader shader, Camera camera, long tick, Matrix4f rotation) {
        super.render(shader, camera, tick, rotation);
        texture.bind(0);
        model.render();
    }

}
