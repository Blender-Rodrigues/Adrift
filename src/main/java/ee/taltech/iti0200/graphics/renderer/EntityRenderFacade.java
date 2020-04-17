package ee.taltech.iti0200.graphics.renderer;

import com.google.inject.Inject;
import ee.taltech.iti0200.di.factory.RendererFactory;

import ee.taltech.iti0200.domain.World;
import ee.taltech.iti0200.domain.entity.Bot;
import ee.taltech.iti0200.domain.entity.Entity;
import ee.taltech.iti0200.domain.entity.equipment.FastGun;
import ee.taltech.iti0200.domain.entity.equipment.Gun;
import ee.taltech.iti0200.domain.entity.HealthGlobe;
import ee.taltech.iti0200.domain.entity.Living;
import ee.taltech.iti0200.domain.entity.Player;
import ee.taltech.iti0200.domain.entity.Projectile;
import ee.taltech.iti0200.domain.entity.equipment.SpecialGun;
import ee.taltech.iti0200.domain.entity.Terrain;

import ee.taltech.iti0200.graphics.Camera;
import ee.taltech.iti0200.graphics.Shader;
import ee.taltech.iti0200.graphics.Texture;
import ee.taltech.iti0200.graphics.Animation;
import ee.taltech.iti0200.graphics.VisualFactory;

import ee.taltech.iti0200.physics.Body;
import ee.taltech.iti0200.physics.BoundingBox;
import org.joml.Vector3f;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import static java.util.stream.Collectors.toMap;

public class EntityRenderFacade implements Renderer {

    public static final String DEFAULT = "DEFAULT";

    private static final HashMap<Class<? extends Body>, HashMap<String, Supplier<EntityRenderer>>> renderers = new HashMap<>();

    private World world;
    private RendererFactory rendererFactory;
    private VisualFactory visualFactory;

    @Inject
    public EntityRenderFacade(World world, RendererFactory rendererFactory, VisualFactory visualFactory) {
        this.world = world;
        this.rendererFactory = rendererFactory;
        this.visualFactory = visualFactory;
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
    }

    /**
     * Render entities that happen to be in view of the camera
     * Add 10 pixel padding around the viewport to have something rendered there when traveling fast
     * Negating camera coordinates as they seem to have opposite values of the world coordinates
     */
    @Override
    public void render(Shader shader, Camera camera, long tick) {
        Vector3f pos = new Vector3f(camera.getPosition()).negate();
        float zoom = camera.getZoom();

        double w = 10 + camera.getWidth() / 2.0 * zoom;
        double h = 10 + camera.getHeight() / 2.0 * zoom;
        double minX = (pos.x - w);
        double maxX = (pos.x + w);
        double minY = (pos.y - h);
        double maxY = (pos.y + h);

        for (Entity entity : world.getEntities()) {
            BoundingBox box = entity.getBoundingBox();
            if (box.getMinX() < minX || box.getMaxX() > maxX || box.getMinY() < minY || box.getMinY() > maxY) {
                continue;
            }
            entity.render(shader, camera, tick);
            if (entity instanceof Living) {
                Gun gun = ((Living) entity).getActiveGun();
                if (gun != null) {
                    gun.render(shader, camera, tick);
                }
            }
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

        Texture defaultTexture = visualFactory.create("", "default");
        Texture pistolTexture = visualFactory.create("gun/", "scoped_pistol");
        Texture smgTexture = visualFactory.create("gun/", "smg");
        Texture greenPistolTexture = visualFactory.create("gun/", "green_pistol");
        Texture projectileTexture = visualFactory.create("projectile/", "bullet");
        Texture healthGlobeTexture = visualFactory.create("consumable/", "healthGlobe");
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

        HashMap<String, Supplier<EntityRenderer>> defaultRenderer = new HashMap<>();
        defaultRenderer.put(DEFAULT, () -> rendererFactory.create(defaultTexture));
        renderers.put(Entity.class, defaultRenderer);

//        HashMap<String, Supplier<EntityRenderer>> weaponRenderer = new HashMap<>();
//        weaponRenderer.put("PISTOL", () -> rendererFactory.create(pistolTexture, RotatingDrawable.class));
//        weaponRenderer.put("SMG", () -> rendererFactory.create(smgTexture, RotatingDrawable.class));
//        renderers.put(Gun.class, weaponRenderer);

        HashMap<String, Supplier<EntityRenderer>> pistolRenderer = new HashMap<>();
        pistolRenderer.put(DEFAULT, () -> rendererFactory.create(pistolTexture, RotatingDrawable.class));
        renderers.put(Gun.class, pistolRenderer);

        HashMap<String, Supplier<EntityRenderer>> smgRenderer = new HashMap<>();
        smgRenderer.put(DEFAULT, () -> rendererFactory.create(smgTexture, RotatingDrawable.class));
        renderers.put(FastGun.class, smgRenderer);

        HashMap<String, Supplier<EntityRenderer>> specialGunRenderer = new HashMap<>();
        specialGunRenderer.put(DEFAULT, () -> rendererFactory.create(greenPistolTexture, RotatingDrawable.class));
        renderers.put(SpecialGun.class, specialGunRenderer);

        HashMap<String, Supplier<EntityRenderer>> playerRenderer = new HashMap<>();
        playerRenderer.put("RUNNING.RIGHT", () -> rendererFactory.create(playerRunningRight));
        playerRenderer.put("RUNNING.LEFT", () -> rendererFactory.create(playerRunningLeft));
        playerRenderer.put("IDLE.RIGHT", () -> rendererFactory.create(playerIdleRight));
        playerRenderer.put("IDLE.LEFT", () -> rendererFactory.create(playerIdleLeft));

        playerRenderer.put("JUMPING.RIGHT", () -> rendererFactory.create(playerJumpingRight));
        playerRenderer.put("JUMPING.LEFT", () -> rendererFactory.create(playerJumpingLeft));
        playerRenderer.put("healthBar", () -> rendererFactory.create(healthBarEmpty, healthBarFull, healthBarGlobe));
        renderers.put(Player.class, playerRenderer);

        HashMap<String, Supplier<EntityRenderer>> botRenderer = new HashMap<>();
        botRenderer.put("IDLE", () -> rendererFactory.create(botIdle));
        botRenderer.put("RIGHT", () -> rendererFactory.create(botMovingRight));
        botRenderer.put("LEFT", () -> rendererFactory.create(botMovingLeft));
        botRenderer.put("healthBar", () -> rendererFactory.create(healthBarEmpty, healthBarFull, healthBarGlobe));
        renderers.put(Bot.class, botRenderer);

        HashMap<String, Supplier<EntityRenderer>> terrainRenderer = new HashMap<>();
        terrainRenderer.put("healthy_0", () -> rendererFactory.create(terrainHealthy0));
        terrainRenderer.put("healthy_1", () -> rendererFactory.create(terrainHealthy1));
        terrainRenderer.put("healthy_2", () -> rendererFactory.create(terrainHealthy2));
        terrainRenderer.put("healthy_3", () -> rendererFactory.create(terrainHealthy3));
        terrainRenderer.put("healthy_4", () -> rendererFactory.create(terrainHealthy4));
        terrainRenderer.put("healthy_5", () -> rendererFactory.create(terrainHealthy5));
        terrainRenderer.put("healthy_6", () -> rendererFactory.create(terrainHealthy6));
        terrainRenderer.put("hurt_0", () -> rendererFactory.create(terrainHurt0));
        terrainRenderer.put("hurt_1", () -> rendererFactory.create(terrainHurt1));
        terrainRenderer.put("hurt_2", () -> rendererFactory.create(terrainHurt2));
        terrainRenderer.put("hurt_3", () -> rendererFactory.create(terrainHurt3));
        terrainRenderer.put("hurt_4", () -> rendererFactory.create(terrainHurt4));
        terrainRenderer.put("hurt_5", () -> rendererFactory.create(terrainHurt5));
        terrainRenderer.put("hurt_6", () -> rendererFactory.create(terrainHurt6));
        terrainRenderer.put("damaged_0", () -> rendererFactory.create(terrainDamaged0));
        terrainRenderer.put("damaged_1", () -> rendererFactory.create(terrainDamaged1));
        terrainRenderer.put("damaged_2", () -> rendererFactory.create(terrainDamaged2));
        terrainRenderer.put("damaged_3", () -> rendererFactory.create(terrainDamaged3));
        terrainRenderer.put("damaged_4", () -> rendererFactory.create(terrainDamaged4));
        terrainRenderer.put("damaged_5", () -> rendererFactory.create(terrainDamaged5));
        terrainRenderer.put("damaged_6", () -> rendererFactory.create(terrainDamaged6));
        renderers.put(Terrain.class, terrainRenderer);

        HashMap<String, Supplier<EntityRenderer>> projectileRenderer = new HashMap<>();
        projectileRenderer.put(DEFAULT, () -> rendererFactory.create(projectileTexture));
        renderers.put(Projectile.class, projectileRenderer);

        HashMap<String, Supplier<EntityRenderer>> healthGlobeRenderer = new HashMap<>();
        healthGlobeRenderer.put(DEFAULT, () -> rendererFactory.create(healthGlobeTexture));
        renderers.put(HealthGlobe.class, healthGlobeRenderer);
    }

}
