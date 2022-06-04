package com.kisman.cc.catlua.common.util;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class StopWatch {
    long time = -1L;

    public boolean passed(double time, TimeUnit unit) {
        return unit.convert(Duration.ofMillis(System.currentTimeMillis() - this.time).toMillis(), unit) >= time;
    }

    public boolean passed(long time) {
        return System.currentTimeMillis() - this.time >= time;
    }

    public void reset() {
        time = System.currentTimeMillis();
    }

}
