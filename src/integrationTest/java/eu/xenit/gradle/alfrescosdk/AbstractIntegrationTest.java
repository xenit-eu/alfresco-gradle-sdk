package eu.xenit.gradle.alfrescosdk;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import org.apache.commons.io.FileUtils;
import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;
import org.gradle.testkit.runner.TaskOutcome;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;

/**
 * Created by thijs on 3/2/17.
 */
public abstract class AbstractIntegrationTest {
    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();

    public File projectFolder;

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
                .withArguments(task, "--stacktrace", "--rerun-tasks", "-i")
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
