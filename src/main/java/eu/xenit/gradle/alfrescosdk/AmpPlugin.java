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
import org.gradle.language.base.plugins.LifecycleBasePlugin;
import org.gradle.plugins.ide.idea.IdeaPlugin;

public class AmpPlugin implements Plugin<Project> {

    @Deprecated
    public static final String AMP_CONFIGURATION = "amp";
    @Deprecated
    public static final String AMP_TASK = "amp";
    @Deprecated
    public static final String AMP_EXTENSION = "ampConfig";

    @Override
    public void apply(Project project) {
        project.getPluginManager().apply(AmpBasePlugin.class);
        project.getPluginManager().apply(AmpLegacyPlugin.class);

        project.getPlugins().withType(AmpBasePlugin.class, ampBasePlugin -> {
            ampBasePlugin.configureAmpSourceSets(ampSourceSet -> {
                configureJarTask(project, ampSourceSet);
                TaskProvider<Amp> ampTask = configureAmpTask(project, ampSourceSet);
                project.getConfigurations().register(ampSourceSet.getAmpConfigurationName());
                project.getArtifacts().add(ampSourceSet.getAmpConfigurationName(), ampTask);

                project.getTasks().named(LifecycleBasePlugin.ASSEMBLE_TASK_NAME).configure(assemble -> {
                    assemble.dependsOn(ampTask);
                });

                project.getPlugins().withType(IdeaPlugin.class, ideaPlugin -> {
                    ideaPlugin.getModel().getModule().getResourceDirs().addAll(ampSourceSet.getAmp().getConfig().getSrcDirs());
                });
            });
        });
    }

    private TaskProvider<Amp> configureAmpTask(Project project, AmpSourceSet ampSourceSet) {
        return project.getTasks().register(ampSourceSet.getAmpTaskName(), Amp.class, amp -> {
            amp.setModuleProperties(() -> project.getTasks().getByName(ampSourceSet.getModulePropertiesTaskName()).getOutputs().getFiles().getSingleFile());
            amp.setFileMappingProperties(() -> project.getTasks().getByName(ampSourceSet.getFileMappingPropertiesTaskName()).getOutputs().getFiles().getSingleFile());
            amp.web(copySpec -> {
                copySpec.from(ampSourceSet.getAmp().getWeb());
            });
            amp.config(copySpec -> {
                copySpec.from(ampSourceSet.getAmp().getConfig());
            });
            amp.setLibs(project.getConfigurations().getByName(ampSourceSet.getAmpLibrariesConfigurationName()));
            amp.setJar(() -> project.getTasks().named(ampSourceSet.getJarTaskName()).map(t -> (Jar)t).get());
            amp.dependsOn(
                    ampSourceSet.getJarTaskName(),
                    ampSourceSet.getFileMappingPropertiesTaskName(),
                    ampSourceSet.getModulePropertiesTaskName()
            );
            amp.setGroup(LifecycleBasePlugin.BUILD_GROUP);
            if(!ampSourceSet.getName().equals(SourceSet.MAIN_SOURCE_SET_NAME)) {
                amp.setClassifier(ampSourceSet.getName());
            }
        });
    }


    @SuppressWarnings("unchecked")
    private TaskProvider<Jar> configureJarTask(Project project, AmpSourceSet ampSourceSet) {
        SourceSetContainer sourceSets = project.getConvention().getPlugin(JavaPluginConvention.class).getSourceSets();
        SourceSet sourceSet = sourceSets.getByName(ampSourceSet.getName());
        if(project.getTasks().findByName(sourceSet.getJarTaskName()) == null) {
            return project.getTasks().register(sourceSet.getJarTaskName(), Jar.class, jar -> {
                jar.setGroup(LifecycleBasePlugin.BUILD_GROUP);
                jar.from(sourceSet.getOutput());
                jar.setClassifier(sourceSet.getName());
                jar.dependsOn(sourceSet.getClassesTaskName());
            });
        }
        return (TaskProvider)project.getTasks().named(sourceSet.getJarTaskName());
    }

}
