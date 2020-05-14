package ee.taltech.iti0200.graphics.renderer;

import ee.taltech.iti0200.graphics.Animation;
import ee.taltech.iti0200.graphics.Shader;
import ee.taltech.iti0200.graphics.ViewPort;

public class Animateable extends EntityRenderer {

    private transient Animation animation;

    public Animateable(Animation animation) {
        this.animation = animation;
    }

    @Override
    public void render(Shader shader, ViewPort viewPort, long tick) {
        super.render(shader, viewPort, tick);
        animation.bind(tick);
        model.render();
    }

}
