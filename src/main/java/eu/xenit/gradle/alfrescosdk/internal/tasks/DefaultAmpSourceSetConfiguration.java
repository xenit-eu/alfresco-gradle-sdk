package eu.xenit.gradle.alfrescosdk.internal.tasks;

import eu.xenit.gradle.alfrescosdk.tasks.AmpSourceSetConfiguration;
import lombok.Getter;
import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.api.file.SourceDirectorySet;
import org.gradle.api.internal.file.SourceDirectorySetFactory;
import org.gradle.api.provider.Property;
import org.gradle.api.provider.Provider;
import org.gradle.util.GUtil;

import java.io.File;
import java.util.Properties;

public class DefaultAmpSourceSetConfiguration implements AmpSourceSetConfiguration {

    @Getter private final SourceDirectorySet config;
    @Getter private final SourceDirectorySet web;
    private final Project project;
    @Getter private Property<Properties> moduleProperties;
    @Getter private Property<Properties> fileMappingProperties;
    private Property<Boolean> dynamicExtension;

    public DefaultAmpSourceSetConfiguration(Project project, SourceDirectorySetFactory sourceDirectorySetFactory) {
        this.project = project;

        // Creates config sourceDir set.
//        config = new DefaultAmpSourceDirectories(sourceDirectorySetFactory.create("config"));
        config = AmpConfigSourceDirectorySet.create("config", project.getObjects());

        //Creates web sourceSir set.
        web =  AmpWebSourceDirectorySet.create("web", project.getObjects());

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


    @Override
    public AmpSourceSetConfiguration dynamicExtension(boolean dynamicExtension) {
        this.dynamicExtension.set(dynamicExtension);
        return this;
    }

    public Provider<Boolean> getDynamicExtension() {
        return dynamicExtension;
    }

}
