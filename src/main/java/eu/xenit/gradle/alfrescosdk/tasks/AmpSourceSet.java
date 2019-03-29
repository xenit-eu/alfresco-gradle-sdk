package eu.xenit.gradle.alfrescosdk.tasks;

import groovy.lang.Closure;
import org.gradle.api.Action;
import org.gradle.api.Named;
import org.gradle.api.NonNullApi;
import org.gradle.util.ConfigureUtil;

@NonNullApi
public interface AmpSourceSet extends Named {
    AmpSourceSetConfiguration getAmp();

    default AmpSourceSet amp(Action<? super AmpSourceSetConfiguration> configureAction) {
        configureAction.execute(getAmp());
        return this;
    }

    default AmpSourceSet amp(Closure configureClosure) {
        return amp(ConfigureUtil.configureUsing(configureClosure));
    }

    String getModulePropertiesTaskName();
    String getFileMappingPropertiesTaskName();
    String getAmpTaskName();
    String getAmpConfigurationName();
    String getAmpLibrariesConfigurationName();
}
