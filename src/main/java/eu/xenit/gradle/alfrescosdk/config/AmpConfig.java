package eu.xenit.gradle.alfrescosdk.config;

import java.io.File;
import java.util.function.Supplier;
import org.gradle.api.Project;

public class AmpConfig {

    public static final String DEFAULT_CONFIG_DIR = "src/main/amp/config";
    public static final String DEFAULT_MODULE_PROPERTIES = "src/main/amp/module.properties";
    public static final String DEFAULT_WEB_DIR = "src/main/amp/web";
    private Supplier<File> moduleProperties;
    private Supplier<File> configDir;
    private Supplier<File> webDir;
    private Supplier<File> fileMappingProperties;
    private boolean dynamicExtension = false;

    private final Project project;
    public AmpConfig(Project project){
        this.project = project;
        moduleProperties = () -> project.file(DEFAULT_MODULE_PROPERTIES);
        configDir =  () -> {
            File file = project.file(DEFAULT_CONFIG_DIR);
            if(file.exists()) {
                return file;
            } else {
                return null;
            }
        };
        webDir =  () -> {
            File file = project.file(DEFAULT_WEB_DIR);
            if(file.exists()) {
                return file;
            } else {
                return null;
            }
        };
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

    public boolean getDynamicExtension() {
        return this.dynamicExtension;
    }

    public void setDynamicExtension(boolean dynamicExtension) {
        this.dynamicExtension = dynamicExtension;
    }
}
