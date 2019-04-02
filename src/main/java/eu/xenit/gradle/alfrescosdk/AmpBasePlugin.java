package eu.xenit.gradle.alfrescosdk;

import eu.xenit.gradle.alfrescosdk.internal.ConfigurationDispatcher;
import eu.xenit.gradle.alfrescosdk.internal.tasks.DefaultAmpSourceSet;
import eu.xenit.gradle.alfrescosdk.tasks.AmpSourceSet;
import java.io.File;
import java.util.Map;
import java.util.Properties;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.inject.Inject;
import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.internal.file.SourceDirectorySetFactory;
import org.gradle.api.internal.plugins.DslObject;
import org.gradle.api.plugins.JavaBasePlugin;
import org.gradle.api.plugins.JavaPluginConvention;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.WriteProperties;
import org.gradle.internal.impldep.org.apache.maven.lifecycle.internal.LifecycleTask;
import org.gradle.language.base.plugins.LifecycleBasePlugin;

public class AmpBasePlugin implements Plugin<Project> {

    private final SourceDirectorySetFactory sourceDirectorySetFactory;
    private final ConfigurationDispatcher<DefaultAmpSourceSet> sourceSetConfigurationDispatcher;
    private Project project;

    @Inject
    public AmpBasePlugin(SourceDirectorySetFactory sourceDirectorySetFactory) {
        this.sourceDirectorySetFactory = sourceDirectorySetFactory;
        sourceSetConfigurationDispatcher = new ConfigurationDispatcher<>();
    }

    @Override
    public void apply(Project target) {
        project = target;
        project.getPluginManager().apply(JavaBasePlugin.class);
        configureSourceSetDefaults();

    }

    private static Map<String, Object> propertiesToMap(Properties properties) {
        return properties.stringPropertyNames()
                .stream()
                .collect(Collectors.toMap(Function.identity(), properties::getProperty));
    }


    private void configureSourceSetDefaults() {
        project.getConvention().getPlugin(JavaPluginConvention.class).getSourceSets().all(sourceSet -> {
            DefaultAmpSourceSet ampSourceSet = new DefaultAmpSourceSet(sourceSet, project, sourceDirectorySetFactory,
                    sourceSetConfigurationDispatcher);
            new DslObject(sourceSet).getConvention().getPlugins().put("amp", ampSourceSet);
            String rootDir = "src/"+ampSourceSet.getName()+"/amp";
            ampSourceSet.getAmp().getConfig().srcDir(rootDir+"/config");
            ampSourceSet.getAmp().getWeb().srcDir(rootDir+"/web");
            File moduleProperties = project.file(rootDir+"/module.properties");
            if(moduleProperties.exists()) {
                ampSourceSet.getAmp().module(moduleProperties);
            }
            File fileMappingProperties = project.file(rootDir+"/file-mapping.properties");
            if(fileMappingProperties.exists()) {
                ampSourceSet.getAmp().fileMapping(fileMappingProperties);
            }

        });

        sourceSetConfigurationDispatcher.add(ampSourceSet -> {
            createWritePropertiesTask(ampSourceSet.getModulePropertiesTaskName(), ampSourceSet.getName(), "module.properties", ampSourceSet.getAmp().getModuleProperties());
            createWritePropertiesTask(ampSourceSet.getFileMappingPropertiesTaskName(), ampSourceSet.getName(), "file-mapping.properties", ampSourceSet.getAmp().getFileMappingProperties());
        });
    }

    private Provider<WriteProperties> createWritePropertiesTask(String taskName, String sourceSetName, String fileName, Properties properties) {
        return project.getTasks().register(taskName, WriteProperties.class, writeProperties -> {
            writeProperties.setDescription("Creates "+fileName+" for "+sourceSetName);
            writeProperties.setProperties(propertiesToMap(properties));
            writeProperties.setOutputFile(project.getBuildDir().toPath().resolve(taskName).resolve(fileName).toFile());
        });
    }

    public void configureAmpSourceSets(Action<? super AmpSourceSet> configure) {
        sourceSetConfigurationDispatcher.add(configure);
    }
}
