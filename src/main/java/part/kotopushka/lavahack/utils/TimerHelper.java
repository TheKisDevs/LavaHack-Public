package part.kotopushka.lavahack.utils;


public class TimerHelper
        implements Helper {
    private long ms = this.getCurrentMS();

    private long getCurrentMS() {
        return System.currentTimeMillis();
    }

    public boolean hasReached(double milliseconds) {
        return (double)(this.getCurrentMS() - this.ms) > milliseconds;
    }

    public void reset() {
        this.ms = this.getCurrentMS();
    }

    public long getTime() {
        return this.getCurrentMS() - this.ms;
    }
}

