package eu.xenit.gradle.alfrescosdk;

import eu.xenit.gradle.alfrescosdk.internal.GradleVersionCheck;
import eu.xenit.gradle.alfrescosdk.internal.RepositoryHandlerExtensions;
import org.gradle.api.NamedDomainObjectProvider;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.plugins.JavaBasePlugin;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.SourceSetContainer;

public class AlfrescoPlugin implements Plugin<Project> {

    public static final String ALFRESCO_PROVIDED = "alfrescoProvided";
    public static final String PLUGIN_ID = "eu.xenit.alfresco";

    private Project project;

    @Override
    public void apply(Project project) {
        GradleVersionCheck.assertSupportedVersion(PLUGIN_ID);

        this.project = project;
        project.getPluginManager().apply(JavaPlugin.class);

        configureRepository();

        project.getPlugins().withType(JavaBasePlugin.class, javaBasePlugin -> {
            SourceSetContainer sourceSets = project.getExtensions().getByType(JavaPluginExtension.class).getSourceSets();

            /*
             * Adds the sourceSets to the default main SourceSet.
             */
            sourceSets.all(sourceSet -> {
                if (sourceSet.getName().equals(SourceSet.MAIN_SOURCE_SET_NAME)) {
                    registerAlfrescoProvided(sourceSet);
                }
            });
            // Separate callback function, so the configuration for the test sourceset certainly happens after the main sourceset
            sourceSets.all(sourceSet -> {
                if(sourceSet.getName().equals(SourceSet.TEST_SOURCE_SET_NAME)) {
                    project.getConfigurations().named(sourceSet.getImplementationConfigurationName())
                            .configure(testImplementation -> {
                                testImplementation.extendsFrom(project.getConfigurations().getByName(ALFRESCO_PROVIDED));
                            });
                }
            });

            // Configure other amp sourcesets with an alfrescoProvided configuration
            project.getPlugins().withType(AmpBasePlugin.class, ampBasePlugin -> {
                ampBasePlugin.allAmpSourceSetConfigurations(ampSourceSetConfig -> {
                    if(ampSourceSetConfig.getSourceSet().getName().equals(SourceSet.MAIN_SOURCE_SET_NAME)) {
                        // Main sourceset is already configured above, do not configure it again
                        return;
                    }
                    registerAlfrescoProvided(ampSourceSetConfig.getSourceSet().getSourceSet());
                });
            });
        });

    }

    private NamedDomainObjectProvider<Configuration> registerAlfrescoProvided(SourceSet sourceSet) {
        String alfrescoProvidedName = sourceSet.getTaskName(null, ALFRESCO_PROVIDED);
        NamedDomainObjectProvider<Configuration> alfrescoProvided = project.getConfigurations().register(alfrescoProvidedName);
        project.getConfigurations().named(sourceSet.getCompileClasspathConfigurationName())
                .configure(compileClasspath -> {
                    compileClasspath.extendsFrom(alfrescoProvided.get());
                });
        return alfrescoProvided;
    }

    /**
     * Adds Alfresco public repository to the build
     */
    private void configureRepository() {
        RepositoryHandlerExtensions.apply(project.getRepositories(), project);
    }
}
