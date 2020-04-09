package ee.taltech.iti0200.graphics;


import org.joml.Vector3f;

public class HealthBar extends EntityRenderer {

    protected Texture textureShell;
    protected Texture textureFilling;

    protected Transform transformShell;
    protected Transform transformFilling;

    private float width;
    private float height;

    public HealthBar(Texture textureShell, Texture textureFilling) {
        this.textureShell = textureShell;
        this.textureFilling = textureFilling;
    }

    @Override
    public void initialize() {
        super.initialize();
        width = (float) entity.getBoundingBox().getSize().getX() * 2;
        height = width / 17 * 3; // image ratio is 17:3

        transformShell = new Transform();
        transformShell.scale = new Vector3f(width, height, 1);

        transformFilling = new Transform();
        transformFilling.scale = new Vector3f(width, height, 1);
    }

    @Override
    public void render(Shader shader, Camera camera, long tick) {
        float locationX = (float) entity.getBoundingBox().getCentre().getX();
        float locationY = (float) (entity.getBoundingBox().getCentre().getY() + entity.getBoundingBox().getSize().getY() * 1.5f);

        transformShell.pos.set(new Vector3f(locationX, locationY, 1));

        transformFilling.scale = new Vector3f(width*getHealth(), height, 1);
        transformFilling.pos.set(new Vector3f(locationX - (width - width*getHealth()), locationY, 0));

        shader.bind();
        shader.setUniform("sampler", 0);

        shader.setUniform("projection", transformFilling.getProjection(camera.getProjection()));
        textureFilling.bind(0);
        model.render();

        shader.setUniform("projection", transformShell.getProjection(camera.getProjection()));
        textureShell.bind(0);
        model.render();
    }

    private float getHealth() {
        return 0.9f;
    }
}
