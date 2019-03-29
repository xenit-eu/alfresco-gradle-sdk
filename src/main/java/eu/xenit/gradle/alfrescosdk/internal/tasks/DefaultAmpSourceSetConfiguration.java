package eu.xenit.gradle.alfrescosdk.internal.tasks;

import eu.xenit.gradle.alfrescosdk.tasks.AmpSourceDirectories;
import eu.xenit.gradle.alfrescosdk.tasks.AmpSourceSetConfiguration;
import java.io.File;
import java.util.Properties;
import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.api.internal.file.SourceDirectorySetFactory;
import org.gradle.util.GUtil;

public class DefaultAmpSourceSetConfiguration implements AmpSourceSetConfiguration {

    private final DefaultAmpSourceDirectories config;
    private final DefaultAmpSourceDirectories web;
    private final Project project;
    private Properties moduleProperties = new Properties();
    private Properties fileMappingProperties = new Properties();

    public DefaultAmpSourceSetConfiguration(Project project, SourceDirectorySetFactory sourceDirectorySetFactory) {
        this.project = project;
        config = new DefaultAmpSourceDirectories(sourceDirectorySetFactory.create("config"));
        web = new DefaultAmpSourceDirectories(sourceDirectorySetFactory.create("web"));
    }

    @Override
    public AmpSourceSetConfiguration module(Object moduleProperties) {
        module(project.file(moduleProperties));
        return this;
    }

    @Override
    public AmpSourceSetConfiguration module(File moduleProperties) {
        this.moduleProperties = GUtil.loadProperties(moduleProperties);
        return this;
    }

    @Override
    public AmpSourceSetConfiguration module(Action<? super Properties> configure) {
        configure.execute(moduleProperties);
        return this;
    }

    public Properties getModuleProperties() {
        return moduleProperties;
    }

    @Override
    public AmpSourceSetConfiguration fileMapping(Object fileMappingProperties) {
        fileMapping(project.file(fileMappingProperties));
        return this;
    }

    @Override
    public AmpSourceSetConfiguration fileMapping(File fileMappingProperties) {
        this.fileMappingProperties = GUtil.loadProperties(fileMappingProperties);
        return null;
    }

    @Override
    public AmpSourceSetConfiguration fileMapping(Action<? super Properties> configure) {
        configure.execute(fileMappingProperties);
        return this;
    }

    public Properties getFileMappingProperties() {
        return fileMappingProperties;
    }

    @Override
    public AmpSourceDirectories getConfig() {
        return config;
    }

    @Override
    public AmpSourceDirectories getWeb() {
        return web;
    }

}
