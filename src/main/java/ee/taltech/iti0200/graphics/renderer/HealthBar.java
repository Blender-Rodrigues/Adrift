package ee.taltech.iti0200.graphics.renderer;


import ee.taltech.iti0200.domain.entity.Damageable;
import ee.taltech.iti0200.graphics.Camera;
import ee.taltech.iti0200.graphics.Shader;
import ee.taltech.iti0200.graphics.Texture;
import ee.taltech.iti0200.graphics.Transform;
import org.joml.Vector3f;

public class HealthBar extends EntityRenderer {

    protected Texture healthBarEmpty;
    protected Texture healthBarFull;
    protected Texture healthBarGlobe;

    protected Transform transformEmpty;
    protected Transform transformFull;
    protected Transform transformGlobe;

    private float width;
    private float height;
    private float widthGlobe;
    private float heightGlobe;

    public HealthBar(Texture healthBarEmpty, Texture healthBarFull, Texture healthBarGlobe) {
        this.healthBarEmpty = healthBarEmpty;
        this.healthBarFull = healthBarFull;
        this.healthBarGlobe = healthBarGlobe;
    }

    @Override
    public void initialize() {
        super.initialize();
        width = (float) entity.getBoundingBox().getSize().getX() * 2;
        height = width / healthBarEmpty.getWidth() * healthBarEmpty.getHeight();

        widthGlobe = (float) entity.getBoundingBox().getSize().getX() / 2;
        heightGlobe = widthGlobe / healthBarGlobe.getWidth() * healthBarGlobe.getHeight();

        transformEmpty = new Transform();
        transformEmpty.scale = new Vector3f(width, height, 1);

        transformFull = new Transform();
        transformFull.scale = new Vector3f(width, height, 1);

        transformGlobe = new Transform();
        transformGlobe.scale = new Vector3f(widthGlobe, heightGlobe, 1);
    }

    @Override
    public void render(Shader shader, Camera camera, long tick) {
        float locationX = (float) entity.getBoundingBox().getCentre().getX();
        float locationY = (float) (entity.getBoundingBox().getCentre().getY() + entity.getBoundingBox().getSize().getY() * 1.5f);

        transformGlobe.pos.set(new Vector3f(locationX - width, locationY, 1));

        transformEmpty.pos.set(new Vector3f(locationX, locationY, 1));

        transformFull.scale = new Vector3f(width * getHealth(), height, 1);
        transformFull.pos.set(new Vector3f(locationX - (width - width * getHealth()), locationY, 0));

        shader.bind();
        shader.setUniform("sampler", 0);

        shader.setUniform("projection", transformEmpty.getProjection(camera.getProjection()));
        healthBarEmpty.bind(0);
        model.render();

        shader.setUniform("projection", transformFull.getProjection(camera.getProjection()));
        healthBarFull.bind(0);
        model.render();

        shader.setUniform("projection", transformGlobe.getProjection(camera.getProjection()));
        healthBarGlobe.bind(0);
        model.render();
    }

    private float getHealth() {
        return ((Damageable) entity).getHealth() / (float) ((Damageable) entity).getMaxHealth();
    }
}
