package ee.taltech.iti0200.di.factory;

import ee.taltech.iti0200.graphics.Animateable;
import ee.taltech.iti0200.graphics.Animation;
import ee.taltech.iti0200.graphics.Camera;
import ee.taltech.iti0200.graphics.CoordinateConverter;
import ee.taltech.iti0200.graphics.Drawable;
import ee.taltech.iti0200.graphics.EntityRenderer;
import ee.taltech.iti0200.graphics.RotatingDrawable;
import ee.taltech.iti0200.graphics.Texture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class EntityRendererFactoryTest {

    private CoordinateConverter converter;
    private Camera camera;
    private RendererFactory factory;

    @BeforeEach
    void setUp() {
        converter = mock(CoordinateConverter.class);
        camera = mock(Camera.class);
        factory = new RendererFactory(converter, camera);
    }

    @Test
    void createReturnsRotating() {
        Texture texture = mock(Texture.class);

        EntityRenderer renderer = factory.create(texture, RotatingDrawable.class);

        assertThat(renderer).isInstanceOf(RotatingDrawable.class);
    }

    @Test
    void createReturnsDrawable() {
        Texture texture = mock(Texture.class);

        factory = new RendererFactory(converter, camera);

        EntityRenderer renderer = factory.create(texture);

        assertThat(renderer).isInstanceOf(Drawable.class);
    }

    @Test
    void createReturnsAnimatable() {
        Animation animation = mock(Animation.class);

        factory = new RendererFactory(converter, camera);

        EntityRenderer renderer = factory.create(animation);

        assertThat(renderer).isInstanceOf(Animateable.class);
    }

}
