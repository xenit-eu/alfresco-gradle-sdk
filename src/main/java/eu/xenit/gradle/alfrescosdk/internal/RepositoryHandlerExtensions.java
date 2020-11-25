package eu.xenit.gradle.alfrescosdk.internal;

import java.net.URI;
import javax.inject.Inject;
import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.api.artifacts.dsl.RepositoryHandler;
import org.gradle.api.artifacts.repositories.MavenArtifactRepository;
import org.gradle.api.internal.HasConvention;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;

public class RepositoryHandlerExtensions {

    public static final URI ALFRESCO_REPOSITORY_PUBLIC = URI
            .create("https://artifacts.alfresco.com/nexus/content/groups/public/");
    private static final URI ALFRESCO_REPOSITORY_ENTERPRISE = URI
            .create("https://artifacts.alfresco.com/nexus/content/repositories/enterprise-releases/");

    private static final String ALFRESCO_REPOSITORY_ENTERPRISE_USERNAME_PROPERTY = "org.alfresco.artifacts.username";
    private static final String ALFRESCO_REPOSITORY_ENTERPRISE_PASSWORD_PROPERTY = "org.alfresco.artifacts.password";

    private static final Logger LOGGER = Logging.getLogger(RepositoryHandlerExtensions.class);

    private final RepositoryHandler repositories;
    private final Project project;

    @Inject
    public RepositoryHandlerExtensions(RepositoryHandler repositories, Project project) {
        this.repositories = repositories;
        this.project = project;
    }

    protected static final Action<? super MavenArtifactRepository> EMPTY_ACTION = repository -> {
    };

    public MavenArtifactRepository alfrescoPublic() {
        return alfrescoPublic(EMPTY_ACTION);
    }

    public MavenArtifactRepository alfrescoPublic(Action<? super MavenArtifactRepository> action) {
        return repositories.maven(repository -> {
            repository.setName("AlfrescoPublic");
            repository.setUrl(ALFRESCO_REPOSITORY_PUBLIC);
            action.execute(repository);
        });
    }

    public MavenArtifactRepository alfrescoEnterprise() {
        return alfrescoEnterprise(EMPTY_ACTION);
    }

    public MavenArtifactRepository alfrescoEnterprise(Action<? super MavenArtifactRepository> action) {
        return repositories.maven(repository -> {
            repository.setName("AlfrescoEnterprise");
            repository.setUrl(ALFRESCO_REPOSITORY_ENTERPRISE);

            if (project.hasProperty(ALFRESCO_REPOSITORY_ENTERPRISE_USERNAME_PROPERTY) && project
                    .hasProperty(ALFRESCO_REPOSITORY_ENTERPRISE_PASSWORD_PROPERTY)) {
                LOGGER.debug("Using credentials for Alfresco enterprise repository from properties");
                repository.credentials(passwordCredentials -> {
                    passwordCredentials
                            .setUsername(project.property(ALFRESCO_REPOSITORY_ENTERPRISE_USERNAME_PROPERTY).toString());
                    passwordCredentials
                            .setPassword(project.property(ALFRESCO_REPOSITORY_ENTERPRISE_PASSWORD_PROPERTY).toString());
                });
            } else {
                LOGGER.debug("Credentials for Alfresco Enterprise repository have not been configured.");
            }

            action.execute(repository);
        });
    }

    public static void apply(RepositoryHandler repositoryHandler, Project project) {
        RepositoryHandlerExtensions repositoryHandlerExtensions = project.getObjects()
                .newInstance(RepositoryHandlerExtensions.class, repositoryHandler, project);
        ((HasConvention) repositoryHandler).getConvention().getPlugins()
                .put("eu.xenit.alfresco", repositoryHandlerExtensions);
    }
}
