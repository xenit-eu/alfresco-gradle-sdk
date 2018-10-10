package eu.xenit.gradle.alfrescosdk;

import groovy.lang.Closure;
import groovy.lang.GroovyObject;
import org.codehaus.groovy.runtime.MethodClosure;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.dsl.RepositoryHandler;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.api.plugins.ExtraPropertiesExtension;
import org.gradle.api.plugins.JavaPlugin;

public class AlfrescoPlugin implements Plugin<Project> {

    public static final String ALFRESCO_PROVIDED = "alfrescoProvided";
    public static final String PLUGIN_ID = "eu.xenit.alfresco";
    public static final String ALFRESCO_REPOSITORY_PUBLIC = "https://artifacts.alfresco.com/nexus/content/groups/public/";
    private static final Logger LOGGER = Logging.getLogger(AlfrescoPlugin.class);

    @Override
    public void apply(Project project) {
        project.getPluginManager().apply(JavaPlugin.class);
        Configuration alfrescoProvided = project.getConfigurations().create(ALFRESCO_PROVIDED);
        Configuration compileOnly = project.getConfigurations().getByName("compileOnly");
        compileOnly.extendsFrom(alfrescoProvided);
        Configuration testRuntime = project.getConfigurations().getByName("testImplementation");
        testRuntime.extendsFrom(alfrescoProvided);

        RepositoryHandler repositories = project.getRepositories();
        // RepositoryHandler does not implement GroovyObject directly, but
        // its implementation or generated delegate will have to implement
        // this class to be usable from groovy code.
        if(repositories instanceof GroovyObject) {
            GroovyObject groovyRepositories = (GroovyObject)repositories;
            // By convention, all groovy objects have an "ext" property, which can be used to add extra fields
            // and functions to an object. All methods an properties that are added to this ext property can be used
            // from Groovy code as if they exist directly on the object.
            ExtraPropertiesExtension ext = (ExtraPropertiesExtension) groovyRepositories.getProperty("ext");
            Closure alfrescoClosure = new Closure(this) {
                public void doCall() {
                    repositories.maven(repo -> {
                        repo.setUrl(ALFRESCO_REPOSITORY_PUBLIC);
                        repo.setName("Alfresco Public");
                    });
                }
            };
            ext.set("alfrescoPublic", alfrescoClosure);
        } else {
            LOGGER.error("Could not register artifacts.alfrescoPublic() extension.");
        }
    }
}
