package eu.xenit.gradle.alfrescosdk;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import eu.xenit.gradle.alfrescosdk.internal.PropertiesUtil;
import eu.xenit.gradle.alfrescosdk.tasks.Amp;
import eu.xenit.gradle.alfrescosdk.tasks.AmpSourceSetConfiguration;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Properties;
import org.gradle.api.internal.project.DefaultProject;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.WriteProperties;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class AmpBasePluginTest {
    @Rule
    public TemporaryFolder projectFolder = new TemporaryFolder();

    private DefaultProject getDefaultProject() {
        DefaultProject project = (DefaultProject) ProjectBuilder.builder()
                .withProjectDir(projectFolder.getRoot())
                .build();
        project.getPluginManager().apply(AmpBasePlugin.class);
        return project;
    }

    @Test
    public void mainSourceSetOnly() {
        DefaultProject project = getDefaultProject();

        project.getPlugins().getPlugin(AmpBasePlugin.class).configureAmpSourceSetConfiguration(SourceSet.MAIN_SOURCE_SET_NAME, ampSourceSetConfiguration -> {
            ampSourceSetConfiguration.module(properties -> {
                properties.setProperty("module.id", "test-module-repo");
                properties.setProperty("module.version", "1.0.0");
                properties.setProperty("module.title", "Test Module Repo");
                properties.setProperty("module.description", "Blabla");
            });
        });


        WriteProperties modulePropertiesTask = project.getTasks().withType(WriteProperties.class).findByName("processModuleProperties");
        assertNotNull(modulePropertiesTask);

        assertEquals("test-module-repo", modulePropertiesTask.getProperties().get("module.id"));
        assertEquals("1.0.0", modulePropertiesTask.getProperties().get("module.version"));
        assertEquals("Test Module Repo", modulePropertiesTask.getProperties().get("module.title"));
        assertEquals("Blabla", modulePropertiesTask.getProperties().get("module.description"));

        WriteProperties fileMappingPropertiesTask = project.getTasks().withType(WriteProperties.class).findByName("processFileMappingProperties");
        assertNotNull(fileMappingPropertiesTask);

        AmpSourceSetConfiguration ampSourceSet = project.getPlugins().getPlugin(AmpBasePlugin.class).getAmpSourceSetConfiguration(SourceSet.MAIN_SOURCE_SET_NAME).get();

        assertEquals(Collections.singleton(project.file("src/main/amp/config")), ampSourceSet.getConfig().getSrcDirs());
        assertEquals(Collections.singleton(project.file("src/main/amp/web")), ampSourceSet.getWeb().getSrcDirs());
    }

    @Test
    public void additionalSourceSet() {
        DefaultProject project = getDefaultProject();

        // Create and configure a share amp sourceset
        project.getPlugins().getPlugin(AmpBasePlugin.class).configureAmpSourceSetConfiguration("share", s -> {});

        assertNotNull(project.getTasks().findByName("processShareModuleProperties"));
        assertNotNull(project.getTasks().findByName("processShareFileMappingProperties"));
        assertTrue(project.getTasks().getNames().contains("shareAmp"));

        AmpSourceSetConfiguration shareAmpSourceSet = project.getPlugins()
                .getPlugin(AmpBasePlugin.class)
                .getAmpSourceSetConfiguration("share")
                .get();

        assertEquals(Collections.singleton(project.file("src/share/amp/config")), shareAmpSourceSet.getConfig().getSrcDirs());
        assertEquals(Collections.singleton(project.file("src/share/amp/web")), shareAmpSourceSet.getWeb().getSrcDirs());
    }

    @Test
    public void autoConfigureSourceSet() throws IOException {
        Path ampFolder = projectFolder.newFolder("src", "main", "amp").toPath();

        File modulePropertiesFile = ampFolder.resolve("module.properties").toFile();
        Properties moduleProperties = new Properties();
        moduleProperties.setProperty("module.id", "test-amp");
        moduleProperties.setProperty("module.version", "1.0.0");
        PropertiesUtil.saveProperties(moduleProperties, modulePropertiesFile);

        File fileMappingPropertiesFile = ampFolder.resolve("file-mapping.properties").toFile();
        Properties fileMappingProperties = new Properties();
        fileMappingProperties.setProperty("/override", "/");
        PropertiesUtil.saveProperties(fileMappingProperties, fileMappingPropertiesFile);

        DefaultProject project = getDefaultProject();
        // Java plugin is required for the jar task
        project.getPlugins().apply(JavaPlugin.class);

        // write an amp {} block inside a sourceset
        project.getPlugins().getPlugin(AmpBasePlugin.class).configureAmpSourceSetConfiguration(SourceSet.MAIN_SOURCE_SET_NAME, s -> {});

        WriteProperties modulePropertiesTask = project.getTasks().withType(WriteProperties.class).findByName("processModuleProperties");
        assertNotNull(modulePropertiesTask);
        assertEquals("test-amp", modulePropertiesTask.getProperties().get("module.id"));
        assertEquals("1.0.0", modulePropertiesTask.getProperties().get("module.version"));

        WriteProperties fileMappingPropertiesTask = project.getTasks().withType(WriteProperties.class).findByName("processFileMappingProperties");
        assertNotNull(fileMappingPropertiesTask);
        assertEquals("/", fileMappingPropertiesTask.getProperties().get("/override"));

        Amp ampTask = project.getTasks().withType(Amp.class).findByName("amp");
        assertNotNull(ampTask);

        assertTrue(ampTask.getDeBundles().isEmpty());
        assertFalse(ampTask.getLibs().isEmpty());
        assertEquals(modulePropertiesTask.getOutputFile(), ampTask.getModuleProperties());
        assertEquals(fileMappingPropertiesTask.getOutputFile(), ampTask.getFileMappingProperties());
    }

    @Test
    public void manualConfigureSourceSet() {
        DefaultProject project = getDefaultProject();

        project.getPlugins().getPlugin(AmpBasePlugin.class).configureAmpSourceSetConfiguration(SourceSet.MAIN_SOURCE_SET_NAME, ampConfig -> {
            ampConfig.module(moduleProperties -> {
                moduleProperties.setProperty("module.id", "test-amp");
                moduleProperties.setProperty("module.version", "1.0.0");
            });

            ampConfig.web(sourceDirs -> {
                sourceDirs.srcDir("src/xyz/amp/web");
            });

            ampConfig.config(sourceDirs -> {
                sourceDirs.setSrcDirs(Collections.singleton("src/xyz/amp/config"));
            });
        });

        WriteProperties modulePropertiesTask = project.getTasks().withType(WriteProperties.class).findByName("processModuleProperties");
        assertNotNull(modulePropertiesTask);
        assertEquals("test-amp", modulePropertiesTask.getProperties().get("module.id"));
        assertEquals("1.0.0", modulePropertiesTask.getProperties().get("module.version"));

        AmpSourceSetConfiguration ampSourceSet = project.getPlugins().getPlugin(AmpBasePlugin.class).getAmpSourceSetConfiguration(SourceSet.MAIN_SOURCE_SET_NAME).get();

        assertEquals(Collections.singleton(project.file("src/xyz/amp/config")), ampSourceSet.getConfig().getSrcDirs());
        assertEquals(new HashSet<>(Arrays.asList(project.file("src/main/amp/web"), project.file("src/xyz/amp/web"))), ampSourceSet.getWeb().getSrcDirs());

    }

}
