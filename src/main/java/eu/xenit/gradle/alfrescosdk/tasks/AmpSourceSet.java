package eu.xenit.gradle.alfrescosdk.tasks;

import groovy.lang.Closure;
import org.gradle.api.Action;
import org.gradle.api.Named;
import org.gradle.api.NonNullApi;
import org.gradle.api.tasks.SourceSet;
import org.gradle.util.ConfigureUtil;

@NonNullApi
public interface AmpSourceSet extends Named {
    SourceSet getSourceSet();
    String getModulePropertiesTaskName();
    String getFileMappingPropertiesTaskName();
    String getAmpTaskName();
    String getAmpConfigurationName();
    String getAmpLibrariesConfigurationName();
    String getJarTaskName();
}
