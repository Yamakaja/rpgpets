package me.yamakaja.rpgpets.api.logging;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * Created by Yamakaja on 23.06.17.
 */
public class ErrorLogHandler extends Handler {

    private SentryManager sentryManager;

    public ErrorLogHandler(SentryManager sentryManager) {
        this.sentryManager = sentryManager;
    }

    @SuppressWarnings("ThrowableNotThrown")
    @Override
    public void publish(LogRecord record) {
        if (record.getThrown() == null)
            return;

        if (!sentryManager.isActive())
            return;

        Throwable throwable = record.getThrown();
        boolean flag = false;

        while (throwable != null) {
            for (StackTraceElement stackTraceElement : throwable.getStackTrace()) {
                if (stackTraceElement.getClassName().startsWith("me.yamakaja")) {
                    flag = true;
                    break;
                }
            }
            throwable = throwable.getCause();
        }

        if (!flag)
            return;

        sentryManager.logException(record.getThrown());
    }

    @Override
    public void flush() {

    }

    @Override
    public void close() throws SecurityException {

    }

    @Override
    public boolean isLoggable(LogRecord record) {
        return true;
    }

    @Override
    public Level getLevel() {
        return Level.ALL;
    }

}
