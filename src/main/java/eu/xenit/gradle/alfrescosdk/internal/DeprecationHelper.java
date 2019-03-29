package eu.xenit.gradle.alfrescosdk.internal;

import java.util.HashSet;
import java.util.Set;
import org.gradle.api.logging.Logger;

public final class DeprecationHelper {
    private static final boolean enableDeprecations = Boolean.parseBoolean(System.getProperty("eu.xenit.gradle.alfrescosdk.deprecation", "true"));
    private DeprecationHelper() {

    }

    public static String getStackTrace(int start) {
        StackTraceElement[] stackTrace = new Exception().getStackTrace();

        StringBuilder stringBuilder = new StringBuilder();
        for (int i = start + 1; i < stackTrace.length; i++) {
            stringBuilder.append(stackTrace[i]).append("\n");
        }
        return stringBuilder.toString();
    }

    private static final Set<Logger> warnedLoggers =  new HashSet<>();

    public static void warnDeprecation(Logger logger, String message, int skipStack) {
        if(!enableDeprecations) {
            return;
        }
        String fullMessage = message;
        if(logger.isInfoEnabled()) {
            fullMessage +="\n"+getStackTrace(skipStack + 1);
        }

        logger.warn(fullMessage);
    }

    public static void warnDeprecation(Logger logger, String message) {
        warnDeprecation(logger, message, 1);
    }

    public static void warnDeprecationOnce(Logger logger, String message, int skipStack) {
        if(!logger.isInfoEnabled()) {
            if(warnedLoggers.contains(logger)) {
                return;
            }
            warnedLoggers.add(logger);
        }
        warnDeprecation(logger, message, skipStack+1);
    }

    public static void warnDeprecationOnce(Logger logger, String message) {
        warnDeprecationOnce(logger, message, 1);
    }

}
