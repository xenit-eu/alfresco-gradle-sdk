package eu.xenit.gradle.alfrescosdk;

import eu.xenit.gradle.alfrescosdk.config.AmpConfig;
import eu.xenit.gradle.alfrescosdk.tasks.Amp;
import java.io.File;
import org.gradle.api.internal.project.DefaultProject;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.Test;
import static org.junit.Assert.*;

public class AmpPluginTest {

    private DefaultProject getDefaultProject() {
        DefaultProject project = (DefaultProject) ProjectBuilder.builder().build();
        project.getPluginManager().apply(AmpPlugin.class);
        return project;
    }

    @Test
    public void testConfigFolderEmpty(){
        DefaultProject defaultProject = getDefaultProject();
        Amp ampTask = (Amp) defaultProject.getTasks().getByName("amp");
        assertNull(ampTask.getConfig());
    }

    @Test
    public void testConfigDirectoryExists(){
        DefaultProject defaultProject = getDefaultProject();

        File configDir = new File(defaultProject.getProjectDir().getAbsolutePath()+"/src/main/amp/config");
        assertTrue(configDir.mkdirs());

        Amp ampTask = (Amp) defaultProject.getTasks().getByName("amp");
        assertTrue(ampTask.getConfig().isDirectory());
    }

    @Test
    public void testDeProjectWithEmptyLib(){
        DefaultProject project = getDefaultProject();
        project.getPluginManager().apply(AlfrescoPlugin.class);
        project.getExtensions().configure("ampConfig", (AmpConfig ampConfig) -> ampConfig.setDynamicExtension(true));

        Amp ampTask = (Amp) project.getTasks().getByName("amp");
        project.evaluate(); // Evaluate the project so that the afterEvaluate can run, which should clear the libs
        assertNull(ampTask.getLibs());
    }

    @Test
    public void testDeProjectWithConfiguredLib(){
        DefaultProject project = getDefaultProject();
        project.getPluginManager().apply(AlfrescoPlugin.class);
        project.getExtensions().configure("ampConfig", (AmpConfig ampConfig) -> ampConfig.setDynamicExtension(true));

        Amp ampTask = (Amp) project.getTasks().getByName("amp");
        ampTask.setLibs(project.files("this/doesnt/exist"));
        project.evaluate(); // Evaluate the project so that the afterEvaluate can run, which should leave the libs alone
        assertNotNull(ampTask.getLibs());
    }
    @Test
    public void testProjectWithConfiguredConfigDir(){
        DefaultProject project = getDefaultProject();
        project.getPluginManager().apply(AlfrescoPlugin.class);
        File need_this = new File("Need this");
        project.getExtensions().configure("ampConfig", (AmpConfig ampConfig) -> {
            ampConfig.setConfigDir(need_this);
        });
        Amp ampTask = (Amp) project.getTasks().getByName("amp");
        project.evaluate(); // Evaluate the project so that the afterEvaluate can run, which should leave the libs alone
        assertEquals(need_this,ampTask.getConfig()); ;
    }

}
