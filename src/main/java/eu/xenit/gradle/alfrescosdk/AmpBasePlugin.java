package eu.xenit.gradle.alfrescosdk;

import eu.xenit.gradle.alfrescosdk.internal.ConfigurationDispatcher;
import eu.xenit.gradle.alfrescosdk.internal.GradleVersionCheck;
import eu.xenit.gradle.alfrescosdk.internal.tasks.DefaultAmpSourceSet;
import eu.xenit.gradle.alfrescosdk.tasks.Amp;
import eu.xenit.gradle.alfrescosdk.tasks.AmpSourceSet;
import eu.xenit.gradle.alfrescosdk.tasks.AmpSourceSetConfiguration;
import java.io.File;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;
import javax.inject.Inject;
import org.gradle.api.Action;
import org.gradle.api.NamedDomainObjectProvider;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.file.FileCollection;
import org.gradle.api.internal.plugins.DslObject;
import org.gradle.api.logging.Logging;
import org.gradle.api.plugins.JavaBasePlugin;
import org.gradle.api.plugins.JavaPluginConvention;
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

    private final ConfigurationDispatcher<DefaultAmpSourceSet> sourceSetConfigurationDispatcher;
    private Project project;

    @Inject
    public AmpBasePlugin() {
        sourceSetConfigurationDispatcher = new ConfigurationDispatcher<>();
    }

    @Override
    public void apply(Project target) {
        GradleVersionCheck.assertSupportedVersion(PLUGIN_ID);
        project = target;
        project.getPluginManager().apply(JavaBasePlugin.class);
        configureSourceSetDefaults();

    }

    private static Map<String, Object> propertiesToMap(Properties properties) {
        return properties.keySet()
                .stream()
                .collect(Collectors.toMap(Object::toString, k -> properties.get(k).toString()));
    }


    private void configureSourceSetDefaults() {
        project.getConvention().getPlugin(JavaPluginConvention.class).getSourceSets().all(sourceSet -> {
            DefaultAmpSourceSet ampSourceSet = new DefaultAmpSourceSet(sourceSet, project,
                    sourceSetConfigurationDispatcher);
            new DslObject(sourceSet).getConvention().getPlugins().put("amp", ampSourceSet);
            String rootDir = "src/" + ampSourceSet.getName() + "/amp";

            //add default config sourceDir
            ampSourceSet.getAmp().getConfig().srcDir(rootDir + "/config");

            //add default web sourceDir
            ampSourceSet.getAmp().getWeb().srcDir(rootDir + "/web");

            //add module.properties
            File moduleProperties = project.file(rootDir + "/module.properties");

            if (moduleProperties.exists()) {
                ampSourceSet.getAmp().module(moduleProperties);
            } else {
                ampSourceSet.getAmp().module(properties -> {
                    LOGGER.info(
                            "{} does not exist for configured amp sourceset {}. A module.properties file is configured automatically from the project",
                            moduleProperties, ampSourceSet.getName());
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
                ampSourceSet.getAmp().fileMapping(fileMappingProperties);
            }

        });

        sourceSetConfigurationDispatcher.add(ampSourceSet -> {
            createWritePropertiesTask(ampSourceSet.getModulePropertiesTaskName(), ampSourceSet.getName(),
                    "module.properties", ampSourceSet.getAmp().getModuleProperties());
            createWritePropertiesTask(ampSourceSet.getFileMappingPropertiesTaskName(), ampSourceSet.getName(),
                    "file-mapping.properties", ampSourceSet.getAmp().getFileMappingProperties());
            createAmpTask(project, ampSourceSet);
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

    private TaskProvider<Amp> createAmpTask(Project project, DefaultAmpSourceSet ampSourceSet) {
        return project.getTasks().register(ampSourceSet.getAmpTaskName(), Amp.class, amp -> {
            amp.setModuleProperties(
                    () -> project.getTasks().getByName(ampSourceSet.getModulePropertiesTaskName()).getOutputs()
                            .getFiles().getSingleFile());
            amp.setFileMappingProperties(
                    () -> project.getTasks().getByName(ampSourceSet.getFileMappingPropertiesTaskName()).getOutputs()
                            .getFiles().getSingleFile());
            amp.web(copySpec -> {
                copySpec.from(ampSourceSet.getAmp().getWeb());
            });
            amp.config(copySpec -> {
                copySpec.from(ampSourceSet.getAmp().getConfig());
            });

            Provider<Boolean> dynamicExtension = ampSourceSet.getAmp().getDynamicExtension();
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
                amp.setClassifier(ampSourceSet.getName());
            }
        });
    }

    public void allAmpSourceSets(Action<? super AmpSourceSet> configure) {
        sourceSetConfigurationDispatcher.add(configure);
    }

    void configureAmpSourceSet(String sourceSetName, Action<? super AmpSourceSetConfiguration> configure) {
        getSourceSet(sourceSetName)
                .configure(sourceSet -> {
                    AmpSourceSet ampSourceSet = new DslObject(sourceSet).getConvention().getPlugin(AmpSourceSet.class);
                    ampSourceSet.amp(configure);
                });
    }

    Provider<AmpSourceSet> getAmpSourceSet(String sourceSetName) {
        return getSourceSet(sourceSetName)
                .map(sourceSet -> new DslObject(sourceSet).getConvention().getPlugin(AmpSourceSet.class));
    }

    private NamedDomainObjectProvider<SourceSet> getSourceSet(String sourceSetName) {
        SourceSetContainer sourceSets = project.getConvention().getPlugin(JavaPluginConvention.class).getSourceSets();
        if (!sourceSets.getNames().contains(sourceSetName)) {
            return sourceSets.register(sourceSetName);
        } else {
            return sourceSets.named(sourceSetName);
        }
    }

}
