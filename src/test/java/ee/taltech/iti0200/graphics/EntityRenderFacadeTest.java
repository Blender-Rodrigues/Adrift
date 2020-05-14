package ee.taltech.iti0200.graphics;

import ee.taltech.iti0200.di.factory.RendererFactory;
import ee.taltech.iti0200.domain.World;
import ee.taltech.iti0200.domain.entity.Bot;
import ee.taltech.iti0200.domain.entity.Terrain;
import ee.taltech.iti0200.domain.entity.equipment.Gun;
import ee.taltech.iti0200.graphics.renderer.CompassRenderer;
import ee.taltech.iti0200.graphics.renderer.EntityRenderFacade;
import ee.taltech.iti0200.graphics.renderer.EntityRenderer;
import ee.taltech.iti0200.physics.BoundingBox;
import org.joml.Vector3f;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class EntityRenderFacadeTest {

    private Bot bot;
    private Terrain terrain;
    private EntityRenderer renderer;
    private World world;
    private EntityRenderFacade facade;
    private Shader shader;

    @BeforeEach
    void setUp() throws IOException {
        bot = mock(Bot.class);
        terrain = mock(Terrain.class);
        renderer = mock(EntityRenderer.class);
        world = mock(World.class);
        shader = mock(Shader.class);

        Texture texture = mock(Texture.class);
        VisualFactory visualFactory = mock(VisualFactory.class);
        when(visualFactory.create(any(String.class), any(String.class))).thenReturn(texture);

        RendererFactory rendererFactory = mock(RendererFactory.class, RETURNS_DEEP_STUBS);
        when(rendererFactory.create(texture)).thenReturn(renderer);
        when(rendererFactory.create(texture, texture, texture)).thenReturn(renderer);
        CompassRenderer compassRenderer = mock(CompassRenderer.class);

        facade = new EntityRenderFacade(world, rendererFactory, visualFactory, compassRenderer, shader);
    }

    @Test
    void initializeDecoratesEntitiesInWorld() throws IOException {
        when(world.getEntities()).thenReturn(asList(bot, terrain));

        facade.initialize();

        verify(renderer, times(21)).setEntity(terrain);
        verify(renderer, times(1)).setEntity(bot);
        verify(renderer, times(22)).initialize();
    }

    @Test
    void renderDelegatesRenderOnlyToEntitiesOnScreen() {
        Camera camera = mock(Camera.class);
        Shader shader = mock(Shader.class);
        Gun gun = mock(Gun.class);

        when(world.getEntities()).thenReturn(asList(bot, terrain));
        when(bot.getActiveGun()).thenReturn(gun);

        when(camera.getPosition()).thenReturn(new Vector3f(5, 5, 0));
        when(camera.getZoom()).thenReturn(1f);
        when(camera.getWidth()).thenReturn(50);
        when(camera.getHeight()).thenReturn(50);

        when(bot.getBoundingBox()).thenReturn(new BoundingBox(-40, -40, 20, 20));
        when(terrain.getBoundingBox()).thenReturn(new BoundingBox(-100, -100, -50, -50));

        facade.render(shader, camera, 1);

        verify(terrain, never()).render(shader, camera, 1);
        verify(bot).render(shader, camera, 1);
        verify(gun).render(shader, camera, 1);
    }

    @Test
    void decorateDoesNothingWhenNotInitialized() {
        when(world.getEntities()).thenReturn(asList(bot, terrain));

        facade.decorate(bot);

        verifyNoInteractions(renderer);
    }

    @Test
    void decorateAttachesRendererToEntity() throws IOException {
        facade.initialize();

        when(world.getEntities()).thenReturn(singletonList(bot));

        facade.decorate(bot);

        verify(renderer).setEntity(bot);
        verify(renderer).initialize();
    }

}
