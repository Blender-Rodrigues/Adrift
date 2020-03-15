package ee.taltech.iti0200.domain.event;

abstract public class Event {

    private boolean isStopped = false;

    public boolean isStopped() {
        return isStopped;
    }

    public void stop() {
        isStopped = true;
    }

}
