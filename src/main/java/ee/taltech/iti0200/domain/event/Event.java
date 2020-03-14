package ee.taltech.iti0200.domain.event;

abstract public class Event {

    private boolean isCancelled = false;

    public boolean isCancelled() {
        return isCancelled;
    }

    public void cancel() {
        isCancelled = true;
    }

}
