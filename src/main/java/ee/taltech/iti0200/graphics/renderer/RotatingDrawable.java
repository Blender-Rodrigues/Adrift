package ee.taltech.iti0200.graphics.renderer;

import com.google.inject.Inject;
import ee.taltech.iti0200.domain.entity.Entity;
import ee.taltech.iti0200.domain.entity.Rotatable;

import ee.taltech.iti0200.graphics.Shader;
import ee.taltech.iti0200.graphics.Texture;

import ee.taltech.iti0200.physics.Vector;
import org.joml.Matrix4f;

public class RotatingDrawable extends Drawable {

    @Inject
    public RotatingDrawable(Texture texture) {
        super(texture);
    }

    @Override
    public EntityRenderer setEntity(Entity entity) {
        if (entity instanceof Rotatable) {
            return super.setEntity(entity);
        }
        throw new IllegalArgumentException(entity + " does not support rotation");
    }

    @Override
    protected void setShaderRotation(Shader shader) {
        Matrix4f rotation = getRotation();

        shader.setUniform("rotation", rotation);
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
