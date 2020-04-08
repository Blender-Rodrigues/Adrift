package ee.taltech.iti0200.graphics;

public class TextBox {

    private int xDistance;
    private int yDistance;
    private String text;
    private float size;

    public TextBox(int xDistance, int yDistance, String text, float size) {
        this.xDistance = xDistance;
        this.yDistance = yDistance;
        this.text = text;
        this.size = size;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void render(Alphabet alphabet, Camera camera) {
        for (int i = 0; i < text.length(); i++) {
            alphabet.render(text.charAt(i), camera, (int) (xDistance + i * size), yDistance, size);
        }
    }
}
