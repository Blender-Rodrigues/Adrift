package ee.taltech.iti0200.input;

import com.google.inject.Inject;
import ee.taltech.iti0200.di.annotations.LocalPlayer;
import ee.taltech.iti0200.domain.entity.Player;
import ee.taltech.iti0200.domain.entity.equipment.Gun;
import ee.taltech.iti0200.domain.event.EventBus;
import ee.taltech.iti0200.domain.event.entity.ChangeEquipment;
import ee.taltech.iti0200.domain.event.entity.GunShot;
import ee.taltech.iti0200.graphics.Camera;
import ee.taltech.iti0200.facade.GlfwInput;

import static ee.taltech.iti0200.network.message.Receiver.EVERYONE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_1;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_2;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_3;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_4;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_5;
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
import static org.lwjgl.glfw.GLFW.GLFW_REPEAT;

public class GameInput extends Input {

    private final Player player;
    private final Camera camera;
    private final Mouse mouse;
    private final EventBus eventBus;

    @Inject
    public GameInput(
        GlfwInput facade,
        @LocalPlayer Player player,
        Camera camera,
        Mouse mouse,
        EventBus eventBus
    ) {
        super(facade);
        this.player = player;
        this.camera = camera;
        this.mouse = mouse;
        this.eventBus = eventBus;
    }

    @Override
    public void initialize() {
        bind(new KeyEvent(GLFW_KEY_A, player::moveLeft, GLFW_PRESS, GLFW_REPEAT));
        bind(new KeyEvent(GLFW_KEY_D, player::moveRight, GLFW_PRESS, GLFW_REPEAT));
        bind(new KeyEvent(GLFW_KEY_W, player::jump, GLFW_PRESS));
        bind(new KeyEvent(GLFW_MOUSE_BUTTON_LEFT, this::playerShoot, GLFW_PRESS, GLFW_REPEAT));

        bind(new KeyEvent(GLFW_KEY_RIGHT, camera::moveRight, GLFW_PRESS, GLFW_REPEAT));
        bind(new KeyEvent(GLFW_KEY_LEFT, camera::moveLeft, GLFW_PRESS, GLFW_REPEAT));
        bind(new KeyEvent(GLFW_KEY_UP, camera::moveUp, GLFW_PRESS, GLFW_REPEAT));
        bind(new KeyEvent(GLFW_KEY_DOWN, camera::moveDown, GLFW_PRESS, GLFW_REPEAT));

        bind(new KeyEvent(GLFW_KEY_1, () -> changeSlot(0), GLFW_PRESS));
        bind(new KeyEvent(GLFW_KEY_2, () -> changeSlot(1), GLFW_PRESS));
        bind(new KeyEvent(GLFW_KEY_3, () -> changeSlot(2), GLFW_PRESS));
        bind(new KeyEvent(GLFW_KEY_4, () -> changeSlot(3), GLFW_PRESS));
        bind(new KeyEvent(GLFW_KEY_5, () -> changeSlot(4), GLFW_PRESS));

        bind(new KeyEvent(GLFW_KEY_I, camera::zoomIn, GLFW_PRESS, GLFW_REPEAT));
        bind(new KeyEvent(GLFW_KEY_O, camera::zoomOut, GLFW_PRESS, GLFW_REPEAT));
        bind(new KeyEvent(GLFW_KEY_F, camera::togglePlayerCam, GLFW_PRESS));
        super.initialize();
    }

    @Override
    public void update(long tick) {
        updateMouse();
        super.update(tick);
    }

    private void changeSlot(int slot) {
        eventBus.dispatch(new ChangeEquipment(player, slot, EVERYONE));
    }

    private void playerShoot() {
        Gun gun = player.getActiveGun();
        if (player.isAlive() && gun != null && gun.canShoot(currentTick)) {
            eventBus.dispatch(new GunShot(gun, player.getLookingAt(), EVERYONE));
        }
    }

    private void updateMouse() {
        mouse.update();
        player.setLookingAt(mouse.getPhysicsPosition());
    }

}
