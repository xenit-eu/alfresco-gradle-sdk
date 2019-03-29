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

    public static final String AMP_CONFIGURATION = "amp";
    public static final String AMP_TASK = "amp";

    public static final String AMP_EXTENSION = "ampConfig";

    @Override
    public void apply(Project project) {
        project.getPluginManager().apply(AmpBasePlugin.class);
        project.getPluginManager().apply(AmpLegacyPlugin.class);

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
                    amp.setLibs(project.getConfigurations().getByName(ampSourceSet.getAmpLibrariesConfigurationName()));
                    amp.dependsOn(ampSourceSet.getFileMappingPropertiesTaskName(), ampSourceSet.getModulePropertiesTaskName());
                });

                project.getConfigurations().register(ampSourceSet.getAmpConfigurationName());
                project.getArtifacts().add(ampSourceSet.getAmpConfigurationName(), ampTask);
                project.getPlugins().withType(IdeaPlugin.class, ideaPlugin -> {
                    ideaPlugin.getModel().getModule().getResourceDirs().addAll(ampSourceSet.getAmp().getConfig().getSrcDirs());
                });
            }
        });
    }

}
