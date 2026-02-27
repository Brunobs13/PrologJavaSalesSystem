package com.bruno.salessystem.util;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public final class AppLogger {
    public enum Level {
        INFO,
        ERROR,
        DEBUG
    }

    private final String component;
    private final boolean debugEnabled;

    private AppLogger(String component, boolean debugEnabled) {
        this.component = component;
        this.debugEnabled = debugEnabled;
    }

    public static AppLogger getLogger(String component) {
        boolean debug = "true".equalsIgnoreCase(System.getenv("DEBUG"));
        return new AppLogger(component, debug);
    }

    public void info(String message) {
        log(Level.INFO, message);
    }

    public void debug(String message) {
        if (debugEnabled) {
            log(Level.DEBUG, message);
        }
    }

    public void error(String message) {
        log(Level.ERROR, message);
    }

    private void log(Level level, String message) {
        String payload = String.format(
            "{\"ts\":\"%s\",\"level\":\"%s\",\"component\":\"%s\",\"msg\":\"%s\"}",
            nowUtc(),
            level,
            JsonUtil.escape(component),
            JsonUtil.escape(message)
        );

        if (level == Level.ERROR) {
            System.err.println(payload);
            return;
        }

        System.out.println(payload);
    }

    private static String nowUtc() {
        return OffsetDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }
}
