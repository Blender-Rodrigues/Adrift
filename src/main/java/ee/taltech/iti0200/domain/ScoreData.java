package ee.taltech.iti0200.domain;

import java.io.Serializable;

public class ScoreData implements Serializable {

    private static final long serialVersionUID = 1L;

    private int kills = 0;
    private int deaths = 0;

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

    public ScoreData setKills(int kills) {
        this.kills = kills;
        return this;
    }

    public ScoreData setDeaths(int deaths) {
        this.deaths = deaths;
        return this;
    }

}
