package ee.taltech.iti0200.domain;

public class ScoreData {

    private int kills;
    private int deaths;

    public ScoreData() {
        kills = 0;
        deaths = 0;
    }

    public void addKill() {
        kills++;
    }

    public void addDeath() {
        deaths++;
    }

    public int getKills() {
        return kills;
    }

    public int getDeaths() {
        return deaths;
    }

}
