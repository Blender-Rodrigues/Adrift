package ee.taltech.iti0200.menu;

import ee.taltech.iti0200.application.RestartGame;

import static ee.taltech.iti0200.menu.Direction.DOWN;
import static ee.taltech.iti0200.menu.Direction.LEFT;
import static ee.taltech.iti0200.menu.Direction.RIGHT;
import static ee.taltech.iti0200.menu.Direction.UP;

public class Menu {

    private int gameMode = 0;
    private String message = "";

    private final MenuItem singlePlayer = new MenuItem("Start", "Single-player", false);
    private final MenuItem client = new MenuItem("Start", "Multiplayer", false);
    private final MenuItem host = new MenuItem("Host:", "127.0.0.1", true);
    private final MenuItem port = new MenuItem("Port:", "8880", true);
    private final MenuItem playerName = new MenuItem("Player Name:", "Unknown", true);
    private final MenuItem exit = new MenuItem("Exit", "", false);

    private MenuItem active;

    public Menu(String[] args) {
        if (args.length >= 2) {
            host.setValue(args[1]);
            client.setActive(true);
            active = client;
        } else {
            singlePlayer.setActive(true);
            active = singlePlayer;
        }
        if (args.length >= 3) {
            port.setValue(args[2]);
        }
        if (args.length >= 4) {
            playerName.setValue(args[3]);
        }

        singlePlayer.addNeighbour(RIGHT, client)
            .addNeighbour(DOWN, exit)
            .setAction(() -> setGameMode(1));

        client.addNeighbour(LEFT, singlePlayer)
            .addNeighbour(DOWN, host)
            .setAction(() -> setGameMode(2));

        host.addNeighbour(LEFT, singlePlayer)
            .addNeighbour(UP, client)
            .addNeighbour(DOWN, port);

        port.addNeighbour(LEFT, singlePlayer)
            .addNeighbour(UP, host)
            .addNeighbour(DOWN, playerName);

        playerName.addNeighbour(LEFT, singlePlayer)
            .addNeighbour(UP, port)
            .addNeighbour(DOWN, exit);

        exit.addNeighbour(LEFT, singlePlayer)
            .addNeighbour(RIGHT, client)
            .addNeighbour(UP, playerName)
            .setAction(() -> { throw new RestartGame("Exit was pressed"); });
    }

    public MenuItem getSinglePlayer() {
        return singlePlayer;
    }

    public MenuItem getClient() {
        return client;
    }

    public MenuItem getHost() {
        return host;
    }

    public MenuItem getPort() {
        return port;
    }

    public MenuItem getPlayerName() {
        return playerName;
    }

    public MenuItem getExit() {
        return exit;
    }

    public boolean isGameModeSelected() {
        return gameMode > 0;
    }

    public Menu setGameMode(int gameMode) {
        this.gameMode = gameMode;
        return this;
    }

    public int getGameMode() {
        return gameMode;
    }

    public String getMessage() {
        return message;
    }

    public Menu setMessage(String message) {
        this.message = message;
        return this;
    }

    public void changeActive(Direction direction) {
        if (!active.getNeighbours().containsKey(direction)) {
            return;
        }
        active.setActive(false);
        active = active.getNeighbours().get(direction);
        active.setActive(true);
    }

    public MenuItem getActive() {
        return active;
    }

}
