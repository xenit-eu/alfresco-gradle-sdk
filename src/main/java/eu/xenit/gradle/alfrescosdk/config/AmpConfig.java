package eu.xenit.gradle.alfrescosdk.config;

import java.io.File;
import java.util.function.Supplier;
import org.gradle.api.PathValidation;
import org.gradle.api.Project;

public class AmpConfig {

    private Supplier<File> moduleProperties;
    private Supplier<File> configDir;
    private Supplier<File> webDir;
    private Supplier<File> fileMappingProperties ;

    private final Project project;
    public AmpConfig(Project project){
        this.project = project;
        moduleProperties = () -> project.file("src/main/amp/module.properties", PathValidation.FILE);
        configDir =  () -> project.file("src/main/amp/config", PathValidation.DIRECTORY);
        webDir =  () -> project.file("src/main/amp/web", PathValidation.DIRECTORY);
    }

    public File getModuleProperties() {
        return moduleProperties.get();
    }

    public void setModuleProperties(File moduleProperties) {
        this.moduleProperties = () -> moduleProperties;
    }

    public File getConfigDir() {
        return configDir.get();
    }

    public void setConfigDir(File configDir) {
        this.configDir = () -> configDir;
    }

    public File getWebDir() {
        return webDir.get();
    }

    public void setWebDir(File webDir) {
        this.webDir = () -> webDir;
    }

    public File getFileMappingProperties() {
        return fileMappingProperties.get();
    }

    public void setModuleProperties(Supplier<File> moduleProperties) {
        this.moduleProperties = moduleProperties;
    }

    public void setConfigDir(Supplier<File> configDir) {
        this.configDir = configDir;
    }

    public void setWebDir(Supplier<File> webDir) {
        this.webDir = webDir;
    }

    public void setFileMappingProperties(Supplier<File> fileMappingProperties) {
        this.fileMappingProperties = fileMappingProperties;
    }

    public Project getProject() {
        return project;
    }

    public void setFileMappingProperties(File fileMappingProperties) {
        this.fileMappingProperties = () -> fileMappingProperties;
    }

    public Supplier<File> getModulePropertiesSupplier() {
        return this.moduleProperties;
    }

    public Supplier<File> getConfigDirSupplier() {
        return this.configDir;
    }

    public Supplier<File> getWebDirSupplier() {
        return this.webDir;
    }

    public Supplier<File> getFileMappingPropertiesSupplier() {
        return this.fileMappingProperties;
    }
}
