package eu.xenit.gradle.alfrescosdk;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.junit.Test;

public class AmpTest extends AbstractIntegrationTest {

    private static Path AMP = Paths.get("src", "integrationTest", "resources", "tasks", "amp");

    @Test
    public void testEmptyAmpTask() throws IOException {
        Path projectFolder = AMP.resolve("empty-amp-task");
        Path tempFolder = getTempCopy(projectFolder);
        testProjectFolderInPlace(tempFolder, "amp");

        File outputAmp = tempFolder.resolve("build").resolve("amp").resolve("amp.amp").toFile();

        Map<String, Path> pathsInAmp = getPathsInZip(outputAmp).collect(Collectors.toMap(Path::toString, p -> p));

        assertTrue(pathsInAmp.containsKey("/module.properties"));

        assertEquals( "a=b\nd=eu.xenit.gradle.alfrescosdk.test\n", readFile(pathsInAmp.get("/module.properties")));
    }

    private String readFile(Path filename) throws IOException {
        byte[] bytes = Files.readAllBytes(filename);
        return new String(bytes);
    }

    private Stream<Path> getPathsInZip(File zipFile) {
        URI u = URI.create("jar:"+zipFile.toURI().toString());

        FileSystem zipFs = null;
        try {
            zipFs = FileSystems.newFileSystem(u, Collections.emptyMap());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return StreamSupport.stream(zipFs.getRootDirectories().spliterator(), false)
                .flatMap((root) -> {
                    try {
                        return Files.walk(root);
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                });
    }

}
