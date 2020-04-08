package ee.taltech.iti0200.graphics;

import com.google.inject.Inject;
import ee.taltech.iti0200.di.factory.RendererFactory;
import ee.taltech.iti0200.domain.World;
import ee.taltech.iti0200.domain.entity.Bot;
import ee.taltech.iti0200.domain.entity.Entity;
import ee.taltech.iti0200.domain.entity.FastGun;
import ee.taltech.iti0200.domain.entity.Gun;
import ee.taltech.iti0200.domain.entity.HealthGlobe;
import ee.taltech.iti0200.domain.entity.Living;
import ee.taltech.iti0200.domain.entity.Player;
import ee.taltech.iti0200.domain.entity.Projectile;
import ee.taltech.iti0200.domain.entity.Terrain;
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
                decorate(((Living) entity).getGun());
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
                Gun gun = ((Living) entity).getGun();
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
        Texture terrainTexture = visualFactory.create("world/", "concrete");
        Texture defaultTexture = visualFactory.create("", "default");
        Texture gunTexture = visualFactory.create("gun/", "shotgun");
        Texture projectileTexture = visualFactory.create("projectile/", "bullet");
        Texture healthGlobeTexture = visualFactory.create("consumable/", "healthGlobe");

        // player
        Animation playerRunningRight = visualFactory.create(10, "player/animations/", "player.running.right", 3);
        Animation playerRunningLeft = visualFactory.create(10, "player/animations/", "player.running.left", 3);
        Animation playerIdleRight = visualFactory.create(4, "player/animations/", "player.idle.right", 5);
        Animation playerIdleLeft = visualFactory.create(4, "player/animations/", "player.idle.left", 5);
        Texture playerJumpingRight = visualFactory.create("player/stills/", "player.jumping.right");
        Texture playerJumpingLeft = visualFactory.create("player/stills/", "player.jumping.left");

        Animation botDefault = visualFactory.create(2, "bot/", "bot.default", 20);

        HashMap<String, Supplier<EntityRenderer>> defaultRenderer = new HashMap<>();
        defaultRenderer.put(DEFAULT, () -> rendererFactory.create(defaultTexture));
        renderers.put(Entity.class, defaultRenderer);

        HashMap<String, Supplier<EntityRenderer>> gunRenderer = new HashMap<>();
        gunRenderer.put(DEFAULT, () -> rendererFactory.create(gunTexture, RotatingDrawable.class));
        renderers.put(Gun.class, gunRenderer);
        renderers.put(FastGun.class, gunRenderer);

        HashMap<String, Supplier<EntityRenderer>> playerRenderer = new HashMap<>();
        playerRenderer.put("RUNNING.RIGHT", () -> rendererFactory.create(playerRunningRight));
        playerRenderer.put("RUNNING.LEFT", () -> rendererFactory.create(playerRunningLeft));
        playerRenderer.put("IDLE.RIGHT", () -> rendererFactory.create(playerIdleRight));
        playerRenderer.put("IDLE.LEFT", () -> rendererFactory.create(playerIdleLeft));
        playerRenderer.put("JUMPING.RIGHT", () -> rendererFactory.create(playerJumpingRight));
        playerRenderer.put("JUMPING.LEFT", () -> rendererFactory.create(playerJumpingLeft));
        renderers.put(Player.class, playerRenderer);

        HashMap<String, Supplier<EntityRenderer>> botRenderer = new HashMap<>();
        botRenderer.put(DEFAULT, () -> rendererFactory.create(botDefault));
        renderers.put(Bot.class, botRenderer);

        HashMap<String, Supplier<EntityRenderer>> terrainRenderer = new HashMap<>();
        terrainRenderer.put(DEFAULT, () -> rendererFactory.create(terrainTexture));
        renderers.put(Terrain.class, terrainRenderer);

        HashMap<String, Supplier<EntityRenderer>> projectileRenderer = new HashMap<>();
        projectileRenderer.put(DEFAULT, () -> rendererFactory.create(projectileTexture));
        renderers.put(Projectile.class, projectileRenderer);

        HashMap<String, Supplier<EntityRenderer>> healthGlobeRenderer = new HashMap<>();
        healthGlobeRenderer.put(DEFAULT, () -> rendererFactory.create(healthGlobeTexture));
        renderers.put(HealthGlobe.class, healthGlobeRenderer);
    }

}
