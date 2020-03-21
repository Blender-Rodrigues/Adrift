package ee.taltech.iti0200.graphics;


public class Animation {
    private Texture[] frames;
    private int pointer;

    public Animation(int amount, String filename) {
        this.pointer = 0;

        this.frames = new Texture[amount];
        for (int i = 0; i < amount; i++) {
            this.frames[i] = new Texture("animations/" + filename + "_" + i + ".png");

        }
    }

    public void bind(long tick) {
        if (tick % 20 == 0) pointer++;
        if (pointer >= frames.length) pointer = 0;

        frames[pointer].bind(0);
    }

}
