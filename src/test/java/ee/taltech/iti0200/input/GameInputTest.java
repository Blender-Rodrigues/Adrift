package ee.taltech.iti0200.input;

import ee.taltech.iti0200.domain.entity.Player;
import ee.taltech.iti0200.domain.entity.equipment.Gun;
import ee.taltech.iti0200.domain.event.EventBus;
import ee.taltech.iti0200.domain.event.entity.ChangeEquipment;
import ee.taltech.iti0200.domain.event.entity.GunShot;
import ee.taltech.iti0200.graphics.Camera;
import ee.taltech.iti0200.facade.GlfwInput;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.lwjgl.glfw.GLFWKeyCallbackI;
import org.lwjgl.glfw.GLFWMouseButtonCallbackI;
import org.mockito.ArgumentCaptor;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_3;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_A;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_DOWN;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_F;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_I;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_O;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_UP;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_W;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class GameInputTest {

    private ArgumentCaptor<GLFWKeyCallbackI> captor;
    private GlfwInput facade;
    private Player player;
    private Camera camera;
    private Mouse mouse;
    private EventBus eventBus;
    private GameInput input;

    @BeforeEach
    void setUp() {
        facade = mock(GlfwInput.class);
        player = mock(Player.class);
        camera = mock(Camera.class);
        mouse = mock(Mouse.class);
        eventBus = mock(EventBus.class);

        input = new GameInput(facade, player, camera, mouse, eventBus);
        captor = ArgumentCaptor.forClass(GLFWKeyCallbackI.class);
    }

    @Test
    void initializeBindsPlayerMoveKeys() {
        input.initialize();

        verify(facade).setKeyCallback(captor.capture());
        GLFWKeyCallbackI callback = captor.getValue();
        callback.invoke(1L, GLFW_KEY_A, 0,  GLFW_PRESS, 0);
        callback.invoke(1L, GLFW_KEY_D, 0,  GLFW_PRESS, 0);
        callback.invoke(1L, GLFW_KEY_W, 0,  GLFW_PRESS, 0);

        input.update(1L);

        verify(player).moveLeft();
        verify(player).moveRight();
        verify(player).jump();
    }

    @Test
    void initializeBindsShootToMouse() {
        ArgumentCaptor<GLFWMouseButtonCallbackI> mouseCaptor = ArgumentCaptor.forClass(GLFWMouseButtonCallbackI.class);
        Gun gun = mock(Gun.class);

        when(player.getActiveGun()).thenReturn(gun);
        when(player.isAlive()).thenReturn(true);
        when(gun.canShoot(1L)).thenReturn(true);

        input.initialize();

        verify(facade).setMouseButtonCallback(mouseCaptor.capture());
        GLFWMouseButtonCallbackI callback = mouseCaptor.getValue();
        callback.invoke(1L, GLFW_MOUSE_BUTTON_LEFT, GLFW_PRESS, 0);

        input.update(1L);

        verify(eventBus).dispatch(any(GunShot.class));
    }

    @Test
    void initializeBindsKeysForCameraControl() {
        input.initialize();

        verify(facade).setKeyCallback(captor.capture());
        GLFWKeyCallbackI callback = captor.getValue();
        callback.invoke(1L, GLFW_KEY_RIGHT, 0,  GLFW_PRESS, 0);
        callback.invoke(1L, GLFW_KEY_LEFT, 0,  GLFW_PRESS, 0);
        callback.invoke(1L, GLFW_KEY_UP, 0,  GLFW_PRESS, 0);
        callback.invoke(1L, GLFW_KEY_DOWN, 0,  GLFW_PRESS, 0);
        callback.invoke(1L, GLFW_KEY_I, 0,  GLFW_PRESS, 0);
        callback.invoke(1L, GLFW_KEY_O, 0,  GLFW_PRESS, 0);
        callback.invoke(1L, GLFW_KEY_F, 0,  GLFW_PRESS, 0);

        input.update(1L);

        verify(camera).moveRight();
        verify(camera).moveLeft();
        verify(camera).moveUp();
        verify(camera).moveDown();
        verify(camera).zoomIn();
        verify(camera).zoomOut();
        verify(camera).togglePlayerCam();
    }

    @Test
    void initializeBindsKeysForInventoryChange() {
        input.initialize();

        verify(facade).setKeyCallback(captor.capture());
        GLFWKeyCallbackI callback = captor.getValue();
        callback.invoke(1L, GLFW_KEY_3, 0,  GLFW_PRESS, 0);

        input.update(1L);

        verify(eventBus).dispatch(any(ChangeEquipment.class));
    }

}
