package ee.taltech.iti0200.graphics;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Use this annotation on a test class or method (that is not extending GraphicsTest as those already inherit this)
 * to disable tests from being run on a build server which doesn't have a GUI.
 *
 * When using on a method it can be used instead of @Test
 */
@Target({ ElementType.ANNOTATION_TYPE, ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Test
@DisabledIfEnvironmentVariable(named = "NO_GUI", matches = "true")
public @interface GuiTest {

}
