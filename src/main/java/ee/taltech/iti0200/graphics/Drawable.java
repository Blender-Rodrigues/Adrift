package ee.taltech.iti0200.graphics;

import ee.taltech.iti0200.domain.entity.Entity;

import static ee.taltech.iti0200.graphics.Graphics.defaultTexture;

public class Drawable extends Renderer {

    private transient Texture texture;

    public Drawable(Entity entity) {
        super(entity);
    }

    @Override
    public void initializeGraphics() {
        super.initializeGraphics();
        texture = defaultTexture;
    }

    @Override
    public void render(Shader shader, Camera camera, long tick) {
        super.render(shader, camera, tick);
        texture.bind(0);
        model.render();
    }

}
