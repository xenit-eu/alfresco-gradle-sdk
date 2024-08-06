package eu.xenit.gradle.alfrescosdk;

import eu.xenit.gradle.alfrescosdk.internal.GradleVersionCheck;
import eu.xenit.gradle.alfrescosdk.internal.tasks.DefaultAmpSourceSet;
import eu.xenit.gradle.alfrescosdk.internal.tasks.DefaultAmpSourceSetConfiguration;
import eu.xenit.gradle.alfrescosdk.tasks.Amp;
import eu.xenit.gradle.alfrescosdk.tasks.AmpSourceSetConfiguration;
import java.io.File;
import java.util.Map;
import org.gradle.api.Action;
import org.gradle.api.NamedDomainObjectProvider;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.file.FileCollection;
import org.gradle.api.internal.plugins.DslObject;
import org.gradle.api.logging.Logging;
import org.gradle.api.plugins.JavaBasePlugin;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.SourceSetContainer;
import org.gradle.api.tasks.TaskProvider;
import org.gradle.api.tasks.WriteProperties;
import org.gradle.language.base.plugins.LifecycleBasePlugin;
import org.slf4j.Logger;

public class AmpBasePlugin implements Plugin<Project> {

    public static final Logger LOGGER = Logging.getLogger(AmpBasePlugin.class);
    public static final String PLUGIN_ID = "eu.xenit.amp-base";

    private Project project;

    @Override
    public void apply(Project target) {
        GradleVersionCheck.assertSupportedVersion(PLUGIN_ID);
        project = target;
        project.getPluginManager().apply(JavaBasePlugin.class);
        configureSourceSetDefaults();
    }

    private void configureSourceSetDefaults() {
        project.getExtensions().getByType(JavaPluginExtension.class).getSourceSets().all(sourceSet -> {
            var ampSourceSetConfig = (DefaultAmpSourceSetConfiguration)new DslObject(sourceSet).getExtensions()
                    .create(AmpSourceSetConfiguration.class, "amp", DefaultAmpSourceSetConfiguration.class, project, sourceSet);
            var ampSourceSet = new DefaultAmpSourceSet(sourceSet);
            String rootDir = "src/" + sourceSet.getName() + "/amp";

            //add default config sourceDir
            ampSourceSetConfig.getConfig().srcDir(rootDir + "/config");

            //add default web sourceDir
            ampSourceSetConfig.getWeb().srcDir(rootDir + "/web");

            //add module.properties
            File moduleProperties = project.file(rootDir + "/module.properties");

            if (moduleProperties.exists()) {
                ampSourceSetConfig.module(moduleProperties);
            } else {
                ampSourceSetConfig.module(properties -> {
                    LOGGER.info(
                            "{} does not exist for configured amp sourceset {}. A module.properties file is configured automatically from the project",
                            moduleProperties, sourceSet.getName());
                    String moduleId = project.getGroup().toString();
                    if (!moduleId.isEmpty()) {
                        moduleId += ".";
                    }
                    moduleId += project.getName();
                    String moduleVersion = project.getVersion().toString();
                    if (moduleVersion.equals(Project.DEFAULT_VERSION)) {
                        moduleVersion = "0.0.0";
                    }
                    properties.setProperty("module.id", moduleId);
                    properties.setProperty("module.version", moduleVersion);
                    properties.setProperty("module.title", project.getName());
                    if (project.getDescription() != null) {
                        properties.setProperty("module.description", project.getDescription());
                    }
                });
            }
            File fileMappingProperties = project.file(rootDir + "/file-mapping.properties");
            if (fileMappingProperties.exists()) {
                ampSourceSetConfig.fileMapping(fileMappingProperties);
            }

            createWritePropertiesTask(ampSourceSet.getModulePropertiesTaskName(), ampSourceSet.getName(),
                    "module.properties", ampSourceSetConfig.getModuleProperties());
            createWritePropertiesTask(ampSourceSet.getFileMappingPropertiesTaskName(), ampSourceSet.getName(),
                    "file-mapping.properties", ampSourceSetConfig.getFileMappingProperties());
            createAmpTask(project, ampSourceSet, ampSourceSetConfig);

        });

    }

    private Provider<WriteProperties> createWritePropertiesTask(String taskName, String sourceSetName, String fileName,
            Provider<Map<String, String>> properties) {
        return project.getTasks().register(taskName, WriteProperties.class, writeProperties -> {
            writeProperties.setDescription("Creates " + fileName + " for " + sourceSetName);
            writeProperties.setProperties((Map<String, Object>)(Map)properties.get());
            writeProperties.setOutputFile(project.getBuildDir().toPath().resolve(taskName).resolve(fileName).toFile());
        });
    }

    private TaskProvider<Amp> createAmpTask(Project project, DefaultAmpSourceSet ampSourceSet, DefaultAmpSourceSetConfiguration ampSourceSetConfig) {
        return project.getTasks().register(ampSourceSet.getAmpTaskName(), Amp.class, amp -> {
            amp.setModuleProperties(
                    () -> project.getTasks().getByName(ampSourceSet.getModulePropertiesTaskName()).getOutputs()
                            .getFiles().getSingleFile());
            amp.setFileMappingProperties(
                    () -> {
                        WriteProperties fileMappingTask = project.getTasks().withType(WriteProperties.class).getByName(ampSourceSet.getFileMappingPropertiesTaskName());
                        if(fileMappingTask.getProperties().isEmpty()) {
                            return null;
                        }
                        return fileMappingTask.getOutputs().getFiles().getSingleFile();
                    });
            amp.web(copySpec -> {
                copySpec.from(ampSourceSetConfig.getWeb());
            });
            amp.config(copySpec -> {
                copySpec.from(ampSourceSetConfig.getConfig());
            });

            Provider<Boolean> dynamicExtension = ampSourceSetConfig.getDynamicExtension();
            Provider<Configuration> ampLibrariesConfiguration = project.getConfigurations()
                    .named(ampSourceSet.getAmpLibrariesConfigurationName());
            Provider<FileCollection> jarOutputs = project.getTasks().named(ampSourceSet.getJarTaskName())
                    .map(t -> t.getOutputs().getFiles());
            // When not a dynamic extension, configure libs
            amp.getLibs().from(dynamicExtension.map(de -> de ? project.files() : ampLibrariesConfiguration.get()));
            amp.getLibs().from(dynamicExtension.map(de -> de ? project.files() : jarOutputs.get()));
            // When a dynamic extension, configure de bundles
            amp.getDeBundles().from(dynamicExtension.map(de -> de ? ampLibrariesConfiguration.get() : project.files()));
            amp.getDeBundles().from(dynamicExtension.map(de -> de ? jarOutputs.get() : project.files()));
            amp.dependsOn(
                    ampSourceSet.getJarTaskName(),
                    ampSourceSet.getFileMappingPropertiesTaskName(),
                    ampSourceSet.getModulePropertiesTaskName()
            );
            amp.setGroup(LifecycleBasePlugin.BUILD_GROUP);
            if (!ampSourceSet.getName().equals(SourceSet.MAIN_SOURCE_SET_NAME)) {
                amp.getArchiveClassifier().set(ampSourceSet.getName());
            }
        });
    }

    public void allAmpSourceSetConfigurations(Action<? super AmpSourceSetConfiguration> configure) {
        project.getExtensions().getByType(JavaPluginExtension.class).getSourceSets().all(sourceSet -> {
            var config = new DslObject(sourceSet).getExtensions().getByType(AmpSourceSetConfiguration.class);
            if (config != null) {
                configure.execute(config);
            }
        });
    }

    void configureAmpSourceSetConfiguration(String sourceSetName, Action<? super AmpSourceSetConfiguration> configure) {
        getSourceSet(sourceSetName)
                .configure(sourceSet -> {
                    AmpSourceSetConfiguration ampSourceSet = new DslObject(sourceSet).getExtensions().getByType(AmpSourceSetConfiguration.class);
                    configure.execute(ampSourceSet);
                });
    }

    Provider<AmpSourceSetConfiguration> getAmpSourceSetConfiguration(String sourceSetName) {
        return getSourceSet(sourceSetName)
                .map(sourceSet -> new DslObject(sourceSet).getExtensions().getByType(AmpSourceSetConfiguration.class));
    }

    private NamedDomainObjectProvider<SourceSet> getSourceSet(String sourceSetName) {
        SourceSetContainer sourceSets = project.getExtensions().getByType(JavaPluginExtension.class).getSourceSets();
        if (!sourceSets.getNames().contains(sourceSetName)) {
            return sourceSets.register(sourceSetName);
        } else {
            return sourceSets.named(sourceSetName);
        }
    }

}
