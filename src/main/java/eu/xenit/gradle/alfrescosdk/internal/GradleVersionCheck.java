package eu.xenit.gradle.alfrescosdk.internal;

import org.gradle.api.GradleException;
import org.gradle.util.GradleVersion;

public final class GradleVersionCheck {
    private GradleVersionCheck() {

    }
    private static final GradleVersion MINIMUM_VERSION = GradleVersion.version("4.10");
    public static void assertSupportedVersion(String pluginId) {
        GradleVersion currentVersion = GradleVersion.current();

        if(MINIMUM_VERSION.compareTo(currentVersion) > 0) {
            throw new GradleException("The "+pluginId+" plugin requires at least "+MINIMUM_VERSION+". (You are running "+currentVersion+")");
        }

    }

}
