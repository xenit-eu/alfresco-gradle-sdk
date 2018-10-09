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

        File configDir = new File(defaultProject.getProjectDir().getAbsolutePath()+"/"+AmpConfig.DEFAULT_CONFIG_DIR);
        assertTrue(configDir.mkdirs());

        Amp ampTask = (Amp) defaultProject.getTasks().getByName("amp");
        assertTrue(ampTask.getConfig().isDirectory());
    }

}
