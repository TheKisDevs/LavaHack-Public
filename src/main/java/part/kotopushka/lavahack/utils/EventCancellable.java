package part.kotopushka.lavahack.utils;



public abstract class EventCancellable
        implements Event,
        Cancellable {
    private boolean cancelled;

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean state) {
        this.cancelled = state;
    }
}

