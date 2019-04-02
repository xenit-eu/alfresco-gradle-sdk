package eu.xenit.gradle.alfrescosdk.config;

import static eu.xenit.gradle.alfrescosdk.internal.DeprecationHelper.warnDeprecationOnce;

import eu.xenit.gradle.alfrescosdk.internal.DeprecationHelper;
import eu.xenit.gradle.alfrescosdk.tasks.AmpSourceSet;
import eu.xenit.gradle.alfrescosdk.tasks.AmpSourceSetConfiguration;
import java.io.File;
import java.util.Collections;
import java.util.function.Supplier;
import org.gradle.api.Project;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;

@Deprecated
public class AmpConfig {
    private static final Logger LOGGER = Logging.getLogger(AmpConfig.class);

    public static final String DEFAULT_CONFIG_DIR = "src/main/amp/config";
    public static final String DEFAULT_MODULE_PROPERTIES = "src/main/amp/module.properties";
    public static final String DEFAULT_WEB_DIR = "src/main/amp/web";
    private Supplier<File> modulePropertiesSupplier;
    private Supplier<File> configDirSupplier;
    private Supplier<File> webDirSupplier;
    private Supplier<File> fileMappingPropertiesSupplier;
    private boolean dynamicExtension = false;

    private final AmpSourceSetConfiguration ampSourceSetConfiguration;
    private final Project project;

    public AmpConfig(Project project, AmpSourceSetConfiguration ampSourceSetConfiguration) {
        this.project = project;
        this.ampSourceSetConfiguration = ampSourceSetConfiguration;
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
        warnDeprecation();
        return modulePropertiesSupplier.get();
    }

    public void setModuleProperties(File moduleProperties) {
        warnDeprecation();
        ampSourceSetConfiguration.module(moduleProperties);
        this.modulePropertiesSupplier = () -> moduleProperties;
    }

    public File getConfigDir() {
        warnDeprecation();
        return configDirSupplier.get();
    }

    public void setConfigDir(File configDir) {
        warnDeprecation();
        ampSourceSetConfiguration.getConfig().setSrcDirs(Collections.singleton(configDir));
        this.configDirSupplier = () -> configDir;
    }

    public File getWebDir() {
        warnDeprecation();
        return webDirSupplier.get();
    }

    public void setWebDir(File webDir) {
        warnDeprecation();
        ampSourceSetConfiguration.getWeb().setSrcDirs(Collections.singleton(webDir));
        this.webDirSupplier = () -> webDir;
    }

    public File getFileMappingProperties() {
        warnDeprecation();
        return fileMappingPropertiesSupplier.get();
    }

    public void setModuleProperties(Supplier<File> moduleProperties) {
        warnDeprecation();
        project.afterEvaluate(p -> {
            ampSourceSetConfiguration.module(modulePropertiesSupplier.get());
        });

        this.modulePropertiesSupplier = moduleProperties;
    }

    public void setConfigDir(Supplier<File> configDir) {
        warnDeprecation();
        project.afterEvaluate(p -> {
            ampSourceSetConfiguration.getConfig().setSrcDirs(Collections.singleton(configDirSupplier.get()));
        });
        this.configDirSupplier = configDir;
    }

    public void setWebDir(Supplier<File> webDir) {
        warnDeprecation();
        project.afterEvaluate(p -> {
            ampSourceSetConfiguration.getWeb().setSrcDirs(Collections.singleton(webDirSupplier.get()));
        });
        this.webDirSupplier = webDir;
    }

    public void setFileMappingProperties(Supplier<File> fileMappingProperties) {
        warnDeprecation();
        project.afterEvaluate(p -> {
            ampSourceSetConfiguration.fileMapping(fileMappingPropertiesSupplier.get());
        });

        this.fileMappingPropertiesSupplier = fileMappingProperties;
    }

    public void setFileMappingProperties(File fileMappingProperties) {
        warnDeprecation();
        ampSourceSetConfiguration.fileMapping(fileMappingProperties);
        this.fileMappingPropertiesSupplier = () -> fileMappingProperties;
    }

    public boolean getDynamicExtension() {
        warnDeprecation();
        return this.dynamicExtension;
    }

    public boolean _getDynamicExtension() {
        return this.dynamicExtension;
    }

    public void setDynamicExtension(boolean dynamicExtension) {
        warnDeprecation();
        this.dynamicExtension = dynamicExtension;
    }

    private static void warnDeprecation() {
        warnDeprecationOnce(LOGGER, "Using the ampConfig configuration block is deprecated. Use the sourceSets configuration instead. Use info logging to get the stacktrace for this deprecation warning.", 1);
    }
}
