package eu.xenit.gradle.alfrescosdk;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.plugins.JavaPlugin;

public class AlfrescoPlugin implements Plugin<Project> {

    public static final String ALFRESCO_PROVIDED = "alfrescoProvided";

    @Override
    public void apply(Project project) {
        project.getPluginManager().apply(JavaPlugin.class);
        Configuration alfrescoProvided = project.getConfigurations().create(ALFRESCO_PROVIDED);
        Configuration compileOnly = project.getConfigurations().getByName("compileOnly");
        compileOnly.extendsFrom(alfrescoProvided);
        Configuration testRuntime = project.getConfigurations().getByName("testRuntime");
        testRuntime.extendsFrom(alfrescoProvided);
    }
}
