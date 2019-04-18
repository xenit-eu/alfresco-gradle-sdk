package eu.xenit.gradle.alfrescosdk;

import java.io.File;
import org.gradle.api.DomainObjectSet;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.internal.plugins.PluginManagerInternal.PluginWithId;
import org.gradle.api.internal.project.DefaultProject;
import org.gradle.api.tasks.SourceSet;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.Test;

import static eu.xenit.gradle.alfrescosdk.AlfrescoPlugin.ALFRESCO_PROVIDED;
import static org.junit.Assert.*;


public class AlfrescoPluginTest {

    private DefaultProject getDefaultProject() {
        DefaultProject project = (DefaultProject) ProjectBuilder.builder().build();
        project.getPluginManager().apply(AlfrescoPlugin.class);
        return project;
    }

    @Test
    public void testJavaPluginApplied(){
        DefaultProject project = getDefaultProject();

        //Check if Java plugin is applied
        DomainObjectSet<PluginWithId> javaPlugins = project.getPluginManager().pluginsForId("java");
        assertEquals(1, javaPlugins.size());
    }

    @Test
    public void alfrescoProvidedDependency(){
        DefaultProject project = getDefaultProject();

        //Check alfrescoProvided dependencies are correctly configured
        ConfigurableFileCollection test123JarCollection = project.files(this.getClass().getClassLoader().getResource("test123.jar").getFile());
        File test123Jar = test123JarCollection.getSingleFile();
        project.getDependencies().add(ALFRESCO_PROVIDED, test123JarCollection);

        assertTrue(configurationHasFile("compileClasspath", project, test123Jar));
        assertFalse(configurationHasFile("runtimeClasspath", project, test123Jar));
        assertTrue(configurationHasFile("testRuntimeClasspath", project, test123Jar));
        assertTrue(configurationHasFile("testCompileClasspath", project, test123Jar));
    }

    @Test
    public void alfrescoProvidedSourceSetWithoutMain() {
        DefaultProject project = getDefaultProject();
        AmpBasePlugin ampBasePlugin = project.getPlugins().apply(AmpBasePlugin.class);
        ampBasePlugin.configureAmpSourceSet("share", s -> {});

        ConfigurableFileCollection test123JarCollection = project.files(this.getClass().getClassLoader().getResource("test123.jar").getFile());
        File test123Jar = test123JarCollection.getSingleFile();
        project.getDependencies().add("shareAlfrescoProvided", test123JarCollection);

        assertTrue(project.getConfigurations().getNames().contains(ALFRESCO_PROVIDED));

        assertTrue(configurationHasFile("shareCompileClasspath", project, test123Jar));
        assertFalse(configurationHasFile("shareRuntimeClasspath", project, test123Jar));

        // This configuration should not add anything to the main/test sourceset configurations
        assertFalse(configurationHasFile("compileClasspath", project, test123Jar));
        assertFalse(configurationHasFile("runtimeClasspath", project, test123Jar));
        assertFalse(configurationHasFile("testRuntimeClasspath", project, test123Jar));
        assertFalse(configurationHasFile("testCompileClasspath", project, test123Jar));
    }

    @Test
    public void alfrescoProvidedSourceSetWithMain() {
        DefaultProject project = getDefaultProject();
        AmpBasePlugin ampBasePlugin = project.getPlugins().apply(AmpBasePlugin.class);
        ampBasePlugin.configureAmpSourceSet(SourceSet.MAIN_SOURCE_SET_NAME, s -> {});
        ampBasePlugin.configureAmpSourceSet("share", s -> {});

        assertTrue(project.getConfigurations().getNames().contains(ALFRESCO_PROVIDED));
        assertTrue(project.getConfigurations().getNames().contains("shareAlfrescoProvided"));

    }

    private boolean configurationHasFile(String configurationName, DefaultProject project, File test123Jar) {
        return project.getConfigurations().getAt(configurationName).getResolvedConfiguration().getFiles().contains(test123Jar);
    }

}
