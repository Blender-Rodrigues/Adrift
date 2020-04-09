package ee.taltech.iti0200.graphics;

import java.io.IOException;

public class VisualFactory {

    public Animation create(int amount, String directory, String filename, int delay) throws IOException {
        return new Animation(amount, directory, filename, delay);
    }

    public Texture create(String directory, String filename) throws IOException {
        return new Texture(directory, filename);
    }

}
