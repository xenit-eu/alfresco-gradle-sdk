package eu.xenit.gradle.alfrescosdk;

import eu.xenit.gradle.alfrescosdk.config.AmpConfig;
import eu.xenit.gradle.alfrescosdk.tasks.Amp;
import java.io.File;
import org.gradle.api.internal.project.DefaultProject;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import static org.junit.Assert.*;

public class AmpLegacyPluginTest {

    @Rule
    public TemporaryFolder projectFolder = new TemporaryFolder();

    private DefaultProject getDefaultProject() {
        DefaultProject project = (DefaultProject) ProjectBuilder.builder()
                .withProjectDir(projectFolder.getRoot())
                .build();
        project.getPluginManager().apply(AmpPlugin.class);
        return project;
    }

    @Test
    public void testConfigFolderEmpty(){
        DefaultProject defaultProject = getDefaultProject();
        defaultProject.evaluate();
        Amp ampTask = (Amp) defaultProject.getTasks().getByName("amp");
        assertNotNull(ampTask);
        assertNull(ampTask.getConfig());
    }

    @Test
    public void testConfigDirectoryExists(){
        DefaultProject defaultProject = getDefaultProject();

        File configDir = new File(defaultProject.getProjectDir().getAbsolutePath()+"/src/main/amp/config");
        assertTrue(configDir.mkdirs());

        defaultProject.evaluate();
        Amp ampTask = (Amp) defaultProject.getTasks().getByName("amp");
        assertNotNull(ampTask);
        assertNotNull(ampTask.getConfig());
        assertTrue(ampTask.getConfig().isDirectory());
    }

    @Test
    public void testDeProjectWithEmptyLib(){
        DefaultProject project = getDefaultProject();
        project.getPluginManager().apply(AlfrescoPlugin.class);
        project.getExtensions().configure("ampConfig", (AmpConfig ampConfig) -> ampConfig.setDynamicExtension(true));

        project.evaluate();
        Amp ampTask = (Amp) project.getTasks().getByName("amp");
        assertNotNull(ampTask);
        project.evaluate(); // Evaluate the project so that the afterEvaluate can run, which should clear the libs
        assertTrue(ampTask.getLibs().isEmpty());
        assertFalse(ampTask.getDeBundles().isEmpty());
    }

    @Test
    public void testDeProjectWithConfiguredLib(){
        DefaultProject project = getDefaultProject();
        project.getPluginManager().apply(AlfrescoPlugin.class);
        project.getExtensions().configure("ampConfig", (AmpConfig ampConfig) -> ampConfig.setDynamicExtension(true));

        project.evaluate();
        Amp ampTask = (Amp) project.getTasks().getByName("amp");
        assertNotNull(ampTask);
        ampTask.setLibs(project.files("this/doesnt/exist"));
        project.evaluate(); // Evaluate the project so that the afterEvaluate can run, which should leave the libs alone
        assertFalse(ampTask.getLibs().isEmpty());
        assertFalse(ampTask.getDeBundles().isEmpty());
    }
    @Test
    public void testProjectWithConfiguredConfigDir(){
        DefaultProject project = getDefaultProject();
        project.getPluginManager().apply(AlfrescoPlugin.class);
        File need_this = project.file("Need this");
        project.getExtensions().configure("ampConfig", (AmpConfig ampConfig) -> {
            ampConfig.setConfigDir(need_this);
        });
        project.evaluate(); // Evaluate the project so that the afterEvaluate can run, which should leave the libs alone
        Amp ampTask = (Amp) project.getTasks().getByName("amp");
        assertNotNull(ampTask);
        assertEquals(need_this,ampTask.getConfig());
    }

}
