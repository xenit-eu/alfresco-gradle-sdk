package eu.xenit.gradle.alfrescosdk;

import static eu.xenit.gradle.alfrescosdk.AmpPlugin.AMP_EXTENSION;

import eu.xenit.gradle.alfrescosdk.config.AmpConfig;
import eu.xenit.gradle.alfrescosdk.tasks.Amp;
import eu.xenit.gradle.alfrescosdk.tasks.AmpSourceSetConfiguration;
import java.io.File;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.TaskProvider;

public class AmpLegacyPlugin implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        project.getPlugins().apply(JavaPlugin.class); // Applying the java plugin ensures that the main source set is created

        project.getPlugins().withType(AmpBasePlugin.class, ampBasePlugin -> {
            ampBasePlugin.configureAmpSourceSet(SourceSet.MAIN_SOURCE_SET_NAME, ampSourceSetConfiguration ->  {
                project.getExtensions().create(AMP_EXTENSION, AmpConfig.class, project, ampSourceSetConfiguration);
            });
            ampBasePlugin.allAmpSourceSets(ampSourceSet -> {
                if(ampSourceSet.getName().equals(SourceSet.MAIN_SOURCE_SET_NAME)) {
                    TaskProvider<Task> ampProvider = project.getTasks().named(ampSourceSet.getAmpTaskName());
                    AmpSourceSetConfiguration ampSourceSetConfiguration = ampSourceSet.getAmp();
                    if(ampSourceSetConfiguration.getWeb().getSrcDirs().size() == 1) {
                        ampProvider.configure(amp -> {
                            ((Amp)amp)._setWeb(() -> ampSourceSetConfiguration.getWeb().getSrcDirs().iterator().next());
                        });
                    }
                    if(ampSourceSetConfiguration.getConfig().getSrcDirs().size() == 1) {
                        ampProvider.configure(amp -> {
                            ((Amp)amp)._setConfig(() -> {
                                File dir = ampSourceSetConfiguration.getConfig().getSrcDirs().iterator().next();
                                if(dir.equals(project.file(AmpConfig.DEFAULT_CONFIG_DIR)) && !dir.exists()) {
                                    return null;
                                }
                                return dir;
                            });
                        });
                    }
                }
            });
        });
    }
}
