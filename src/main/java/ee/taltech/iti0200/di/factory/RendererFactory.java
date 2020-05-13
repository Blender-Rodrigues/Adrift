package ee.taltech.iti0200.di.factory;

import com.google.inject.Inject;
import ee.taltech.iti0200.domain.Score;
import ee.taltech.iti0200.graphics.Animation;
import ee.taltech.iti0200.graphics.CoordinateConverter;
import ee.taltech.iti0200.graphics.renderer.Animateable;
import ee.taltech.iti0200.graphics.renderer.Drawable;
import ee.taltech.iti0200.graphics.renderer.EntityRenderer;
import ee.taltech.iti0200.graphics.renderer.HealthBar;
import ee.taltech.iti0200.graphics.renderer.RotatingDrawable;
import ee.taltech.iti0200.graphics.renderer.ScoreRenderer;
import ee.taltech.iti0200.graphics.Texture;
import ee.taltech.iti0200.graphics.renderer.Shield;

public class RendererFactory {

    private final Score score;
    private final CoordinateConverter converter;

    @Inject
    public RendererFactory(Score score, CoordinateConverter converter) {
        this.score = score;
        this.converter = converter;
    }

    public EntityRenderer createShield() {
        return new Shield(converter);
    }

    public EntityRenderer create(Animation animation) {
        return new Animateable(animation);
    }

    public EntityRenderer create(Texture texture) {
        return create(texture, Drawable.class);
    }

    public ScoreRenderer createScoreRenderer() {
        return new ScoreRenderer(score);
    }

    public EntityRenderer create(Texture texture, Class<? extends EntityRenderer> type) {
        if (type == RotatingDrawable.class) {
            return new RotatingDrawable(texture);
        }
        return new Drawable(texture);
    }

    public EntityRenderer create(Texture textureEmpty, Texture textureFull, Texture textureGlobe) {
        return new HealthBar(textureEmpty, textureFull, textureGlobe);
    }

}
