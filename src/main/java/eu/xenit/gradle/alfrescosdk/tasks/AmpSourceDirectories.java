package eu.xenit.gradle.alfrescosdk.tasks;

import java.io.File;
import java.util.Set;
import org.gradle.api.file.FileTree;

public interface AmpSourceDirectories extends FileTree {
    AmpSourceDirectories srcDir(Object srcPaths);

    default AmpSourceDirectories srcDirs(Object... srcPaths) {
        for (Object srcPath : srcPaths) {
            srcDir(srcPath);
        }
        return this;
    }

    AmpSourceDirectories setSrcDirs(Iterable<?> srcPaths);

    Set<File> getSrcDirs();
}
