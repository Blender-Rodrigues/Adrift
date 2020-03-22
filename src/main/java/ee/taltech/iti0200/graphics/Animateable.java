package ee.taltech.iti0200.graphics;

import ee.taltech.iti0200.domain.entity.Entity;

public class Animateable extends Renderer {

    private transient Animation animation;

    public Animateable(Entity entity) {
        super(entity);
    }

    @Override
    public void initializeGraphics() {
        super.initializeGraphics();
        animation = new Animation(6, "anim");
    }

    @Override
    public void render(Shader shader, Camera camera, long tick) {
        super.render(shader, camera, tick);
        animation.bind(tick);
        model.render();
    }

}
