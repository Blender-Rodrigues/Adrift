package ee.taltech.iti0200.graphics.renderer;

import ee.taltech.iti0200.domain.entity.Damageable;
import ee.taltech.iti0200.graphics.CoordinateConverter;
import ee.taltech.iti0200.graphics.Shader;
import ee.taltech.iti0200.graphics.ViewPort;
import ee.taltech.iti0200.physics.Vector;
import org.joml.Vector2f;

public class Shield extends EntityRenderer {

    private CoordinateConverter converter;

    public Shield(CoordinateConverter converter) {
        this.converter = converter;
    }

    @Override
    public void render(Shader shader, ViewPort viewPort, long tick) {
        Vector location = entity.getBoundingBox().getCentre();
        location = converter.physicsToCamera(location);
        location = converter.cameraToScreen(location);

        shader.bind();
        shader.setUniform("sampler", 0);
        shader.setUniform("intensity", ((Damageable) entity).getShield());
        shader.setUniform("size", new Vector2f((float) (2 * entity.getBoundingBox().getSize().getX()) / viewPort.getZoom(), (float) (2 * entity.getBoundingBox().getSize().getY()) / viewPort.getZoom()));
        shader.setUniform("location", new Vector2f((float) location.getX(), (float) location.getY()));
        setShaderRotation(shader);
        model.render();
    }

}
