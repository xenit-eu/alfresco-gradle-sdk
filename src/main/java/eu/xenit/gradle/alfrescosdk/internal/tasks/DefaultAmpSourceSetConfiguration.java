package eu.xenit.gradle.alfrescosdk.internal.tasks;

import eu.xenit.gradle.alfrescosdk.tasks.AmpSourceDirectories;
import eu.xenit.gradle.alfrescosdk.tasks.AmpSourceSetConfiguration;
import java.io.File;
import java.util.Properties;
import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.api.internal.file.SourceDirectorySetFactory;
import org.gradle.api.provider.Property;
import org.gradle.api.provider.Provider;
import org.gradle.util.GUtil;

public class DefaultAmpSourceSetConfiguration implements AmpSourceSetConfiguration {

    private final DefaultAmpSourceDirectories config;
    private final DefaultAmpSourceDirectories web;
    private final Project project;
    private Property<Properties> moduleProperties;
    private Property<Properties> fileMappingProperties;
    private Property<Boolean> dynamicExtension;

    public DefaultAmpSourceSetConfiguration(Project project, SourceDirectorySetFactory sourceDirectorySetFactory) {
        this.project = project;
        config = new DefaultAmpSourceDirectories(sourceDirectorySetFactory.create("config"));
        web = new DefaultAmpSourceDirectories(sourceDirectorySetFactory.create("web"));
        moduleProperties = project.getObjects().property(Properties.class);
        moduleProperties.set(new Properties());
        fileMappingProperties = project.getObjects().property(Properties.class);
        fileMappingProperties.set(new Properties());
        dynamicExtension = project.getObjects().property(Boolean.class);
        dynamicExtension.set(false);
    }

    @Override
    public AmpSourceSetConfiguration module(String moduleProperties) {
        module(project.file(moduleProperties));
        return this;
    }

    @Override
    public AmpSourceSetConfiguration module(File moduleProperties) {
        this.moduleProperties.set(project.provider(() -> GUtil.loadProperties(moduleProperties)));
        return this;
    }

    @Override
    public AmpSourceSetConfiguration module(Action<? super Properties> configure) {
        moduleProperties.set(project.provider(() -> {
            Properties newProperties = new Properties();
            configure.execute(newProperties);
            return newProperties;
        }));
        return this;
    }

    public Provider<Properties> getModuleProperties() {
        return moduleProperties;
    }

    @Override
    public AmpSourceSetConfiguration fileMapping(String fileMappingProperties) {
        fileMapping(project.file(fileMappingProperties));
        return this;
    }

    @Override
    public AmpSourceSetConfiguration fileMapping(File fileMappingProperties) {
        this.fileMappingProperties.set(project.provider(() -> GUtil.loadProperties(fileMappingProperties)));
        return this;
    }

    @Override
    public AmpSourceSetConfiguration fileMapping(Action<? super Properties> configure) {
        fileMappingProperties.set(project.provider(() -> {
            Properties newProperties = new Properties();
            configure.execute(newProperties);
            return newProperties;
        }));
        return this;
    }

    public Provider<Properties> getFileMappingProperties() {
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

    @Override
    public AmpSourceSetConfiguration dynamicExtension(boolean dynamicExtension) {
        this.dynamicExtension.set(dynamicExtension);
        return this;
    }

    public Provider<Boolean> getDynamicExtension() {
        return dynamicExtension;
    }

}
