package eu.xenit.gradle.alfrescosdk.internal.tasks;

import eu.xenit.gradle.alfrescosdk.tasks.AmpSourceDirectories;
import groovy.lang.Closure;
import java.io.File;
import java.util.Iterator;
import java.util.Set;
import org.gradle.api.Action;
import org.gradle.api.file.FileCollection;
import org.gradle.api.file.FileTree;
import org.gradle.api.file.FileVisitDetails;
import org.gradle.api.file.FileVisitor;
import org.gradle.api.file.SourceDirectorySet;
import org.gradle.api.internal.file.FileCollectionVisitor;
import org.gradle.api.internal.file.FileSystemSubset.Builder;
import org.gradle.api.internal.file.FileTreeInternal;
import org.gradle.api.specs.Spec;
import org.gradle.api.tasks.StopExecutionException;
import org.gradle.api.tasks.TaskDependency;
import org.gradle.api.tasks.util.PatternFilterable;

public class DefaultAmpSourceDirectories implements AmpSourceDirectories, FileTreeInternal {

    private final SourceDirectorySet sourceDirectorySet;

    DefaultAmpSourceDirectories(SourceDirectorySet sourceDirectorySet) {
        this.sourceDirectorySet = sourceDirectorySet;
    }

    @Override
    public AmpSourceDirectories srcDir(Object srcPaths) {
        sourceDirectorySet.srcDir(srcPaths);
        return this;
    }

    @Override
    public AmpSourceDirectories setSrcDirs(Iterable<?> srcPaths) {
        sourceDirectorySet.setSrcDirs(srcPaths);
        return this;
    }

    @Override
    public Set<File> getSrcDirs() {
        return sourceDirectorySet.getSrcDirs();
    }

    @Override
    public FileTree matching(Closure filterConfigClosure) {
        return sourceDirectorySet.matching(filterConfigClosure);
    }

    @Override
    public FileTree matching(Action<? super PatternFilterable> filterConfigAction) {
        return sourceDirectorySet.matching(filterConfigAction);
    }

    @Override
    public FileTree matching(PatternFilterable patterns) {
        return sourceDirectorySet.matching(patterns);
    }

    @Override
    public FileTree visit(FileVisitor visitor) {
        return sourceDirectorySet.visit(visitor);
    }

    @Override
    public FileTree visit(Closure visitor) {
        return sourceDirectorySet.visit(visitor);
    }

    @Override
    public FileTree visit(Action<? super FileVisitDetails> visitor) {
        return sourceDirectorySet.visit(visitor);
    }

    @Override
    public FileTree plus(FileTree fileTree) {
        return sourceDirectorySet.plus(fileTree);
    }

    @Override
    public FileTree getAsFileTree() {
        return sourceDirectorySet.getAsFileTree();
    }

    @Override
    public void addToAntBuilder(Object builder, String nodeName, AntType type) {
        sourceDirectorySet.addToAntBuilder(builder, nodeName, type);
    }

    @Override
    public Object addToAntBuilder(Object builder, String nodeName) {
        return sourceDirectorySet.addToAntBuilder(builder, nodeName);
    }

    @Override
    public File getSingleFile() throws IllegalStateException {
        return sourceDirectorySet.getSingleFile();
    }

    @Override
    public Set<File> getFiles() {
        return sourceDirectorySet.getFiles();
    }

    @Override
    public boolean contains(File file) {
        return sourceDirectorySet.contains(file);
    }

    @Override
    public String getAsPath() {
        return sourceDirectorySet.getAsPath();
    }

    @Override
    public FileCollection plus(FileCollection collection) {
        return sourceDirectorySet.plus(collection);
    }

    @Override
    public FileCollection minus(FileCollection collection) {
        return sourceDirectorySet.minus(collection);
    }

    @Override
    public FileCollection filter(Closure filterClosure) {
        return sourceDirectorySet.filter(filterClosure);
    }

    @Override
    public FileCollection filter(Spec<? super File> filterSpec) {
        return sourceDirectorySet.filter(filterSpec);
    }

    @Override
    @Deprecated
    public Object asType(Class<?> type) {
        return sourceDirectorySet.asType(type);
    }

    @Override
    @Deprecated
    public FileCollection add(FileCollection collection) throws UnsupportedOperationException {
        return sourceDirectorySet.add(collection);
    }

    @Override
    public boolean isEmpty() {
        return sourceDirectorySet.isEmpty();
    }

    @Override
    @Deprecated
    public FileCollection stopExecutionIfEmpty() throws StopExecutionException {
        return sourceDirectorySet.stopExecutionIfEmpty();
    }

    @Override
    public Iterator<File> iterator() {
        return sourceDirectorySet.iterator();
    }

    @Override
    public TaskDependency getBuildDependencies() {
        return sourceDirectorySet.getBuildDependencies();
    }

    @Override
    public void visitTreeOrBackingFile(FileVisitor visitor) {
        ((FileTreeInternal)sourceDirectorySet).visitTreeOrBackingFile(visitor);
    }

    @Override
    public void registerWatchPoints(Builder builder) {
        ((FileTreeInternal)sourceDirectorySet).registerWatchPoints(builder);
    }

    @Override
    public void visitRootElements(FileCollectionVisitor visitor) {
        ((FileTreeInternal)sourceDirectorySet).visitRootElements(visitor);
    }
}
