package ee.taltech.iti0200.menu;

import ee.taltech.iti0200.proxy.GlfwInput;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.lwjgl.glfw.GLFWKeyCallbackI;
import org.mockito.ArgumentCaptor;

import static ee.taltech.iti0200.menu.Direction.DOWN;
import static ee.taltech.iti0200.menu.Direction.LEFT;
import static ee.taltech.iti0200.menu.Direction.RIGHT;
import static ee.taltech.iti0200.menu.Direction.UP;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_0;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_9;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_A;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_BACKSPACE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_DOWN;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ENTER;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_KP_DIVIDE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_UP;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MenuInputTest {

    private ArgumentCaptor<GLFWKeyCallbackI> captor;
    private GlfwInput proxy;
    private Menu menu;
    private MenuInput input;

    @BeforeEach
    void setUp() {
        proxy = mock(GlfwInput.class);
        menu = mock(Menu.class, RETURNS_DEEP_STUBS);
        input = new MenuInput(proxy, menu);
        captor = ArgumentCaptor.forClass(GLFWKeyCallbackI.class);
    }

    @Test
    void initializeBindsArrowMenuChangeKeys() {
        input.initialize();

        verify(proxy).setKeyCallback(captor.capture());
        GLFWKeyCallbackI callback = captor.getValue();
        callback.invoke(1L, GLFW_KEY_RIGHT, 0,  GLFW_RELEASE, 0);
        callback.invoke(1L, GLFW_KEY_LEFT, 0,  GLFW_RELEASE, 0);
        callback.invoke(1L, GLFW_KEY_UP, 0,  GLFW_RELEASE, 0);
        callback.invoke(1L, GLFW_KEY_DOWN, 0,  GLFW_RELEASE, 0);

        input.update(1L);

        verify(menu).changeActive(RIGHT);
        verify(menu).changeActive(LEFT);
        verify(menu).changeActive(UP);
        verify(menu).changeActive(DOWN);
    }

    @Test
    void initializeBindsEnterToInteractWhenActive() {
        Runnable action = mock(Runnable.class);

        input.initialize();

        when(menu.getActive().getAction()).thenReturn(action);

        verify(proxy).setKeyCallback(captor.capture());
        GLFWKeyCallbackI callback = captor.getValue();
        callback.invoke(1L, GLFW_KEY_ENTER, 0,  GLFW_RELEASE, 0);

        input.update(1L);

        verify(action).run();
    }

    @Test
    void initializeBindsEnterToDoNothingWhenNoAction() {
        input.initialize();

        when(menu.getActive().getAction()).thenReturn(null);

        verify(proxy).setKeyCallback(captor.capture());
        GLFWKeyCallbackI callback = captor.getValue();
        callback.invoke(1L, GLFW_KEY_ENTER, 0,  GLFW_RELEASE, 0);

        input.update(1L);

        verify(menu, atLeastOnce()).getActive();
    }

    @Test
    void invokeKeyIgnoresValueChangeIfActiveItemIsNotWritable() {
        input.initialize();

        when(menu.getActive().isWritable()).thenReturn(false);

        verify(proxy).setKeyCallback(captor.capture());
        GLFWKeyCallbackI callback = captor.getValue();
        callback.invoke(1L, GLFW_KEY_0, 0,  GLFW_RELEASE, 0);

        verify(menu.getActive()).isWritable();
        verify(menu.getActive(), never()).setValue(anyString());
    }

    @Test
    void invokeKeyIgnoresValueChangeIfKeyNotInAllowedRange() {
        input.initialize();

        when(menu.getActive().isWritable()).thenReturn(true);

        verify(proxy).setKeyCallback(captor.capture());
        GLFWKeyCallbackI callback = captor.getValue();
        callback.invoke(1L, GLFW_KEY_KP_DIVIDE, 0,  GLFW_RELEASE, 0);

        verify(menu.getActive()).isWritable();
        verify(menu.getActive(), never()).setValue(anyString());
    }

    @Test
    void invokeKeyIgnoresValueChangeIfBackspaceAndEmptyValue() {
        input.initialize();

        when(menu.getActive().isWritable()).thenReturn(true);
        when(menu.getActive().getValue()).thenReturn("");

        verify(proxy).setKeyCallback(captor.capture());
        GLFWKeyCallbackI callback = captor.getValue();
        callback.invoke(1L, GLFW_KEY_BACKSPACE, 0,  GLFW_RELEASE, 0);

        verify(menu.getActive()).isWritable();
        verify(menu.getActive()).getValue();
        verify(menu.getActive(), never()).setValue(anyString());
    }

    @Test
    void invokeKeyDeletesLastCharacterOfValueIfBackspace() {
        input.initialize();

        when(menu.getActive().isWritable()).thenReturn(true);
        when(menu.getActive().getValue()).thenReturn("Character");

        verify(proxy).setKeyCallback(captor.capture());
        GLFWKeyCallbackI callback = captor.getValue();
        callback.invoke(1L, GLFW_KEY_BACKSPACE, 0,  GLFW_RELEASE, 0);

        verify(menu.getActive()).setValue("Characte");
    }

    @Test
    void invokeKeyIgnoresValueChangeIfActivePortAndKeyNotNumber() {
        MenuItem port = mock(MenuItem.class);

        input.initialize();

        when(menu.getActive()).thenReturn(port);
        when(menu.getPort()).thenReturn(port);
        when(port.isWritable()).thenReturn(true);

        verify(proxy).setKeyCallback(captor.capture());
        GLFWKeyCallbackI callback = captor.getValue();
        callback.invoke(1L, GLFW_KEY_A, 0,  GLFW_RELEASE, 0);

        verify(port).isWritable();
        verify(port, never()).setValue(anyString());
    }

    @Test
    void invokeKeyAppendsNumberToPort() {
        MenuItem port = mock(MenuItem.class);

        input.initialize();

        when(menu.getActive()).thenReturn(port);
        when(menu.getPort()).thenReturn(port);
        when(port.isWritable()).thenReturn(true);
        when(port.getValue()).thenReturn("0120");

        verify(proxy).setKeyCallback(captor.capture());
        GLFWKeyCallbackI callback = captor.getValue();
        callback.invoke(1L, GLFW_KEY_9, 0,  GLFW_RELEASE, 0);

        verify(port).setValue("01209");
    }

}
