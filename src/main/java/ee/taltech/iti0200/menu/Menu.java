package ee.taltech.iti0200.menu;

public class Menu {

    private int gameMode = 0;
    private String message = "";
    private String host = "localhost";
    private int port = 8880;
    private String playerName = "000";

    public Menu(String[] args) {
        if (args.length >= 2) {
            host = args[1];
        }
        if (args.length >= 3) {
            port = Integer.parseInt(args[2]);
        }
        if (args.length >= 4) {
            playerName = args[3];
        }
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

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getPlayerName() {
        return playerName;
    }

    public String getMessage() {
        return message;
    }

    public Menu setMessage(String message) {
        this.message = message;
        return this;
    }

}
