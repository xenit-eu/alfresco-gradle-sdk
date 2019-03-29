package eu.xenit.gradle.alfrescosdk;

import eu.xenit.gradle.alfrescosdk.tasks.Amp;
import eu.xenit.gradle.alfrescosdk.tasks.AmpSourceSet;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.JavaPluginConvention;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.SourceSetContainer;
import org.gradle.api.tasks.TaskProvider;
import org.gradle.jvm.tasks.Jar;
import org.gradle.plugins.ide.idea.IdeaPlugin;

public class AmpPlugin implements Plugin<Project> {

    public static final String AMP_CONFIGURATION = "amp";
    public static final String AMP_TASK = "amp";

    public static final String AMP_EXTENSION = "ampConfig";

    @Override
    public void apply(Project project) {
        project.getPluginManager().apply(AmpBasePlugin.class);
        project.getPluginManager().apply(AmpLegacyPlugin.class);

        project.getPlugins().withType(AmpBasePlugin.class, ampBasePlugin -> {
            ampBasePlugin.configureAmpSourceSets(ampSourceSet -> {
                TaskProvider<Jar> jarTask = configureJarTask(project, ampSourceSet);
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
                    amp.setJar(jarTask.get());
                    amp.dependsOn(
                            ampSourceSet.getJarTaskName(),
                            ampSourceSet.getFileMappingPropertiesTaskName(),
                            ampSourceSet.getModulePropertiesTaskName()
                    );
                    amp.setGroup("build");
                    if(!ampSourceSet.getName().equals(SourceSet.MAIN_SOURCE_SET_NAME)) {
                        amp.setClassifier(ampSourceSet.getName());
                    }
                });
                project.getConfigurations().register(ampSourceSet.getAmpConfigurationName());
                project.getArtifacts().add(ampSourceSet.getAmpConfigurationName(), ampTask);

                project.getTasks().named("assemble").configure(assemble -> {
                    assemble.dependsOn(ampTask);
                });

                project.getPlugins().withType(IdeaPlugin.class, ideaPlugin -> {
                    ideaPlugin.getModel().getModule().getResourceDirs().addAll(ampSourceSet.getAmp().getConfig().getSrcDirs());
                });
            });
        });
    }


    @SuppressWarnings("unchecked")
    private TaskProvider<Jar> configureJarTask(Project project, AmpSourceSet ampSourceSet) {
        SourceSetContainer sourceSets = project.getConvention().getPlugin(JavaPluginConvention.class).getSourceSets();
        SourceSet sourceSet = sourceSets.getByName(ampSourceSet.getName());
        if(project.getTasks().findByName(sourceSet.getJarTaskName()) == null) {
            return project.getTasks().register(sourceSet.getJarTaskName(), Jar.class, jar -> {
                jar.from(sourceSet.getOutput());
                jar.setClassifier(sourceSet.getName());
                jar.dependsOn(sourceSet.getClassesTaskName());
            });
        }
        return (TaskProvider)project.getTasks().named(sourceSet.getJarTaskName());
    }

}
