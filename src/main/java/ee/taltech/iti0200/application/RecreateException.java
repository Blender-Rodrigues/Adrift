package ee.taltech.iti0200.application;

public class RecreateException extends RuntimeException{

    public final int delay;

    /**
     * @param delay Signal a delay for the restart in milliseconds
     */
    public RecreateException(int delay) {
        this.delay = delay;
    }

}
