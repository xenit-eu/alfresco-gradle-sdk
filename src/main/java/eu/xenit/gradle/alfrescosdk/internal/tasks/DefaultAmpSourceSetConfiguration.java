package eu.xenit.gradle.alfrescosdk.internal.tasks;

import eu.xenit.gradle.alfrescosdk.internal.PropertiesUtil;
import eu.xenit.gradle.alfrescosdk.tasks.AmpSourceSet;
import eu.xenit.gradle.alfrescosdk.tasks.AmpSourceSetConfiguration;
import java.io.File;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.api.file.SourceDirectorySet;
import org.gradle.api.provider.MapProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.SourceSet;

public class DefaultAmpSourceSetConfiguration implements AmpSourceSetConfiguration {

    private final SourceSet sourceSet;
    private final SourceDirectorySet config;
    private final SourceDirectorySet web;
    private final Project project;
    private final MapProperty<String, String> moduleProperties;
    private final MapProperty<String, String> fileMappingProperties;
    private final Property<Boolean> dynamicExtension;

    public DefaultAmpSourceSetConfiguration(Project project, SourceSet sourceSet) {
        this.project = project;
        this.sourceSet = sourceSet;

        // Creates config sourceDir set.
        config = project.getObjects().sourceDirectorySet("config", "Alfresco AMP configuration");

        //Creates web sourceSir set.
        web = project.getObjects().sourceDirectorySet("web", "Alfresco AMP web");

        //alfresco module.properties file
        moduleProperties = project.getObjects().mapProperty(String.class, String.class).empty();

        //file-mapping properties
        fileMappingProperties = project.getObjects().mapProperty(String.class, String.class).empty();

        //dynamic extension related options.
        dynamicExtension = project.getObjects().property(Boolean.class);
        dynamicExtension.set(false);
    }

    @Override
    public AmpSourceSet getSourceSet() {
        return new DefaultAmpSourceSet(sourceSet);
    }

    @Override
    public AmpSourceSetConfiguration module(String moduleProperties) {
        module(project.file(moduleProperties));
        return this;
    }

    @Override
    public AmpSourceSetConfiguration module(File moduleProperties) {
        return module(properties -> {
            properties.putAll(PropertiesUtil.loadProperties(moduleProperties));
        });
    }

    @Override
    public AmpSourceSetConfiguration module(Action<? super Properties> configure) {
        moduleProperties.set(project.provider(() -> {
            Properties newProperties = new Properties();
            configure.execute(newProperties);
            return Collections.checkedMap((Map<String, String>) (Map) newProperties, String.class, String.class);
        }));
        return this;
    }

    public MapProperty<String, String> getModuleProperties() {
        return moduleProperties;
    }

    @Override
    public AmpSourceSetConfiguration fileMapping(String fileMappingProperties) {
        fileMapping(project.file(fileMappingProperties));
        return this;
    }

    @Override
    public AmpSourceSetConfiguration fileMapping(File fileMappingProperties) {
        return fileMapping(properties -> {
            properties.putAll(PropertiesUtil.loadProperties(fileMappingProperties));
        });
    }

    @Override
    public AmpSourceSetConfiguration fileMapping(Action<? super Properties> configure) {
        fileMappingProperties.set(project.provider(() -> {
            Properties newProperties = new Properties();
            configure.execute(newProperties);
            return Collections.checkedMap((Map<String, String>) (Map) newProperties, String.class, String.class);
        }));
        return this;
    }


    public MapProperty<String, String> getFileMappingProperties() {
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
