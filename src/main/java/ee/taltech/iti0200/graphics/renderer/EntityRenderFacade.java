package ee.taltech.iti0200.graphics.renderer;

import com.google.inject.Inject;
import ee.taltech.iti0200.di.annotations.MainShader;
import ee.taltech.iti0200.di.annotations.ShieldShader;
import ee.taltech.iti0200.di.factory.RendererFactory;
import ee.taltech.iti0200.domain.World;
import ee.taltech.iti0200.domain.entity.BlastProjectile;
import ee.taltech.iti0200.domain.entity.Bot;
import ee.taltech.iti0200.domain.entity.Damageable;
import ee.taltech.iti0200.domain.entity.Entity;
import ee.taltech.iti0200.domain.entity.HealthGlobe;
import ee.taltech.iti0200.domain.entity.Living;
import ee.taltech.iti0200.domain.entity.PlasmaProjectile;
import ee.taltech.iti0200.domain.entity.Player;
import ee.taltech.iti0200.domain.entity.Projectile;
import ee.taltech.iti0200.domain.entity.Shield;
import ee.taltech.iti0200.domain.entity.Terrain;
import ee.taltech.iti0200.domain.entity.equipment.FastGun;
import ee.taltech.iti0200.domain.entity.equipment.Gun;
import ee.taltech.iti0200.domain.entity.equipment.SpecialGun;
import ee.taltech.iti0200.graphics.Animation;
import ee.taltech.iti0200.graphics.Shader;
import ee.taltech.iti0200.graphics.Texture;
import ee.taltech.iti0200.graphics.ViewPort;
import ee.taltech.iti0200.graphics.VisualFactory;
import ee.taltech.iti0200.physics.Body;
import ee.taltech.iti0200.physics.BoundingBox;
import ee.taltech.iti0200.physics.Vector;
import org.joml.Vector3f;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import static java.util.stream.Collectors.toMap;

public class EntityRenderFacade implements Renderer {

    public static final String DEFAULT = "DEFAULT";

    private static final HashMap<Class<? extends Body>, HashMap<String, Supplier<EntityRenderer>>> renderers = new HashMap<>();

    private final World world;
    private final RendererFactory rendererFactory;
    private final VisualFactory visualFactory;
    private final CompassRenderer compassRenderer;
    private Shader shieldShader;

    @Inject
    public EntityRenderFacade(World world, RendererFactory rendererFactory, VisualFactory visualFactory, CompassRenderer compassRenderer, @ShieldShader Shader shader) {
        this.world = world;
        this.rendererFactory = rendererFactory;
        this.visualFactory = visualFactory;
        this.compassRenderer = compassRenderer;
        this.shieldShader = shader;
    }

    @Override
    public void initialize() throws IOException {
        createRenderers();

        world.getEntities().forEach(entity -> {
            decorate(entity);
            if (entity instanceof Living) {
                for (Gun weapon : ((Living) entity).getWeapons()) {
                    decorate(weapon);
                }
            }
        });
        compassRenderer.initialize();
    }

    /**
     * Render entities that happen to be in view of the camera
     * Add 10 pixel padding around the viewport to have something rendered there when traveling fast
     * Negating camera coordinates as they seem to have opposite values of the world coordinates
     */
    @Override
    public void render(Shader shader, ViewPort viewPort, long tick) {
        Vector3f pos = new Vector3f(viewPort.getPosition()).negate();
        float zoom = viewPort.getZoom();

        double w = 10 + viewPort.getWidth() / 2.0 * zoom;
        double h = 10 + viewPort.getHeight() / 2.0 * zoom;
        double minX = (pos.x - w);
        double maxX = (pos.x + w);
        double minY = (pos.y - h);
        double maxY = (pos.y + h);

        int livingThingsOnScreen = 0;
        Vector closestDistance = new Vector(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);

        for (Entity entity : world.getEntities()) {
            BoundingBox box = entity.getBoundingBox();
            if (box.getMinX() < minX || box.getMaxX() > maxX || box.getMinY() < minY || box.getMinY() > maxY) {
                if (entity instanceof Living) {
                    Vector distance = new Vector(entity.getBoundingBox().getCentre());
                    distance.sub(new Vector(pos.x, pos.y));
                    if (distance.lengthSquared() < closestDistance.lengthSquared()) {
                        closestDistance = distance;
                    }
                }
                continue;
            }

            if (entity instanceof Damageable) {
                ((Damageable) entity).renderShield(shieldShader, viewPort, tick);
            }
            entity.render(shader, viewPort, tick);
            if (entity instanceof Living) {
                livingThingsOnScreen++;

                Gun gun = ((Living) entity).getActiveGun();
                if (gun != null) {
                    gun.render(shader, viewPort, tick);
                }
            }
        }
        if (livingThingsOnScreen < 2) {
            compassRenderer.setDirection(closestDistance);
            compassRenderer.render(shader, viewPort, tick);
        }
    }

    public void decorate(Entity entity) {
        if (renderers.isEmpty() || entity == null) {
            return;
        }

        HashMap<String, EntityRenderer> map = renderers.getOrDefault(entity.getClass(), renderers.get(Entity.class))
            .entrySet()
            .stream()
            .collect(toMap(
                Map.Entry::getKey,
                entry -> {
                    EntityRenderer renderer = entry.getValue().get();
                    renderer.setEntity(entity);
                    renderer.initialize();
                    return renderer;
                },
                (a, b) -> b,
                HashMap::new
            ));
        entity.setRenderers(map);
    }

    private void createRenderers() throws IOException {
        // terrain
        Texture terrainHealthy0 = visualFactory.create("world/", "healthy_0");
        Texture terrainHealthy1 = visualFactory.create("world/", "healthy_1");
        Texture terrainHealthy2 = visualFactory.create("world/", "healthy_2");
        Texture terrainHealthy3 = visualFactory.create("world/", "healthy_3");
        Texture terrainHealthy4 = visualFactory.create("world/", "healthy_4");
        Texture terrainHealthy5 = visualFactory.create("world/", "healthy_5");
        Texture terrainHealthy6 = visualFactory.create("world/", "healthy_6");
        Texture terrainHurt0 = visualFactory.create("world/", "hurt_0");
        Texture terrainHurt1 = visualFactory.create("world/", "hurt_1");
        Texture terrainHurt2 = visualFactory.create("world/", "hurt_2");
        Texture terrainHurt3 = visualFactory.create("world/", "hurt_3");
        Texture terrainHurt4 = visualFactory.create("world/", "hurt_4");
        Texture terrainHurt5 = visualFactory.create("world/", "hurt_5");
        Texture terrainHurt6 = visualFactory.create("world/", "hurt_6");
        Texture terrainDamaged0 = visualFactory.create("world/", "damaged_0");
        Texture terrainDamaged1 = visualFactory.create("world/", "damaged_1");
        Texture terrainDamaged2 = visualFactory.create("world/", "damaged_2");
        Texture terrainDamaged3 = visualFactory.create("world/", "damaged_3");
        Texture terrainDamaged4 = visualFactory.create("world/", "damaged_4");
        Texture terrainDamaged5 = visualFactory.create("world/", "damaged_5");
        Texture terrainDamaged6 = visualFactory.create("world/", "damaged_6");

        // weapon
        Texture defaultTexture = visualFactory.create("", "default");
        Texture pistolTexture = visualFactory.create("gun/", "scoped_pistol");
        Texture smgTexture = visualFactory.create("gun/", "smg");
        Texture greenPistolTexture = visualFactory.create("gun/", "green_pistol");
        Texture bulletTexture = visualFactory.create("projectile/", "bullet");
        Texture plasmaTexture = visualFactory.create("projectile/", "plasma");
        Texture blastTexture = visualFactory.create("projectile/", "green");

        // health
        Texture healthGlobeTexture = visualFactory.create("consumable/", "healthGlobe");
        Texture shieldTexture = visualFactory.create("consumable/", "shield");
        Texture healthBarEmpty = visualFactory.create("overhead/", "healthBarEmpty");
        Texture healthBarFull = visualFactory.create("overhead/", "healthBarFull");
        Texture healthBarGlobe = visualFactory.create("overhead/", "healthBarGlobe");

        // player
        Animation playerRunningRight = visualFactory.create(10, "player/animations/", "player.running.right", 3);
        Animation playerRunningLeft = visualFactory.create(10, "player/animations/", "player.running.left", 3);
        Animation playerIdleRight = visualFactory.create(4, "player/animations/", "player.idle.right", 5);
        Animation playerIdleLeft = visualFactory.create(4, "player/animations/", "player.idle.left", 5);
        Texture playerJumpingRight = visualFactory.create("player/stills/", "player.jumping.right");
        Texture playerJumpingLeft = visualFactory.create("player/stills/", "player.jumping.left");

        // bot
        Animation botMovingRight = visualFactory.create(4, "bot/animations/", "bot.right", 10);
        Animation botMovingLeft = visualFactory.create(4, "bot/animations/", "bot.left", 10);
        Animation botIdle = visualFactory.create(6, "bot/animations/", "bot.idle", 15);
        Texture botJumpingRight = visualFactory.create("bot/stills/", "bot.jumping.right");
        Texture botJumpingLeft = visualFactory.create("bot/stills/", "bot.jumping.left");

        // compass
        Texture compassArrow = visualFactory.create("compass/", "arrow");
        compassRenderer.setTexture(compassArrow);

        new Builder(Entity.class)
            .put(DEFAULT, () -> rendererFactory.create(defaultTexture));

        new Builder(Damageable.class)
            .put("shield", () -> rendererFactory.create(shieldTexture));

        new Builder(Gun.class)
            .put(DEFAULT, () -> rendererFactory.create(pistolTexture, RotatingDrawable.class));

        new Builder(FastGun.class)
            .put(DEFAULT, () -> rendererFactory.create(smgTexture, RotatingDrawable.class));

        new Builder(SpecialGun.class)
            .put(DEFAULT, () -> rendererFactory.create(greenPistolTexture, RotatingDrawable.class));

        new Builder(Player.class)
            .put("RUNNING.RIGHT", () -> rendererFactory.create(playerRunningRight))
            .put("RUNNING.LEFT", () -> rendererFactory.create(playerRunningLeft))
            .put("IDLE.RIGHT", () -> rendererFactory.create(playerIdleRight))
            .put("IDLE.LEFT", () -> rendererFactory.create(playerIdleLeft))
            .put("JUMPING.RIGHT", () -> rendererFactory.create(playerJumpingRight))
            .put("JUMPING.LEFT", () -> rendererFactory.create(playerJumpingLeft))
            .put("healthBar", () -> rendererFactory.create(healthBarEmpty, healthBarFull, healthBarGlobe))
            .put("shield", () -> rendererFactory.createShield());;

        new Builder(Bot.class)
            .put("IDLE", () -> rendererFactory.create(botIdle))
            .put("RIGHT", () -> rendererFactory.create(botMovingRight))
            .put("LEFT", () -> rendererFactory.create(botMovingLeft))
            .put("healthBar", () -> rendererFactory.create(healthBarEmpty, healthBarFull, healthBarGlobe))
            .put("shield", () -> rendererFactory.createShield());;

        new Builder(Terrain.class)
            .put("healthy_0", () -> rendererFactory.create(terrainHealthy0))
            .put("healthy_1", () -> rendererFactory.create(terrainHealthy1))
            .put("healthy_2", () -> rendererFactory.create(terrainHealthy2))
            .put("healthy_3", () -> rendererFactory.create(terrainHealthy3))
            .put("healthy_4", () -> rendererFactory.create(terrainHealthy4))
            .put("healthy_5", () -> rendererFactory.create(terrainHealthy5))
            .put("healthy_6", () -> rendererFactory.create(terrainHealthy6))
            .put("hurt_0", () -> rendererFactory.create(terrainHurt0))
            .put("hurt_1", () -> rendererFactory.create(terrainHurt1))
            .put("hurt_2", () -> rendererFactory.create(terrainHurt2))
            .put("hurt_3", () -> rendererFactory.create(terrainHurt3))
            .put("hurt_4", () -> rendererFactory.create(terrainHurt4))
            .put("hurt_5", () -> rendererFactory.create(terrainHurt5))
            .put("hurt_6", () -> rendererFactory.create(terrainHurt6))
            .put("damaged_0", () -> rendererFactory.create(terrainDamaged0))
            .put("damaged_1", () -> rendererFactory.create(terrainDamaged1))
            .put("damaged_2", () -> rendererFactory.create(terrainDamaged2))
            .put("damaged_3", () -> rendererFactory.create(terrainDamaged3))
            .put("damaged_4", () -> rendererFactory.create(terrainDamaged4))
            .put("damaged_5", () -> rendererFactory.create(terrainDamaged5))
            .put("damaged_6", () -> rendererFactory.create(terrainDamaged6));

        new Builder(Projectile.class)
            .put(DEFAULT, () -> rendererFactory.create(bulletTexture));

        new Builder(BlastProjectile.class)
            .put(DEFAULT, () -> rendererFactory.create(blastTexture));

        new Builder(PlasmaProjectile.class)
            .put(DEFAULT, () -> rendererFactory.create(plasmaTexture));

        new Builder(HealthGlobe.class)
            .put(DEFAULT, () -> rendererFactory.create(healthGlobeTexture));

        new Builder(Shield.class)
            .put(DEFAULT, () -> rendererFactory.create(shieldTexture));
    }

    private static class Builder {

        HashMap<String, Supplier<EntityRenderer>> map = new HashMap<>();

        private Builder(Class<? extends Body> type) {
            renderers.put(type, map);
        }

        private Builder put(String key, Supplier<EntityRenderer> supplier) {
            map.put(key, supplier);
            return this;
        }

    }

}
