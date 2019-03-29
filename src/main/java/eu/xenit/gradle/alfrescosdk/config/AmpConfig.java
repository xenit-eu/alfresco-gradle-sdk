package eu.xenit.gradle.alfrescosdk.config;

import java.io.File;
import java.util.function.Supplier;
import org.gradle.api.Project;

public class AmpConfig {

    public static final String DEFAULT_CONFIG_DIR = "src/main/amp/config";
    public static final String DEFAULT_MODULE_PROPERTIES = "src/main/amp/module.properties";
    public static final String DEFAULT_WEB_DIR = "src/main/amp/web";
    private Supplier<File> modulePropertiesSupplier;
    private Supplier<File> configDirSupplier;
    private Supplier<File> webDirSupplier;
    private Supplier<File> fileMappingPropertiesSupplier;
    private boolean dynamicExtension = false;
    private boolean configTouched = false;

    private final Project project;

    public AmpConfig(Project project) {
        this.project = project;
        modulePropertiesSupplier = () -> project.file(DEFAULT_MODULE_PROPERTIES);
        configDirSupplier = () -> {
            File file = project.file(DEFAULT_CONFIG_DIR);
            if (file.exists()) {
                return file;
            } else {
                return null;
            }
        };
        webDirSupplier = () -> {
            File file = project.file(DEFAULT_WEB_DIR);
            if (file.exists()) {
                return file;
            } else {
                return null;
            }
        };
        fileMappingPropertiesSupplier = () -> {
            return null;
        };

    }

    public File getModuleProperties() {
        return modulePropertiesSupplier.get();
    }

    public void setModuleProperties(File moduleProperties) {
        configTouched = true;
        this.modulePropertiesSupplier = () -> moduleProperties;
    }

    public File getConfigDir() {
        return configDirSupplier.get();
    }

    public void setConfigDir(File configDir) {
        configTouched = true;
        this.configDirSupplier = () -> configDir;
    }

    public File getWebDir() {
        return webDirSupplier.get();
    }

    public void setWebDir(File webDir) {
        configTouched = true;
        this.webDirSupplier = () -> webDir;
    }

    public File getFileMappingProperties() {
        return fileMappingPropertiesSupplier.get();
    }

    public void setModuleProperties(Supplier<File> moduleProperties) {
        configTouched = true;
        this.modulePropertiesSupplier = moduleProperties;
    }

    public void setConfigDir(Supplier<File> configDir) {
        configTouched = true;
        this.configDirSupplier = configDir;
    }

    public void setWebDir(Supplier<File> webDir) {
        configTouched = true;
        this.webDirSupplier = webDir;
    }

    public void setFileMappingProperties(Supplier<File> fileMappingProperties) {
        configTouched = true;
        this.fileMappingPropertiesSupplier = fileMappingProperties;
    }

    public Project getProject() {
        return project;
    }

    public void setFileMappingProperties(File fileMappingProperties) {
        configTouched = true;
        this.fileMappingPropertiesSupplier = () -> fileMappingProperties;
    }

    public boolean getDynamicExtension() {
        return this.dynamicExtension;
    }

    public void setDynamicExtension(boolean dynamicExtension) {
        configTouched = true;
        this.dynamicExtension = dynamicExtension;
    }

    public boolean isConfigTouched() {
        return configTouched;
    }
}
