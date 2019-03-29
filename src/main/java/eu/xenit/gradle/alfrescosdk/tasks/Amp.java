package eu.xenit.gradle.alfrescosdk.tasks;

import groovy.lang.Closure;
import java.io.File;
import java.util.concurrent.Callable;
import java.util.function.Supplier;
import org.gradle.api.Action;
import org.gradle.api.file.CopySpec;
import org.gradle.api.file.FileCollection;
import org.gradle.api.internal.file.copy.DefaultCopySpec;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.bundling.Zip;
import org.gradle.util.ConfigureUtil;

public class Amp extends Zip {

    private static final Logger LOGGER = Logging.getLogger(Amp.class);
    public static final String AMP_EXTENSION = "amp";

    private FileCollection libs = getProject().files();

    private FileCollection licenses = getProject().files();

    @Deprecated
    private Supplier<File> web;

    @Deprecated
    private Supplier<File> config;

    private Supplier<File> moduleProperties;

    private Supplier<File> fileMappingProperties;

    private final DefaultCopySpec ampCopySpec;

    public Amp() {
        setExtension(AMP_EXTENSION);
        setDestinationDir(getProject().getBuildDir().toPath().resolve("dist").toFile());
        ampCopySpec = (DefaultCopySpec) getRootSpec().addChildBeforeSpec(getMainSpec()).into("");
        //<editor-fold desc="CopySpec setup">
        ampCopySpec.into("lib", spec -> {
            spec.from(new Callable<FileCollection>() {
                public FileCollection call() {
                    return getLibs();
                }
            });
        });
        ampCopySpec.into("licenses", spec -> {
            spec.from(new Callable<FileCollection>() {
                public FileCollection call() {
                    return getLicenses();
                }
            });
        });
        ampCopySpec.into("", spec -> {
            spec.from(new Callable<File>() {
                @Override
                public File call() {
                    return getModuleProperties();
                }
            });
            spec.rename((original) -> "module.properties");
            spec.expand(getProject().getProperties());
        });
        ampCopySpec.into("web", spec -> {
            spec.from(new Callable<File>() {
                @Override
                public File call() throws Exception {
                    return getWeb();
                }
            });
        });
        ampCopySpec.into("config", spec -> {
            spec.from(new Callable<File>() {
                @Override
                public File call() {
                    return getConfig();
                }
            });
        });
        ampCopySpec.into("", spec -> {
            spec.from(new Callable<File>() {
                @Override
                public File call() {
                    return getFileMappingProperties();
                }
            });
            spec.rename((original) -> "file-mapping.properties");
        });
        //</editor-fold>
    }


    @InputFiles
    @Optional
    public FileCollection getLibs() {
        return libs;
    }

    public void setLibs(FileCollection libs) {
        this.libs = libs;
    }

    @InputFiles
    @Optional
    public FileCollection getLicenses() {
        return licenses;
    }

    public void setLicenses(FileCollection licenses) {
        this.licenses = licenses;
    }

    //<editor-fold desc="ModuleProperties">
    @InputFile
    public File getModuleProperties() {
        if(this.moduleProperties == null){
            return null;
        }
        return moduleProperties.get();
    }

    public void setModuleProperties(File moduleProperties) {
        this.moduleProperties = () -> moduleProperties;
    }

    public void setModuleProperties(Supplier<File> moduleProperties) {
        this.moduleProperties = moduleProperties;
    }
    //</editor-fold>

    //<editor-fold desc="FileMappingProperties">
    @InputFile
    @Optional
    public File getFileMappingProperties() {
        if(this.fileMappingProperties == null){
            return null;
        }
        return this.fileMappingProperties.get();
    }

    public void setFileMappingProperties(File fileMappingProperties) {
        this.fileMappingProperties = () -> fileMappingProperties;
    }

    public void setFileMappingProperties(Supplier<File> fileMappingProperties) {
        this.fileMappingProperties = fileMappingProperties;
    }
    //</editor-fold>

    public void web(Action<? super CopySpec> copySpec) {
        ampCopySpec.into("web", copySpec);
    }

    public void config(Action<? super CopySpec> copySpec) {
        ampCopySpec.into("", copySpec);
    }

    @Internal
    private CopySpec getDe() {
        return ampCopySpec.addChild().into("config/dynamic-extensions/bundles");
    }

    public void de(Action<? super CopySpec> configureAction) {
        configureAction.execute(getDe());
    }

    //<editor-fold desc="Deprecated getters/setters for web and config">
    @Internal
    @Deprecated
    public File getWeb() {
        LOGGER.warn("Amp::getWeb() is deprecated. For automatically configured tasks, use `sourceSets.main.amp.web.srcDirs` to get a list of source directories instead.");
        if(_web.get() == null){
            return null;
        }
        return _web.get().get();
    }

    @Deprecated
    public void setWeb(File web) {
        LOGGER.warn("Amp::setWeb(File) is deprecated. Use `web { from(File) }` instead.");
        this.web = () -> web;
    }

    @Internal
    @Deprecated
    public File getConfig() {
        LOGGER.warn("Amp::getConfig() is deprecated. For automatically configured tasks, use `sourceSets.main.amp.config.srcDirs` to get a list of source directories instead.");
        if(_config.get() == null){
            return null;
        }
        return _config.get().get();
    }

    @Deprecated
    public void setConfig(File directory) {
        LOGGER.warn("Amp::setConfig(File) is deprecated. Use `config { from(File) } instead.");
        this.config = () -> directory;
    }

    @Deprecated
    public void setWeb(Supplier<File> web) {
        LOGGER.warn("Amp::setWeb(Supplier<File>) is deprecated. Use `web { from(File) }` instead.");
        this.web = web;
    }

    @Deprecated
    public void setConfig(Supplier<File> config) {
        LOGGER.warn("Amp::setConfig(Supplier<File>) is deprecated. Use `config { from(File) } instead.");
        this.config = config;
    }
    //</editor-fold>

    //<editor-fold desc="Groovy closure support">
    @Deprecated
    public void setWeb(Closure<File> web) {
        setWeb(web::call);
    }

    @Deprecated
    public void setConfig(Closure<File> config) {
        setConfig(config::call);
    }

    public void setModuleProperties(Closure<File> moduleProperties) {
        setModuleProperties(moduleProperties::call);
    }

    public void setFileMappingProperties(Closure<File> fileMappingProperties) {
        setFileMappingProperties(fileMappingProperties::call);
    }

    public void de(Closure configureClosure) {
        ConfigureUtil.configure(configureClosure, getDe());
    }
    //</editor-fold>

    //<editor-fold desc="Deprecation support helpers">
    private Supplier<Supplier<File>> _web = () -> web;
    private Supplier<Supplier<File>> _config = () -> config;

    public void _setWeb(Supplier<File> web) {
        this._web = () -> web;
    }

    public void _setConfig(Supplier<File> config) {
        this._config = () -> config;
    }
    //</editor-fold>

}
