package eu.xenit.gradle.alfrescosdk.internal.tasks;

import eu.xenit.gradle.alfrescosdk.tasks.AmpSourceSetConfiguration;
import java.io.File;
import java.util.Properties;
import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.api.file.SourceDirectorySet;
import org.gradle.api.provider.Property;
import org.gradle.api.provider.Provider;
import org.gradle.util.GUtil;

public class DefaultAmpSourceSetConfiguration implements AmpSourceSetConfiguration {

    private final SourceDirectorySet config;
    private final SourceDirectorySet web;
    private final Project project;
    private final Property<Properties> moduleProperties;
    private final Property<Properties> fileMappingProperties;
    private final Property<Boolean> dynamicExtension;

    public DefaultAmpSourceSetConfiguration(Project project) {
        this.project = project;

        // Creates config sourceDir set.
        config = project.getObjects().sourceDirectorySet("config", "Alfresco AMP configuration");

        //Creates web sourceSir set.
        web = project.getObjects().sourceDirectorySet("web", "Alfresco AMP web");

        //alfresco module.properties file
        moduleProperties = project.getObjects().property(Properties.class);
        moduleProperties.set(new Properties());

        //file-mapping properties
        fileMappingProperties = project.getObjects().property(Properties.class);
        fileMappingProperties.set(new Properties());

        //dynamic extension related options.
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
    public AmpSourceSetConfiguration dynamicExtension(boolean dynamicExtension) {
        this.dynamicExtension.set(dynamicExtension);
        return this;
    }

    public Provider<Boolean> getDynamicExtension() {
        return dynamicExtension;
    }

    @Override
    public SourceDirectorySet getConfig() {
        return config;
    }

    @Override
    public SourceDirectorySet getWeb() {
        return web;
    }
}
