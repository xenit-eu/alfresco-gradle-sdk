package eu.xenit.gradle.alfrescosdk;

import eu.xenit.gradle.alfrescosdk.internal.GradleVersionCheck;
import groovy.lang.Closure;
import org.gradle.api.NamedDomainObjectProvider;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.dsl.RepositoryHandler;
import org.gradle.api.artifacts.repositories.MavenArtifactRepository;
import org.gradle.api.internal.plugins.DslObject;
import org.gradle.api.plugins.JavaBasePlugin;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.plugins.JavaPluginConvention;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.SourceSetContainer;

public class AlfrescoPlugin implements Plugin<Project> {

    public static final String ALFRESCO_PROVIDED = "alfrescoProvided";
    public static final String PLUGIN_ID = "eu.xenit.alfresco";
    public static final String ALFRESCO_REPOSITORY_PUBLIC = "https://artifacts.alfresco.com/nexus/content/groups/public/";

    private Project project;

    @Override
    public void apply(Project project) {
        GradleVersionCheck.assertSupportedVersion(PLUGIN_ID);

        this.project = project;
        project.getPluginManager().apply(JavaPlugin.class);

        configureRepository();

        project.getPlugins().withType(JavaBasePlugin.class, javaBasePlugin -> {
            SourceSetContainer sourceSets = project.getConvention().getPlugin(JavaPluginConvention.class).getSourceSets();

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
                ampBasePlugin.allAmpSourceSets(ampSourceSet -> {
                    if(ampSourceSet.getName().equals(SourceSet.MAIN_SOURCE_SET_NAME)) {
                        // Main sourceset is already configured above, do not configure it again
                        return;
                    }
                    SourceSet sourceSet = sourceSets.getByName(ampSourceSet.getName());
                    registerAlfrescoProvided(sourceSet);
                });
            });
        });

    }

    private NamedDomainObjectProvider<Configuration> registerAlfrescoProvided(SourceSet sourceSet) {
        String alfrescoProvidedName = sourceSet.getTaskName(null, ALFRESCO_PROVIDED);
        NamedDomainObjectProvider<Configuration> alfrescoProvided = project.getConfigurations().register(alfrescoProvidedName);
        project.getConfigurations().named(sourceSet.getCompileOnlyConfigurationName())
                .configure(compileOnly -> {
                    compileOnly.extendsFrom(alfrescoProvided.get());
                });
        return alfrescoProvided;
    }

    private void configureRepository() {
        RepositoryHandler repositories = project.getRepositories();
        new DslObject(repositories).getExtensions().add("alfrescoPublic", new AlfrescoPublicRepositoryConvention(repositories));
    }

    public static class AlfrescoPublicRepositoryConvention extends Closure<MavenArtifactRepository> {

        private final RepositoryHandler repositories;

        private AlfrescoPublicRepositoryConvention(RepositoryHandler repositories) {
            super(repositories);
            this.repositories = repositories;
        }

        public MavenArtifactRepository doCall() {
            return repositories.maven(repo -> {
                repo.setUrl(ALFRESCO_REPOSITORY_PUBLIC);
                repo.setName("Alfresco Public");
            });
        }
    }
}
