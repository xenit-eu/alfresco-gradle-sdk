package eu.xenit.gradle.alfrescosdk.internal.tasks;

import eu.xenit.gradle.alfrescosdk.internal.ConfigurationDispatcher;
import eu.xenit.gradle.alfrescosdk.tasks.AmpSourceSet;
import eu.xenit.gradle.alfrescosdk.tasks.AmpSourceSetConfiguration;
import org.gradle.api.Action;
import org.gradle.api.NonNullApi;
import org.gradle.api.Project;
import org.gradle.api.internal.file.SourceDirectorySetFactory;
import org.gradle.api.tasks.SourceSet;

@NonNullApi
public class DefaultAmpSourceSet implements AmpSourceSet {
    private final SourceSet parentSourceSet;
    private final DefaultAmpSourceSetConfiguration amp;
    private final ConfigurationDispatcher<DefaultAmpSourceSet> configurationDispatcher;

    public DefaultAmpSourceSet(SourceSet parentSourceSet, Project project,
            ConfigurationDispatcher<DefaultAmpSourceSet> configurationDispatcher) {
        this.parentSourceSet = parentSourceSet;
        this.configurationDispatcher = configurationDispatcher;
        amp = project.getObjects().newInstance(DefaultAmpSourceSetConfiguration.class, project);
    }

    @Override
    public DefaultAmpSourceSetConfiguration getAmp() {
        return amp;
    }

    @Override
    public AmpSourceSet amp(Action<? super AmpSourceSetConfiguration> configureAction) {
        configureAction.execute(getAmp());
        configurationDispatcher.add(this);
        return this;
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
