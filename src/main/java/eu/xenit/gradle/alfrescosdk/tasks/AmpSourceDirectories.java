package eu.xenit.gradle.alfrescosdk.tasks;

import org.gradle.api.file.SourceDirectorySet;

import java.io.File;
import java.util.Set;

public interface AmpSourceDirectories extends SourceDirectorySet {
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
