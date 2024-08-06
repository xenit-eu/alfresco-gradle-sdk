package eu.xenit.gradle.alfrescosdk;

import eu.xenit.gradle.alfrescosdk.internal.GradleVersionCheck;
import eu.xenit.gradle.alfrescosdk.tasks.Amp;
import eu.xenit.gradle.alfrescosdk.tasks.AmpSourceSetConfiguration;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.TaskProvider;
import org.gradle.jvm.tasks.Jar;
import org.gradle.language.base.plugins.LifecycleBasePlugin;
import org.gradle.plugins.ide.idea.IdeaPlugin;

public class AmpPlugin implements Plugin<Project> {

    public static final String PLUGIN_ID = "eu.xenit.amp";

    @Deprecated
    public static final String AMP_CONFIGURATION = "amp";
    @Deprecated
    public static final String AMP_TASK = "amp";
    @Deprecated
    public static final String AMP_EXTENSION = "ampConfig";

    @Override
    public void apply(Project project) {
        GradleVersionCheck.assertSupportedVersion(PLUGIN_ID);

        project.getPluginManager().apply(AmpBasePlugin.class);
        project.getPluginManager().apply(AmpLegacyPlugin.class);

        project.getPlugins().withType(AmpBasePlugin.class, ampBasePlugin -> {
            // Automatically configure main sourceset for amps
            ampBasePlugin.configureAmpSourceSetConfiguration(SourceSet.MAIN_SOURCE_SET_NAME, s -> {});
            ampBasePlugin.allAmpSourceSetConfigurations(ampSourceSetConfig -> {
                var ampSourceSet = ampSourceSetConfig.getSourceSet();
                configureJarTask(project, ampSourceSetConfig);
                TaskProvider<Amp> ampTask = project.getTasks().withType(Amp.class).named(ampSourceSet.getAmpTaskName());

                // Configure amp artifact
                project.getConfigurations().register(ampSourceSet.getAmpConfigurationName());
                project.getArtifacts().add(ampSourceSet.getAmpConfigurationName(), ampTask);

                project.getTasks().named(LifecycleBasePlugin.ASSEMBLE_TASK_NAME).configure(assemble -> {
                    assemble.dependsOn(ampTask);
                });

                project.getPlugins().withType(IdeaPlugin.class, ideaPlugin -> {
                    ideaPlugin.getModel().getModule().getResourceDirs().addAll(ampSourceSetConfig.getConfig().getSrcDirs());
                });
            });
        });
    }

    private TaskProvider<Jar> configureJarTask(Project project, AmpSourceSetConfiguration ampSourceSetConfig) {
        SourceSet sourceSet = ampSourceSetConfig.getSourceSet().getSourceSet();
        if(project.getTasks().getNames().contains(sourceSet.getJarTaskName())) {
            return project.getTasks().named(sourceSet.getJarTaskName(), Jar.class);
        }
        return project.getTasks().register(sourceSet.getJarTaskName(), Jar.class, jar -> {
            jar.setGroup(LifecycleBasePlugin.BUILD_GROUP);
            jar.from(sourceSet.getOutput());
            jar.getArchiveClassifier().convention(sourceSet.getName());
            jar.dependsOn(sourceSet.getClassesTaskName());
        });
    }

}
