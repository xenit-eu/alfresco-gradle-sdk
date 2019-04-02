package eu.xenit.gradle.alfrescosdk;

import static eu.xenit.gradle.alfrescosdk.AmpPlugin.AMP_EXTENSION;
import static eu.xenit.gradle.alfrescosdk.AmpPlugin.AMP_TASK;

import eu.xenit.gradle.alfrescosdk.config.AmpConfig;
import eu.xenit.gradle.alfrescosdk.tasks.Amp;
import java.io.File;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.file.FileCollection;
import org.gradle.api.plugins.JavaBasePlugin;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.plugins.JavaPluginConvention;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.TaskProvider;

public class AmpLegacyPlugin implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        project.getPlugins().apply(JavaPlugin.class); // Applying the java plugin ensures that the main source set is created

        project.getPlugins().withType(AmpBasePlugin.class, ampBasePlugin -> {
            ampBasePlugin.configureAmpSourceSet(SourceSet.MAIN_SOURCE_SET_NAME, ampSourceSetConfiguration ->  {
                    AmpConfig ampConfig = project.getExtensions().create(AMP_EXTENSION, AmpConfig.class, project, ampSourceSetConfiguration);
                    project.afterEvaluate(p -> {
                        TaskProvider<Task> ampProvider = project.getTasks().named(AMP_TASK);
                        if(ampConfig._getDynamicExtension()) {
                            ampProvider.configure(t -> {
                                Amp amp = (Amp)t;
                                FileCollection libs = amp.getLibs();
                                amp.setLibs(null);
                                amp.de(copySpec -> {
                                    copySpec.from(project.getTasks().named("jar"));
                                    copySpec.from(libs);
                                });
                            });
                        }

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
                    });
            });
        });
    }
}
