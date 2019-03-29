package eu.xenit.gradle.alfrescosdk;

import eu.xenit.gradle.alfrescosdk.config.AmpConfig;
import eu.xenit.gradle.alfrescosdk.tasks.Amp;
import eu.xenit.gradle.alfrescosdk.tasks.AmpSourceSet;
import java.util.Collections;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.file.CopySpec;
import org.gradle.api.file.FileCollection;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.plugins.JavaPluginConvention;
import org.gradle.api.tasks.TaskProvider;
import org.gradle.plugins.ide.idea.IdeaPlugin;
import org.gradle.plugins.ide.idea.model.IdeaModule;

public class AmpPlugin implements Plugin<Project> {
    private static final Logger LOGGER = Logging.getLogger(AmpPlugin.class);

    public static final String AMP_CONFIGURATION = "amp";
    public static final String AMP_TASK = "amp";

    public static final String AMP_EXTENSION = "ampConfig";
    private Project project;

    @Override
    public void apply(Project project) {
        this.project = project;
        project.getPluginManager().apply(AmpBasePlugin.class);

        project.getPlugins().withType(AmpBasePlugin.class, ampBasePlugin -> {
            for (AmpSourceSet ampSourceSet : ampBasePlugin.getAmpSourceSets()) {
                TaskProvider<Amp> ampTask = project.getTasks().register(ampSourceSet.getAmpTaskName(), Amp.class, amp -> {
                    amp.setModuleProperties(() -> project.getTasks().getByName(ampSourceSet.getModulePropertiesTaskName()).getOutputs().getFiles().getSingleFile());
                    amp.setFileMappingProperties(() -> project.getTasks().getByName(ampSourceSet.getFileMappingPropertiesTaskName()).getOutputs().getFiles().getSingleFile());
                    amp.web(copySpec -> {
                        copySpec.from(ampSourceSet.getAmp().getWeb());
                    });
                    amp.config(copySpec -> {
                        copySpec.from(ampSourceSet.getAmp().getConfig());
                    });

                    amp.dependsOn(ampSourceSet.getFileMappingPropertiesTaskName(), ampSourceSet.getModulePropertiesTaskName());
                });

                project.getConfigurations().register(ampSourceSet.getAmpConfigurationName());
                project.getArtifacts().add(ampSourceSet.getAmpConfigurationName(), ampTask);
                project.getPlugins().withType(IdeaPlugin.class, ideaPlugin -> {
                    ideaPlugin.getModel().getModule().getResourceDirs().addAll(ampSourceSet.getAmp().getConfig().getSrcDirs());
                });
            }

        });

        AmpConfig ampConfig = project.getExtensions().create(AMP_EXTENSION, AmpConfig.class, project);

        project.afterEvaluate(p -> {
            // Handle legacy amp configuration if anything has been changed on it
            if(ampConfig.isConfigTouched()) {
                LOGGER.warn("Using the "+AMP_EXTENSION+" configuration block is deprecated. Use the sourceSets configuration instead.");
                getMainAmpTaskSourceSet().amp(config -> {
                    config.module(ampConfig.getModuleProperties().getAbsolutePath());
                    config.fileMapping(ampConfig.getFileMappingProperties().getAbsolutePath());
                    config.getConfig().setSrcDirs(Collections.singleton(ampConfig.getConfigDir()));
                    config.getWeb().setSrcDirs(Collections.singleton(ampConfig.getWebDir()));
                });
            }
        });

        project.getPluginManager().withPlugin(AlfrescoPlugin.PLUGIN_ID, appliedPlugin -> {
            FileCollection runtime = project.getConfigurations().getAt("runtimeClasspath");
            FileCollection jarArtifact = project.getTasks().getAt("jar").getOutputs().getFiles();
            FileCollection jars = runtime.plus(jarArtifact);
            project.getTasks().withType(Amp.class).getByName(AMP_TASK).setLibs(jars);
        });
    }

    private AmpSourceSet getMainAmpTaskSourceSet() {
        for (AmpSourceSet ampSourceSet : project.getPlugins().getPlugin(AmpBasePlugin.class).getAmpSourceSets()) {
            if(ampSourceSet.getAmpTaskName().equals(AMP_TASK)) {
                return ampSourceSet;
            }
        }
        return null;
    }
}
