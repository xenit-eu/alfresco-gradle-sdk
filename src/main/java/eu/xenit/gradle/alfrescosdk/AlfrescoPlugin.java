package eu.xenit.gradle.alfrescosdk;

import groovy.lang.Closure;
import groovy.lang.GroovyObject;
import org.codehaus.groovy.runtime.MethodClosure;
import org.gradle.api.NamedDomainObjectProvider;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.dsl.RepositoryHandler;
import org.gradle.api.artifacts.repositories.MavenArtifactRepository;
import org.gradle.api.internal.plugins.DslObject;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.api.plugins.ExtraPropertiesExtension;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.plugins.JavaPluginConvention;
import org.gradle.api.tasks.SourceSet;

public class AlfrescoPlugin implements Plugin<Project> {

    public static final String ALFRESCO_PROVIDED = "alfrescoProvided";
    public static final String PLUGIN_ID = "eu.xenit.alfresco";
    public static final String ALFRESCO_REPOSITORY_PUBLIC = "https://artifacts.alfresco.com/nexus/content/groups/public/";

    @Override
    public void apply(Project project) {
        project.getPluginManager().apply(JavaPlugin.class);
        // Configure an alfrescoProvided configuration for every sourceset
        project.getConvention().getPlugin(JavaPluginConvention.class).getSourceSets().all(sourceSet -> {
            if(sourceSet.getName().endsWith("Test")) {
                // Do not apply for other test source sets (e.g. integration tests)
                return;
            }

            // For the main test sourceset, add the alfrescoProvided configuration to the implementation dependencies
            if(sourceSet.getName().equals(SourceSet.TEST_SOURCE_SET_NAME)) {
                project.getConfigurations().named(sourceSet.getImplementationConfigurationName()).configure(testImpl -> {
                    NamedDomainObjectProvider<Configuration> alfrescoProvided = project.getConfigurations().named(ALFRESCO_PROVIDED);
                    if(alfrescoProvided.isPresent()) {
                        testImpl.extendsFrom(alfrescoProvided.get());
                    }
                });
            } else {
                NamedDomainObjectProvider<Configuration> alfrescoProvided = project.getConfigurations()
                        .register(sourceSet.getTaskName(null, ALFRESCO_PROVIDED));
                project.getConfigurations().named(sourceSet.getCompileOnlyConfigurationName())
                        .configure(compileOnly -> {
                            compileOnly.extendsFrom(alfrescoProvided.get());
                        });
            }

        });

        RepositoryHandler repositories = project.getRepositories();
        new DslObject(repositories).getExtensions().add("alfrescoPublic", new AlfrescoPublicRepository(this, repositories));
    }

    private static class AlfrescoPublicRepository extends Closure<MavenArtifactRepository> {

        private final RepositoryHandler repositories;

        private AlfrescoPublicRepository(Object owner, RepositoryHandler repositories) {
            super(owner);
            this.repositories = repositories;
        }

        public MavenArtifactRepository doCall() {
            return repositories.maven(repo -> {
                repo.setUrl(ALFRESCO_REPOSITORY_PUBLIC);
                repo.setName("Alfresco Public");
            });
        }

        @Override
        public AlfrescoPublicRepository clone() {
            return (AlfrescoPublicRepository) super.clone();
        }
    }
}
