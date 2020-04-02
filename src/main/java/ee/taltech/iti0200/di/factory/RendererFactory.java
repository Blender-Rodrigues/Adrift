package ee.taltech.iti0200.di.factory;

import com.google.inject.Inject;
import ee.taltech.iti0200.graphics.Animateable;
import ee.taltech.iti0200.graphics.Animation;
import ee.taltech.iti0200.graphics.Camera;
import ee.taltech.iti0200.graphics.CoordinateConverter;
import ee.taltech.iti0200.graphics.Drawable;
import ee.taltech.iti0200.graphics.Renderer;
import ee.taltech.iti0200.graphics.RotatingDrawable;
import ee.taltech.iti0200.graphics.Texture;

public class RendererFactory {

    private final CoordinateConverter converter;
    private final Camera camera;

    @Inject
    public RendererFactory(CoordinateConverter converter, Camera camera) {
        this.converter = converter;
        this.camera = camera;
    }

    public Renderer create(Animation animation) {
        return new Animateable(animation);
    }

    public Renderer create(Texture texture) {
        return create(texture, Drawable.class);
    }

    public Renderer create(Texture texture, Class<? extends Renderer> type) {
        if (type == RotatingDrawable.class) {
            return new RotatingDrawable(texture, converter, camera);
        }
        return new Drawable(texture);
    }

}
