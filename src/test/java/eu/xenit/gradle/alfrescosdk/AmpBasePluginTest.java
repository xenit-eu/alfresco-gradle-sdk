package eu.xenit.gradle.alfrescosdk;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import eu.xenit.gradle.alfrescosdk.internal.tasks.DefaultAmpSourceSet;
import eu.xenit.gradle.alfrescosdk.tasks.AmpSourceSet;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Properties;
import org.gradle.api.internal.plugins.DslObject;
import org.gradle.api.internal.project.DefaultProject;
import org.gradle.api.plugins.JavaPluginConvention;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.SourceSetContainer;
import org.gradle.api.tasks.WriteProperties;
import org.gradle.testfixtures.ProjectBuilder;
import org.gradle.util.GUtil;
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

        project.getPlugins().getPlugin(AmpBasePlugin.class).configureAmpSourceSet(SourceSet.MAIN_SOURCE_SET_NAME, ampSourceSetConfiguration -> {
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

        AmpSourceSet ampSourceSet = project.getPlugins().getPlugin(AmpBasePlugin.class).getAmpSourceSet(SourceSet.MAIN_SOURCE_SET_NAME).get();

        assertEquals(Collections.singleton(project.file("src/main/amp/config")), ampSourceSet.getAmp().getConfig().getSrcDirs());
        assertEquals(Collections.singleton(project.file("src/main/amp/web")), ampSourceSet.getAmp().getWeb().getSrcDirs());
    }

    @Test
    public void additionalSourceSet() {
        DefaultProject project = getDefaultProject();

        // Create and configure a share amp sourceset
        project.getPlugins().getPlugin(AmpBasePlugin.class).configureAmpSourceSet("share", s -> {});

        assertNotNull(project.getTasks().findByName("processShareModuleProperties"));
        assertNotNull(project.getTasks().findByName("processShareFileMappingProperties"));

        AmpSourceSet shareAmpSourceSet = project.getPlugins()
                .getPlugin(AmpBasePlugin.class)
                .getAmpSourceSet("share")
                .get();

        assertEquals(Collections.singleton(project.file("src/share/amp/config")), shareAmpSourceSet.getAmp().getConfig().getSrcDirs());
        assertEquals(Collections.singleton(project.file("src/share/amp/web")), shareAmpSourceSet.getAmp().getWeb().getSrcDirs());
    }

    @Test
    public void autoConfigureSourceSet() throws IOException {
        Path ampFolder = projectFolder.newFolder("src", "main", "amp").toPath();

        File modulePropertiesFile = ampFolder.resolve("module.properties").toFile();
        Properties moduleProperties = new Properties();
        moduleProperties.setProperty("module.id", "test-amp");
        moduleProperties.setProperty("module.version", "1.0.0");
        GUtil.saveProperties(moduleProperties, modulePropertiesFile);

        File fileMappingPropertiesFile = ampFolder.resolve("file-mapping.properties").toFile();
        Properties fileMappingProperties = new Properties();
        fileMappingProperties.setProperty("/override", "/");
        GUtil.saveProperties(fileMappingProperties, fileMappingPropertiesFile);

        DefaultProject project = getDefaultProject();

        // write an amp {} block inside a sourceset
        project.getPlugins().getPlugin(AmpBasePlugin.class).configureAmpSourceSet(SourceSet.MAIN_SOURCE_SET_NAME, s -> {});

        WriteProperties modulePropertiesTask = project.getTasks().withType(WriteProperties.class).findByName("processModuleProperties");
        assertNotNull(modulePropertiesTask);
        assertEquals("test-amp", modulePropertiesTask.getProperties().get("module.id"));
        assertEquals("1.0.0", modulePropertiesTask.getProperties().get("module.version"));

        WriteProperties fileMappingPropertiesTask = project.getTasks().withType(WriteProperties.class).findByName("processFileMappingProperties");
        assertNotNull(fileMappingPropertiesTask);
        assertEquals("/", fileMappingPropertiesTask.getProperties().get("/override"));
    }

    @Test
    public void manualConfigureSourceSet() {
        DefaultProject project = getDefaultProject();

        SourceSetContainer sourceSets = project.getConvention().getPlugin(JavaPluginConvention.class)
                .getSourceSets();

        project.getPlugins().getPlugin(AmpBasePlugin.class).configureAmpSourceSet(SourceSet.MAIN_SOURCE_SET_NAME, ampConfig -> {
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

        AmpSourceSet ampSourceSet = project.getPlugins().getPlugin(AmpBasePlugin.class).getAmpSourceSet(SourceSet.MAIN_SOURCE_SET_NAME).get();

        assertEquals(Collections.singleton(project.file("src/xyz/amp/config")), ampSourceSet.getAmp().getConfig().getSrcDirs());
        assertEquals(new HashSet<>(Arrays.asList(project.file("src/main/amp/web"), project.file("src/xyz/amp/web"))), ampSourceSet.getAmp().getWeb().getSrcDirs());

    }

}
