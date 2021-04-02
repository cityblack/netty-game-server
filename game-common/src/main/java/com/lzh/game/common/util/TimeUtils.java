package com.lzh.game.common.util;

public class TimeUtils {

    public static final String FIVE_CLOCK_CRON = "0 0 5 * * ?";

    public static long now() {
        return System.currentTimeMillis();
    }

    private TimeUtils() {}
}
