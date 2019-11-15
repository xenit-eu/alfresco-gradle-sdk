package eu.xenit.gradle.alfrescosdk;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import org.apache.commons.io.FileUtils;
import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

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
                {"6.0"},
                {"5.6.4"},
                {"5.5.1"},
                {"5.4.1"},
                {"5.3.1"},
                {"5.2.1"},
                {"5.1.1"}
        });
    }

    protected BuildResult testProjectFolderThatShouldFail(Path projectFolder, String task) throws IOException {
        return createRunner(projectFolder, task).buildAndFail();
    }

    private GradleRunner createRunner(Path projectFolder, String task) throws IOException {
        Path tempExample = getTempCopy(projectFolder);
        return createRunnerInPlace(tempExample, task);
    }

    private GradleRunner createRunnerInPlace(Path tempExample, String task) {
        File tempExampleFile = tempExample.toFile();
        System.out.println("Executing test build for example " + tempExampleFile.getName());
        return GradleRunner.create()
                .withProjectDir(tempExampleFile)
                .withGradleVersion(gradleVersion)
                .withArguments(task, "--stacktrace", "-i")
                .withPluginClasspath()
                .withDebug(true)
                .forwardOutput();
    }

    protected Path getTempCopy(Path projectFolder) throws IOException {
        File tempExample = testFolder.newFolder(projectFolder.getFileName().toString());
        this.projectFolder = tempExample;
        FileUtils.copyDirectory(projectFolder.toFile(), tempExample);
        return tempExample.toPath();
    }

    protected BuildResult testProjectFolder(Path projectFolder, String task) throws IOException {
        return createRunner(projectFolder, task).build();
    }

    protected BuildResult testProjectFolderInPlace(Path projectFolder, String task) throws IOException {
        return createRunnerInPlace(projectFolder, task).build();
    }

    protected BuildResult testProjectFolder(Path projectFolder) throws IOException {
        return testProjectFolder(projectFolder, ":amp");
    }
}
