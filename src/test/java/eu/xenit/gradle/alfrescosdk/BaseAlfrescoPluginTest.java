package eu.xenit.gradle.alfrescosdk;

import java.io.File;
import org.gradle.api.DomainObjectSet;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.internal.plugins.PluginManagerInternal.PluginWithId;
import org.gradle.api.internal.project.DefaultProject;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import static eu.xenit.gradle.alfrescosdk.AlfrescoPlugin.ALFRESCO_PROVIDED;
import static org.junit.Assert.*;


public class BaseAlfrescoPluginTest {

    private DefaultProject getDefaultProject() {
        DefaultProject project = (DefaultProject) ProjectBuilder.builder().build();
        project.getPluginManager().apply(AlfrescoPlugin.class);
        return project;
    }

    @Rule
    public final TemporaryFolder testProjectFolder = new TemporaryFolder();

    @Test
    public void testJavaPluginApplied(){
        DefaultProject project = getDefaultProject();

        //Check if Java plugin is applied
        DomainObjectSet<PluginWithId> javaPlugins = project.getPluginManager().pluginsForId("java");
        assertEquals(1, javaPlugins.size());
    }

    @Test
    public void testAlfrescoOnlyDependency(){
        DefaultProject project = getDefaultProject();

        //Check alfrescoProvided dependencies are correctly configured
        ConfigurableFileCollection test123JarCollection = project.files(this.getClass().getClassLoader().getResource("test123.jar").getFile());
        File test123Jar = test123JarCollection.getSingleFile();
        project.getDependencies().add(ALFRESCO_PROVIDED, test123JarCollection);

        assertTrue(configurationHasFile("compileOnly", project, test123Jar));
        assertTrue(configurationHasFile("testRuntimeClasspath", project, test123Jar));
        assertTrue(configurationHasFile("testCompileClasspath", project, test123Jar));
        assertFalse(configurationHasFile("runtime", project, test123Jar));
    }

    private boolean configurationHasFile(String configurationName, DefaultProject project, File test123Jar) {
        return project.getConfigurations().getAt(configurationName).getResolvedConfiguration().getFiles().contains(test123Jar);
    }

}
