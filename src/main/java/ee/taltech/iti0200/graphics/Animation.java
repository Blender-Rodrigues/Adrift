package ee.taltech.iti0200.graphics;

import java.io.IOException;

public class Animation {

    private Texture[] frames;
    private long lastTick;
    private int pointer;
    private int delay;

    public Animation(int amount, String directory, String filename, int delay) throws IOException {
        this.pointer = 0;
        this.delay = delay;

        this.frames = new Texture[amount];
        for (int i = 0; i < amount; i++) {
            this.frames[i] = new Texture(directory, filename + "_" + i);
        }
    }

    public void bind(long tick) {
        if (tick % delay == 0 && tick > lastTick) {
            lastTick = tick;
            pointer++;
        }
        if (pointer >= frames.length) {
            pointer = 0;
        }

        frames[pointer].bind(0);
    }

}
