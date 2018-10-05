package eu.xenit.gradle.alfrescosdk;

import eu.xenit.gradle.alfrescosdk.tasks.Amp;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class AmpPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        Amp amp = project.getTasks().create("amp", Amp.class);

    }
}
