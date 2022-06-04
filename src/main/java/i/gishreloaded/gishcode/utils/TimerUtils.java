package i.gishreloaded.gishcode.utils;

public class TimerUtils {
	private long lastMS = 0L;
	private long prevMS = 0L;
	private long nanoTime = -1L;
	
	public boolean isDelay(long delay) {
		if(System.currentTimeMillis() - lastMS >= delay) {
			return true;
		}
		return false;
	}

	public boolean hasTimeElapsed(long time, boolean reset) {
		if(time < 150) {
			if (((double)getTime()) >= ((double)time) / 1.63d) {
				if (reset) {
					reset();
				}
				return true;
			}
		} else {
			if (getTime() >= time) {
				if (reset) {
					reset();
				}
				return true;
			}
		}

		return false;
	}
	
    public long getCurrentMS(){
		return System.nanoTime() / 1000000L;
	}
	
    public void setLastMS(long lastMS) {
		this.lastMS = lastMS;
	}
    public void setLastMS() {
		this.lastMS = System.currentTimeMillis();
	}
	
    public int convertToMS(int d) {
		return 1000 /d;
	}

	// All setters
	public void setTicks(long ticks) { nanoTime = System.nanoTime() - convertTicksToNano(ticks); }
	public void setNano(long time) { nanoTime = System.nanoTime() - time; }
	public void setMicro(long time) { nanoTime = System.nanoTime() - convertMicroToNano(time); }
	public void setMillis(long time) { nanoTime = System.nanoTime() - convertMillisToNano(time); }
	public void setSec(long time) { nanoTime = System.nanoTime() - convertSecToNano(time); }

	// All getters
	public long getTicks() { return convertNanoToTicks(nanoTime); }
	public long getNano() { return nanoTime; }
	public long getMicro() { return convertNanoToMicro(nanoTime); }
	public long getMillis() { return convertNanoToMillis(nanoTime); }
	public long getSec() { return convertNanoToSec(nanoTime); }

	// All passed
	public boolean passedTicks(long ticks) { return passedNano(convertTicksToNano(ticks)); }
	public boolean passedNano(long time) { return System.nanoTime() - nanoTime >= time; }
	public boolean passedMicro(long time) { return passedNano(convertMicroToNano(time)); }
	public boolean passedMillis(long time) { return passedNano(convertMillisToNano(time)); }
	public boolean passedSec(long time) { return passedNano(convertSecToNano(time)); }
    public boolean hasReached(float f){return (float) (getCurrentMS() - this.lastMS) >= f;}

    public void reset(){
		this.lastMS = getCurrentMS();
		this.nanoTime = System.nanoTime();
	}

	public void resetTimeSkipTo(long ms) {
		this.lastMS = getCurrentMS() + ms;
		this.nanoTime = System.nanoTime();
	}
	
    public boolean delay(float milliSec){
		return (float)(getTime() - this.prevMS) >= milliSec;
	}
	
    public long getTime(){
		return System.nanoTime() / 1000000L;
	}

	// Tick Conversions
	public long convertMillisToTicks(long time) { return time / 50; }
	public long convertTicksToMillis(long ticks) { return ticks * 50; }
	public long convertNanoToTicks(long time) { return convertMillisToTicks(convertNanoToMillis(time)); }
	public long convertTicksToNano(long ticks) { return convertMillisToNano(convertTicksToMillis(ticks)); }

	// All Conversions To Smaller
	public long convertSecToMillis(long time) { return time * 1000L; }
	public long convertSecToMicro(long time) { return convertMillisToMicro(convertSecToMillis(time)); }
	public long convertSecToNano(long time) { return convertMicroToNano(convertMillisToMicro(convertSecToMillis(time))); }
	public long convertMillisToMicro(long time) { return time * 1000L; }
	public long convertMillisToNano(long time) { return convertMicroToNano(convertMillisToMicro(time)); }
	public long convertMicroToNano(long time) { return time * 1000L; }

	// All Conversions To Larger
	public long convertNanoToMicro(long time) { return time / 1000L; }
	public long convertNanoToMillis(long time) { return convertMicroToMillis(convertNanoToMicro(time)); }
	public long convertNanoToSec(long time) { return convertMillisToSec(convertMicroToMillis(convertNanoToMicro(time))); }
	public long convertMicroToMillis(long time) { return time / 1000L; }
	public long convertMicroToSec(long time) { return convertMillisToSec(convertMicroToMillis(time)); }
	public long convertMillisToSec(long time) { return time / 1000L; }
}
