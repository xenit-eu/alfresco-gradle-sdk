package eu.xenit.gradle.alfrescosdk.internal.tasks;

import eu.xenit.gradle.alfrescosdk.tasks.AmpSourceSet;
import org.gradle.api.NonNullApi;
import org.gradle.api.Project;
import org.gradle.api.internal.file.SourceDirectorySetFactory;
import org.gradle.api.tasks.SourceSet;

@NonNullApi
public class DefaultAmpSourceSet implements AmpSourceSet {
    private final SourceSet parentSourceSet;
    private final DefaultAmpSourceSetConfiguration amp;

    public DefaultAmpSourceSet(SourceSet parentSourceSet, Project project, SourceDirectorySetFactory sourceDirectorySetFactory) {
        this.parentSourceSet = parentSourceSet;
        amp = new DefaultAmpSourceSetConfiguration(project, sourceDirectorySetFactory);
    }

    @Override
    public DefaultAmpSourceSetConfiguration getAmp() {
        return amp;
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
    public String getName() {
        return parentSourceSet.getName();
    }
}
