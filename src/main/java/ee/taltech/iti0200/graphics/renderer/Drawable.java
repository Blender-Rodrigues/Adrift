package ee.taltech.iti0200.graphics.renderer;

import ee.taltech.iti0200.graphics.Camera;
import ee.taltech.iti0200.graphics.Shader;
import ee.taltech.iti0200.graphics.Texture;

public class Drawable extends EntityRenderer {

    protected transient Texture texture;

    public Drawable(Texture texture) {
        this.texture = texture;
    }

    @Override
    public void render(Shader shader, Camera camera, long tick) {
        super.render(shader, camera, tick);
        texture.bind(0);
        model.render();
    }

    public Texture getTexture() {
        return texture;
    }

}
