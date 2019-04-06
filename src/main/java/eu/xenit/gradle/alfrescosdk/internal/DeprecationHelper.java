package eu.xenit.gradle.alfrescosdk.internal;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.gradle.api.logging.Logger;

public final class DeprecationHelper {
    private static final String PROP_DEPRECATION = "eu.xenit.gradle.alfrescosdk.deprecation";
    private static final String PROP_STACKTRACE = PROP_DEPRECATION+".stacktrace";
    private static final String PROP_WARN_ALWAYS = PROP_DEPRECATION+".warnAlways";
    private static final boolean enableDeprecations = Boolean.parseBoolean(System.getProperty(PROP_DEPRECATION, "true"));
    private DeprecationHelper() {

    }

    private static boolean isEnableStacktraces(Logger logger) {
        String enableStacktracesProperty = System.getProperty(PROP_STACKTRACE);
        if(enableStacktracesProperty == null) {
            return logger.isInfoEnabled();
        }

        return Boolean.parseBoolean(enableStacktracesProperty);
    }

    private static boolean isWarnAlways(Logger logger) {
        if(isEnableStacktraces(logger)) {
            return true;
        }
        return Boolean.parseBoolean(System.getProperty(PROP_WARN_ALWAYS, "false"));
    }

    public static String getStackTrace(int start) {
        StackTraceElement[] stackTrace = new Exception().getStackTrace();

        StringBuilder stringBuilder = new StringBuilder();
        for (int i = start + 1; i < stackTrace.length; i++) {
            stringBuilder.append(stackTrace[i]).append("\n");
        }
        return stringBuilder.toString();
    }

    private static final Map<Logger, Set<String>> warnedLoggers =  new HashMap<>();

    public static void warnDeprecation(Logger logger, String message, int skipStack) {
        if(!enableDeprecations) {
            return;
        }
        String fullMessage = message;
        if(isEnableStacktraces(logger)) {
            fullMessage +="\n"+getStackTrace(skipStack + 1);
        }

        logger.warn(fullMessage);
    }

    public static void warnDeprecation(Logger logger, String message) {
        warnDeprecation(logger, message, 1);
    }

    public static void warnDeprecationOnce(Logger logger, String message, int skipStack) {
        if(!isWarnAlways(logger)) {
            if(!warnedLoggers.containsKey(logger)) {
                warnedLoggers.put(logger, new HashSet<>());
            }
            Set<String> warnedStrings = warnedLoggers.get(logger);
            if(warnedStrings.contains(message)) {
                return;
            }
            warnedStrings.add(message);
        }
        warnDeprecation(logger, message, skipStack+1);
    }

    public static void warnDeprecationOnce(Logger logger, String message) {
        warnDeprecationOnce(logger, message, 1);
    }

}
