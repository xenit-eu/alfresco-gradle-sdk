package eu.xenit.gradle.alfrescosdk;

import eu.xenit.gradle.alfrescosdk.tasks.Amp;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.file.FileCollection;

public class AmpPlugin implements Plugin<Project> {

    public static final String AMP_CONFIGURATION = "amp";
    public static final String AMP_TASK = "amp";

    @Override
    public void apply(Project project) {
        Amp amp = project.getTasks().create(AMP_TASK, Amp.class);
        project.getConfigurations().create(AMP_CONFIGURATION);
        project.getArtifacts().add(AMP_CONFIGURATION, amp);


        project.getPluginManager().withPlugin(AlfrescoPlugin.PLUGIN_ID, appliedPlugin -> {
            FileCollection runtime = project.getConfigurations().getAt("runtime");
            FileCollection jarArtifact = project.getTasks().getAt("jar").getOutputs().getFiles();
            FileCollection libs = runtime.plus(jarArtifact);
            amp.setLibs(libs);
        });
    }
}
