package ee.taltech.iti0200.graphics;

public class Drawable extends EntityRenderer {

    private transient Texture texture;

    public Drawable(Texture texture) {
        this.texture = texture;
    }

    @Override
    public void render(Shader shader, Camera camera, long tick) {
        super.render(shader, camera, tick);
        texture.bind(0);
        model.render();
    }

}
