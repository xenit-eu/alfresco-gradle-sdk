package eu.xenit.gradle.alfrescosdk;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.Test;

public class Examples extends AbstractIntegrationTest {

    private static Path EXAMPLES = Paths.get("src", "integration-test", "resources", "examples");

    @Test
    public void testSimpleAlfrescoProject() throws IOException {
        testProjectFolder(EXAMPLES.resolve("simple-alfresco-project"));
    }

}
