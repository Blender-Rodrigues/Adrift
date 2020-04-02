package ee.taltech.iti0200.graphics;

import com.google.inject.Inject;
import ee.taltech.iti0200.domain.entity.Entity;
import ee.taltech.iti0200.domain.entity.Rotatable;
import ee.taltech.iti0200.physics.Vector;
import org.joml.Matrix4f;

public class RotatingDrawable extends Drawable {

    private CoordinateConverter converter;
    private Camera camera;

    @Inject
    public RotatingDrawable(Texture texture, CoordinateConverter converter, Camera camera) {
        super(texture);
        this.converter = converter;
        this.camera = camera;
    }

    @Override
    public Renderer setEntity(Entity entity) {
        if (entity instanceof Rotatable) {
            return super.setEntity(entity);
        }
        throw new IllegalArgumentException(entity + " does not support rotation");
    }

    @Override
    protected void setShaderRotation(Shader shader) {
        Matrix4f rotation = getRotation();

        Vector cameraVector = converter.physicsToCamera(entity.getBoundingBox().getCentre());
        cameraVector.setX(cameraVector.getX() * 2 / (camera.getZoom() * camera.getWidth()));
        cameraVector.setY(- cameraVector.getY() * 2 / (camera.getZoom() * camera.getHeight()));

        Matrix4f locationMatrix = new Matrix4f().setTranslation((float) cameraVector.getX(), (float) cameraVector.getY(), 0);
        Matrix4f inverseLocationMatrix = new Matrix4f().setTranslation((float) - cameraVector.getX(), (float) - cameraVector.getY(), 0);

        shader.setUniform("rotation", rotation);
        shader.setUniform("location", locationMatrix);
        shader.setUniform("inverseLocation", inverseLocationMatrix);
    }

    private Matrix4f getRotation() {
        Vector rotation = ((Rotatable) entity).getRotation();

        if (rotation == null) {
            rotation = new Vector(1, 0);
        }

        float rotationAngle = - (float) (Math.atan2(rotation.getX(), rotation.getY()));

        if (rotationAngle < 0) {
            return new Matrix4f().setRotationXYZ(0F, 0F, rotationAngle + (float) Math.PI / 2);
        }

        Matrix4f mirrorMatrix = new Matrix4f().m00(-1);
        Matrix4f rotationMatrix = new Matrix4f().setRotationXYZ(0F, 0F, - rotationAngle + (float) Math.PI / 2);
        rotationMatrix = mirrorMatrix.mul(rotationMatrix);

        return rotationMatrix;
    }

}
