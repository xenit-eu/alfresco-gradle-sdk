package eu.xenit.gradle.alfrescosdk;

import eu.xenit.gradle.alfrescosdk.internal.tasks.DefaultAmpSourceSet;
import eu.xenit.gradle.alfrescosdk.tasks.AmpSourceSet;
import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
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
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.WriteProperties;

public class AmpBasePlugin implements Plugin<Project> {

    private final SourceDirectorySetFactory sourceDirectorySetFactory;
    private Project project;

    @Inject
    public AmpBasePlugin(SourceDirectorySetFactory sourceDirectorySetFactory) {
        this.sourceDirectorySetFactory = sourceDirectorySetFactory;
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
            if(sourceSet.getName().equals(SourceSet.TEST_SOURCE_SET_NAME) || sourceSet.getName().endsWith("Test")) {
                // Do not apply this configuration for tests
                return;
            }
            DefaultAmpSourceSet ampSourceSet = new DefaultAmpSourceSet(sourceSet, project, sourceDirectorySetFactory);
            new DslObject(sourceSet).getConvention().getPlugins().put("amp", ampSourceSet);

            String rootDir = "src/"+sourceSet.getName()+"/amp";
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

            createWritePropertiesTask(ampSourceSet.getModulePropertiesTaskName(), "module.properties", ampSourceSet.getAmp().getModuleProperties());
            createWritePropertiesTask(ampSourceSet.getFileMappingPropertiesTaskName(), "file-mapping.properties", ampSourceSet.getAmp().getFileMappingProperties());
        });
    }

    private Provider<WriteProperties> createWritePropertiesTask(String taskName, String fileName, Properties properties) {
        return project.getTasks().register(taskName, WriteProperties.class, writeProperties -> {
            writeProperties.setProperties(propertiesToMap(properties));
            writeProperties.setOutputFile(project.getBuildDir().toPath().resolve(taskName).resolve(fileName).toFile());
        });
    }

    public void configureAmpSourceSets(Action<? super AmpSourceSet> configure) {
        project.getConvention().getPlugin(JavaPluginConvention.class).getSourceSets().all(sourceSet ->  {
            AmpSourceSet ampSourceSet = new DslObject(sourceSet).getConvention().findPlugin(AmpSourceSet.class);
            if(ampSourceSet != null) {
                configure.execute(ampSourceSet);
            }
        });
    }
}
