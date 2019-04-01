package eu.xenit.gradle.alfrescosdk;

import eu.xenit.gradle.alfrescosdk.config.AmpConfig;
import eu.xenit.gradle.alfrescosdk.tasks.Amp;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.file.CopySpec;
import org.gradle.api.file.FileCollection;

public class AmpPlugin implements Plugin<Project> {

    public static final String AMP_CONFIGURATION = "amp";
    public static final String AMP_TASK = "amp";

    public static final String AMP_EXTENSION = "ampConfig";

    @Override
    public void apply(Project project) {
        AmpConfig ampConfig = project.getExtensions().create(AMP_EXTENSION, AmpConfig.class, project);

        Amp amp = project.getTasks().create(AMP_TASK, Amp.class);
        project.getConfigurations().create(AMP_CONFIGURATION);
        project.getArtifacts().add(AMP_CONFIGURATION, amp);

        amp.setModuleProperties(ampConfig::getModuleProperties);
        amp.setConfig(ampConfig::getConfigDir);
        amp.setWeb(ampConfig::getWebDir);
        amp.setFileMappingProperties(ampConfig::getFileMappingProperties);

        project.getPluginManager().withPlugin(AlfrescoPlugin.PLUGIN_ID, appliedPlugin -> {
            FileCollection runtime = project.getConfigurations().getAt("runtimeClasspath");
            FileCollection jarArtifact = project.getTasks().getAt("jar").getOutputs().getFiles();
            FileCollection jars = runtime.plus(jarArtifact);
            amp.setLibs(jars);
            project.afterEvaluate(p -> {
                if (ampConfig.getDynamicExtension()) {
                    amp.de((CopySpec c) -> c.from(jars));
                    // Reset libs only when they have not been changed directly in the task
                    if(amp.getLibs() == jars) {
                        amp.setLibs(null);
                    }
                }
            });
        });
    }
}
