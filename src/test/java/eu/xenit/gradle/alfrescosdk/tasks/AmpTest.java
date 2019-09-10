package eu.xenit.gradle.alfrescosdk.tasks;

import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.internal.TaskInternal;
import org.gradle.api.internal.project.DefaultProject;
import org.gradle.api.internal.tasks.TaskExecuter;
import org.gradle.api.internal.tasks.TaskStateInternal;
import org.gradle.api.internal.tasks.execution.DefaultTaskExecutionContext;
import org.gradle.execution.plan.LocalTaskNode;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.*;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class AmpTest {
    private DefaultProject getDefaultProject() throws IOException {
        Project project =  ProjectBuilder.builder().build();
        Amp ampTask = project.getTasks().create("amp", Amp.class);
        ampTask.setDestinationDir(testProjectDir.newFolder("build"));
        return (DefaultProject) project;
    }

    @Rule
    public final TemporaryFolder testProjectDir = new TemporaryFolder();

    @Test
    public void testEmptyAmpTask() throws IOException {
        DefaultProject project = getDefaultProject();
        project.setGroup("eu.xenit.gradle.alfrescosdk.test");

        File moduleProperties = createFile("module.properties", "a=b\nd=${project.group}\n");

        project.evaluate();

        Amp ampTask = (Amp) project.getTasks().getByName("amp");
        ampTask.setModuleProperties(moduleProperties);
        //TODO: Following needs to be migrated to support latest Gradle versions.
//        execute(ampTask);

//        Optional<File> outputAmp = Stream.of(ampTask.getDestinationDir().listFiles())
//                .findFirst();
//
//        assertTrue(outputAmp.isPresent());
//
//        Map<String, Path> pathsInAmp = getPathsInZip(outputAmp.get()).collect(Collectors.toMap(Path::toString, p -> p));
//
//        assertTrue(pathsInAmp.containsKey("/module.properties"));
//
//        assertEquals( "a=b\nd=eu.xenit.gradle.alfrescosdk.test\n", readFile(pathsInAmp.get("/module.properties")));
    }

    private File createFile(String filename, String content) throws IOException {
        File file = testProjectDir.newFile(filename);
        OutputStream outputStream = new FileOutputStream(file);
        Writer writer = new OutputStreamWriter(outputStream);
        writer.write(content);
        writer.close();
        outputStream.close();
        return file;
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

    private void execute(Task task) {
        final TaskExecuter taskExecuter = ((DefaultProject) task.getProject()).getServices().get(TaskExecuter.class);
        taskExecuter.execute((TaskInternal)task, (TaskStateInternal) task.getState(), new DefaultTaskExecutionContext(new LocalTaskNode((TaskInternal) task)));

        
        task.getState().rethrowFailure();
    }

}
