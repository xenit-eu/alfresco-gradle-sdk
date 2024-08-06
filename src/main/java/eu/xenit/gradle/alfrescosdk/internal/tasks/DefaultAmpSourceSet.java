package eu.xenit.gradle.alfrescosdk.internal.tasks;

import eu.xenit.gradle.alfrescosdk.tasks.AmpSourceSet;
import org.gradle.api.NonNullApi;
import org.gradle.api.tasks.SourceSet;

@NonNullApi
public class DefaultAmpSourceSet implements AmpSourceSet {
    private final SourceSet parentSourceSet;

    public DefaultAmpSourceSet(SourceSet parentSourceSet) {
        this.parentSourceSet = parentSourceSet;
    }

    @Override
    public SourceSet getSourceSet() {
        return parentSourceSet;
    }

    @Override
    public String getModulePropertiesTaskName() {
        return parentSourceSet.getTaskName("process", "moduleProperties");
    }

    @Override
    public String getFileMappingPropertiesTaskName() {
        return parentSourceSet.getTaskName("process", "fileMappingProperties");
    }

    @Override
    public String getAmpTaskName() {
        return parentSourceSet.getTaskName(null, "amp");
    }

    @Override
    public String getAmpConfigurationName() {
        return getAmpTaskName();
    }

    @Override
    public String getAmpLibrariesConfigurationName() {
        return parentSourceSet.getRuntimeClasspathConfigurationName();
    }

    @Override
    public String getJarTaskName() {
        return parentSourceSet.getJarTaskName();
    }

    @Override
    public String getName() {
        return parentSourceSet.getName();
    }
}
