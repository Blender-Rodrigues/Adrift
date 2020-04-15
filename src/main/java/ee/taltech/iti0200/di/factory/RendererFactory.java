package ee.taltech.iti0200.di.factory;

import com.google.inject.Inject;
import ee.taltech.iti0200.domain.Score;
import ee.taltech.iti0200.graphics.Animation;
import ee.taltech.iti0200.graphics.renderer.Animateable;
import ee.taltech.iti0200.graphics.renderer.Drawable;
import ee.taltech.iti0200.graphics.renderer.EntityRenderer;
import ee.taltech.iti0200.graphics.renderer.HealthBar;
import ee.taltech.iti0200.graphics.renderer.RotatingDrawable;
import ee.taltech.iti0200.graphics.renderer.ScoreRenderer;
import ee.taltech.iti0200.graphics.Texture;

public class RendererFactory {

    private final Score score;

    @Inject
    public RendererFactory(Score score) {
        this.score = score;
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

    public EntityRenderer create(Texture textureShell, Texture textureFilling) {
        return new HealthBar(textureShell, textureFilling);
    }

}
