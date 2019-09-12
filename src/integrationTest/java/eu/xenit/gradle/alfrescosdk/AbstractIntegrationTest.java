package eu.xenit.gradle.alfrescosdk;

import org.apache.commons.io.FileUtils;
import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public abstract class AbstractIntegrationTest {
    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();

    public File projectFolder;

    @Parameter(0)
    public String gradleVersion;

    @Parameters(name = "Gradle v{0}")
    public static Collection<Object[]> testData() {
        return Arrays.asList(new Object[][]{
                {"5.3.1"},
                {"5.2.1"},
                {"5.1.1"},
                {"5.0"},
        });
    }

    protected BuildResult testProjectFolderThatShouldFail(Path projectFolder, String task) throws IOException {
        return createRunner(projectFolder, task).buildAndFail();
    }

    private GradleRunner createRunner(Path projectFolder, String task) throws IOException {
        File tempExample = testFolder.newFolder(projectFolder.getFileName().toString());
        this.projectFolder = tempExample;
        FileUtils.copyDirectory(projectFolder.toFile(), tempExample);
        System.out.println("Executing test build for example " + tempExample.getName());
        return GradleRunner.create()
                .withProjectDir(tempExample)
                .withGradleVersion(gradleVersion)
                .withArguments(task, "--stacktrace", "-i")
                .withPluginClasspath()
                .withDebug(true)
                .forwardOutput();
    }

    protected BuildResult testProjectFolder(Path projectFolder, String task) throws IOException {
        return createRunner(projectFolder, task).build();
    }

    protected BuildResult testProjectFolder(Path projectFolder) throws IOException {
        return testProjectFolder(projectFolder, ":amp");
    }
}
