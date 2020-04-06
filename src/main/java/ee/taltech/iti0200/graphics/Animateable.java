package ee.taltech.iti0200.graphics;

public class Animateable extends EntityRenderer {

    private transient Animation animation;

    public Animateable(Animation animation) {
        this.animation = animation;
    }

    @Override
    public void render(Shader shader, Camera camera, long tick) {
        super.render(shader, camera, tick);
        animation.bind(tick);
        model.render();
    }

}
