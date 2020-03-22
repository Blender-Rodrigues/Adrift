package ee.taltech.iti0200.graphics;

import ee.taltech.iti0200.domain.entity.Entity;

import static ee.taltech.iti0200.graphics.Graphics.defaultAnimation;

public class Animateable extends Renderer {

    private transient Animation animation;

    public Animateable(Entity entity) {
        super(entity);
    }

    @Override
    public void initializeGraphics() {
        super.initializeGraphics();
        animation = defaultAnimation;
    }

    @Override
    public void render(Shader shader, Camera camera, long tick) {
        super.render(shader, camera, tick);
        animation.bind(tick);
        model.render();
    }

}
