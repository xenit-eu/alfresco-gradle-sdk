package eu.xenit.gradle.alfrescosdk;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertArrayEquals;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Predicate;
import org.junit.Test;

public class Examples extends AbstractIntegrationTest {

    private static Path EXAMPLES = Paths.get("src", "integrationTest", "resources", "examples");

    private static void assertPath(Predicate<Path> check, Path path) {
        assertTrue(path.toString(), check.test(path));
    }

    @Test
    public void testSimpleAlfrescoProject() throws IOException {
        testProjectFolder(EXAMPLES.resolve("simple-alfresco-project"));

        Path buildFolder = projectFolder.toPath().resolve("build");

        Path ampFile = buildFolder.resolve("dist/simple-alfresco-project-0.0.1.amp");
        Path jarFile = buildFolder.resolve("libs/simple-alfresco-project-0.0.1.jar");

        assertPath(Files::isRegularFile, ampFile);
        assertPath(Files::isRegularFile, jarFile);

        FileSystem ampFs = FileSystems.newFileSystem(ampFile, null);
        assertPath(Files::isRegularFile, ampFs.getPath("module.properties"));
        assertPath(Files::isRegularFile,ampFs.getPath("config/alfresco/extension/templates/webscripts/eu/xenit/alfresco/gradle/sample/helloworld.get.desc.xml"));
        assertPath(Files::isRegularFile,ampFs.getPath("config/alfresco/module/example-default-amp/module-context.xml"));
        Path packagedJarFile = ampFs.getPath("lib/simple-alfresco-project-0.0.1.jar");
        assertPath(Files::isRegularFile, packagedJarFile);
        assertArrayEquals("Jar inside amp is not identical to jar outside amp", Files.readAllBytes(jarFile), Files.readAllBytes(packagedJarFile));
        ampFs.close();

        FileSystem jarFs = FileSystems.newFileSystem(jarFile, null);
        assertPath(Files::isRegularFile, jarFs.getPath("eu/xenit/alfresco/gradle/sample/HelloWorldWebScript.class"));
        jarFs.close();
    }

    @Test
    public void testSimpleDeProject() throws IOException {
        testProjectFolder(EXAMPLES.resolve("simple-de-project"));

        Path buildFolder = projectFolder.toPath().resolve("build");

        Path ampFile = buildFolder.resolve("dist/simple-de-project-0.0.1.amp");
        Path jarFile = buildFolder.resolve("libs/simple-de-project-0.0.1.jar");

        assertPath(Files::isRegularFile, ampFile);
        assertPath(Files::isRegularFile, jarFile);

        FileSystem ampFs = FileSystems.newFileSystem(ampFile, null);
        assertPath(Files::isRegularFile, ampFs.getPath("module.properties"));
        assertPath(Files::notExists, ampFs.getPath("lib/simple-de-project-0.0.1.jar"));
        Path packagedJarFile = ampFs.getPath("config/dynamic-extensions/bundles/simple-de-project-0.0.1.jar");
        assertPath(Files::isRegularFile, packagedJarFile);
        assertArrayEquals("Jar inside amp is not identical to jar outside amp", Files.readAllBytes(jarFile), Files.readAllBytes(packagedJarFile));
        ampFs.close();

        FileSystem jarFs = FileSystems.newFileSystem(jarFile, null);
        assertPath(Files::isRegularFile, jarFs.getPath("eu/xenit/alfresco/gradle/sample/HelloWorldWebScript.class"));
        jarFs.close();
    }

    @Test
    public void testConfiguredAlfrescoProject() throws IOException {
        testProjectFolder(EXAMPLES.resolve("configured-alfresco-project"));

        Path buildFolder = projectFolder.toPath().resolve("build");
        Path ampFile = buildFolder.resolve("dist/configured-alfresco-project-0.0.1.amp");
        Path jarFile = buildFolder.resolve("libs/configured-alfresco-project-0.0.1.jar");

        assertPath(Files::isRegularFile, ampFile);
        assertPath(Files::isRegularFile, jarFile);

        FileSystem ampFs = FileSystems.newFileSystem(ampFile, null);
        assertPath(Files::isRegularFile, ampFs.getPath("module.properties"));
        assertPath(Files::isRegularFile,ampFs.getPath("config/alfresco/extension/templates/webscripts/eu/xenit/alfresco/gradle/sample/contenttypedetection.get.desc.xml"));
        assertPath(Files::isRegularFile,ampFs.getPath("config/alfresco/module/configured-default-amp/module-context.xml"));
        assertPath(Files::isRegularFile,ampFs.getPath("web/index.html"));
        assertPath(Files::isRegularFile, ampFs.getPath("lib/tika-core-1.20.jar"));
        Path packagedJarFile = ampFs.getPath("lib/configured-alfresco-project-0.0.1.jar");
        assertPath(Files::isRegularFile, packagedJarFile);
        assertArrayEquals("Jar inside amp is not identical to jar outside amp", Files.readAllBytes(jarFile), Files.readAllBytes(packagedJarFile));
        ampFs.close();

        FileSystem jarFs = FileSystems.newFileSystem(jarFile, null);
        assertPath(Files::isRegularFile, jarFs.getPath("eu/xenit/alfresco/gradle/sample/ContentTypeDetectionWebScript.class"));
        jarFs.close();


    }

    @Test
    public void multiSourceAlfrescoProject() throws IOException {
        testProjectFolder(EXAMPLES.resolve("multi-source-alfresco-project"), ":assemble");
    }
}
