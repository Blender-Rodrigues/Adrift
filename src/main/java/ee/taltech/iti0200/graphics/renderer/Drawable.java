package ee.taltech.iti0200.graphics.renderer;

import ee.taltech.iti0200.graphics.Shader;
import ee.taltech.iti0200.graphics.Texture;
import ee.taltech.iti0200.graphics.ViewPort;

public class Drawable extends EntityRenderer {

    protected transient Texture texture;

    public Drawable(Texture texture) {
        this.texture = texture;
    }

    @Override
    public void render(Shader shader, ViewPort viewPort, long tick) {
        super.render(shader, viewPort, tick);
        texture.bind(0);
        model.render();
    }

    public Texture getTexture() {
        return texture;
    }

}
