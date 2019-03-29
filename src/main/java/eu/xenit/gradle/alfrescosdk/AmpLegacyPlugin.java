package eu.xenit.gradle.alfrescosdk;

import static eu.xenit.gradle.alfrescosdk.AmpPlugin.AMP_CONFIGURATION;
import static eu.xenit.gradle.alfrescosdk.AmpPlugin.AMP_EXTENSION;
import static eu.xenit.gradle.alfrescosdk.AmpPlugin.AMP_TASK;

import eu.xenit.gradle.alfrescosdk.config.AmpConfig;
import eu.xenit.gradle.alfrescosdk.tasks.Amp;
import eu.xenit.gradle.alfrescosdk.tasks.AmpSourceSet;
import java.io.File;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.file.FileCollection;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.TaskProvider;

public class AmpLegacyPlugin implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        project.getPlugins().apply(JavaPlugin.class); // Applying the java plugin ensures that the main source set is created
        project.getPlugins().withType(AmpBasePlugin.class, ampBasePlugin -> {
            for (AmpSourceSet ampSourceSet : ampBasePlugin.getAmpSourceSets()) {
                if(ampSourceSet.getName().equals(SourceSet.MAIN_SOURCE_SET_NAME)) {
                    AmpConfig ampConfig = project.getExtensions().create(AMP_EXTENSION, AmpConfig.class, project, ampSourceSet);
                    project.afterEvaluate(p -> {
                        TaskProvider<Task> ampProvider = project.getTasks().named(AMP_TASK);

                        if(ampConfig._getDynamicExtension()) {
                            ampProvider.configure(t -> {
                                Amp amp = (Amp)t;
                                FileCollection libs = amp.getLibs();
                                amp.setLibs(null);
                                amp.de(copySpec -> {
                                    copySpec.from(libs);
                                });
                            });
                        }

                        if(ampSourceSet.getAmp().getWeb().getSrcDirs().size() == 1) {
                            ampProvider.configure(amp -> {
                                ((Amp)amp)._setWeb(() -> ampSourceSet.getAmp().getWeb().getSrcDirs().iterator().next());
                            });
                        }
                        if(ampSourceSet.getAmp().getConfig().getSrcDirs().size() == 1) {
                            ampProvider.configure(amp -> {
                                ((Amp)amp)._setConfig(() -> {
                                    File dir = ampSourceSet.getAmp().getConfig().getSrcDirs().iterator().next();
                                    if(dir.equals(project.file(AmpConfig.DEFAULT_CONFIG_DIR)) && !dir.exists()) {
                                        return null;
                                    }
                                    return dir;
                                });
                            });
                        }
                    });

                }
            }
        });
    }
}
