package ee.taltech.iti0200.graphics;

import ee.taltech.iti0200.domain.entity.Entity;

import static ee.taltech.iti0200.graphics.Graphics.defaultAnimation;

public class Animateable extends Renderer {

    private transient Animation animation;

    //todo creating an animation in Player or Bot etc does not work.
    //todo should all animations be created before the game starts?

    public Animateable(Entity entity) {
        super(entity);
//        this.animation = defaultAnimation;
    }

    public Animateable(Entity entity, Animation animation) {
        super(entity);
//        this.animation = animation;
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

    public void setAnimation(Animation animation) {
        this.animation = animation;
    }

}
