package ee.taltech.iti0200.menu;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MenuTest {

    private Menu menu;

    @BeforeEach
    void setUp() {
        menu = new Menu(new String[]{"client", "127.0.0.0", "9000", "name"});
    }

    @Test
    void constructorInitializesItems() {
        MenuItem host = menu.getHost();
        MenuItem port = menu.getPort();
        MenuItem playerName = menu.getPlayerName();
        MenuItem active = menu.getActive();

        assertThat(host.getValue()).isEqualTo("127.0.0.0");
        assertThat(port.getValue()).isEqualTo("9000");
        assertThat(playerName.getValue()).isEqualTo("name");
        assertThat(menu.getClient()).isEqualTo(active);
    }

    @Test
    void changeActiveRetainsValueIfDirectionHasNoConnection() {
        MenuItem active = menu.getActive();

        menu.changeActive(Direction.UP);

        assertThat(menu.getActive()).isSameAs(active);
    }

    @Test
    void changeActiveChangesValueIfConnectionExists() {
        MenuItem active = menu.getActive();

        menu.changeActive(Direction.DOWN);

        assertThat(menu.getActive()).isNotEqualTo(active);
        assertThat(menu.getActive()).isEqualTo(menu.getHost());
    }

}
