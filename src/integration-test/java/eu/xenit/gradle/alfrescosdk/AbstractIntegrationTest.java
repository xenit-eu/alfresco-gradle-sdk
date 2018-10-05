package eu.xenit.gradle.alfrescosdk;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import org.apache.commons.io.FileUtils;
import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;
import org.gradle.testkit.runner.TaskOutcome;
import org.junit.rules.TemporaryFolder;

/**
 * Created by thijs on 3/2/17.
 */
public abstract class AbstractIntegrationTest {
    protected void testProjectFolderThatShouldFail(Path projectFolder, String task) throws IOException {
        testProjectFolder(projectFolder, task, true);
    }
    protected void testProjectFolder(Path projectFolder, String task)throws IOException {
        testProjectFolder(projectFolder,task,false);
    }
    protected void testProjectFolder(Path projectFolder, String task, boolean expectsException) throws IOException {
        TemporaryFolder temporaryFolder = new TemporaryFolder();
        temporaryFolder.create();

        try {
            File tempExample = temporaryFolder.newFolder(projectFolder.getFileName().toString());
            FileUtils.copyDirectory(projectFolder.toFile(), tempExample);
            System.out.println("Executing test build for example " + tempExample.getName());
            BuildResult result = GradleRunner.create()
                    .withProjectDir(tempExample)
                    .withArguments(task, "--stacktrace", "--rerun-tasks", "-i")
                    .withTestKitDir(new File(System.getProperty("user.home") + File.separator + ".gradle"))
                    .withPluginClasspath()
                    .withDebug(true)
                    .forwardOutput()
                    .build();

            if(expectsException){
                assert false;
            }else{
                assertEquals(TaskOutcome.SUCCESS, result.task(task).getOutcome());
            }
        } catch(Exception e){
            e.printStackTrace();
            assert expectsException;
        }

        finally {
            temporaryFolder.delete();
        }
    }

    protected void testProjectFolder(Path projectFolder) throws IOException {
        testProjectFolder(projectFolder, ":amp");
    }
}
