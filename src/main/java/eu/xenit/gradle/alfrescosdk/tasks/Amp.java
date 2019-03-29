package eu.xenit.gradle.alfrescosdk.tasks;

import groovy.lang.Closure;
import java.io.File;
import java.util.concurrent.Callable;
import java.util.function.Supplier;
import org.gradle.api.Action;
import org.gradle.api.file.CopySpec;
import org.gradle.api.file.FileCollection;
import org.gradle.api.internal.file.copy.DefaultCopySpec;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.bundling.Zip;
import org.gradle.util.ConfigureUtil;

public class Amp extends Zip {

    public static final String AMP_EXTENSION = "amp";

    private FileCollection libs = getProject().files();

    private FileCollection licenses = getProject().files();

    private Supplier<File> web;

    private Supplier<File> config;

    private Supplier<File> moduleProperties;

    private Supplier<File> fileMappingProperties;

    private DefaultCopySpec ampCopySpec;

    public Amp() {
        setExtension(AMP_EXTENSION);
        setDestinationDir(getProject().getBuildDir().toPath().resolve("dist").toFile());
        ampCopySpec = (DefaultCopySpec) getRootSpec().addChildBeforeSpec(getMainSpec()).into("");
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
    }

    @Internal
    private CopySpec getDe() {
        return ampCopySpec.addChild().into("config/dynamic-extensions/bundles");
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

    public void de(Closure configureClosure) {
        ConfigureUtil.configure(configureClosure, getDe());
    }

    public void de(Action<? super CopySpec> configureAction) {
        configureAction.execute(getDe());
    }

    @InputDirectory
    @Optional
    public File getWeb() {
        if(this.web == null){
            return null;
        }
        return web.get();
    }

    public void setWeb(File web) {
        this.web = () -> web;
    }

    @InputDirectory
    @Optional
    public File getConfig() {
        if(this.config == null){
            return null;
        }
        return config.get();
    }

    public void setConfig(File directory) {
        this.config = () -> directory;
    }

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

    public void setWeb(Supplier<File> web) {
        this.web = web;
    }

    public void web(Action<? super CopySpec> copySpec) {
        ampCopySpec.into("web", copySpec);
    }

    public void setConfig(Supplier<File> config) {
        this.config = config;
    }

    public void config(Action<? super CopySpec> copySpec) {
        ampCopySpec.into("", copySpec);
    }

    public void setModuleProperties(Supplier<File> moduleProperties) {
        this.moduleProperties = moduleProperties;
    }

    public void setFileMappingProperties(Supplier<File> fileMappingProperties) {
        this.fileMappingProperties = fileMappingProperties;
    }

    //Groovy closure support

    public void setWeb(Closure<File> web) {
        this.web = () -> web.call();
    }

    public void setConfig(Closure<File> config) {
        this.config = () -> config.call();
    }

    public void setModuleProperties(Closure<File> moduleProperties) {
        this.moduleProperties = () -> moduleProperties.call();
    }

    public void setFileMappingProperties(Closure<File> fileMappingProperties) {
        this.fileMappingProperties = () -> fileMappingProperties.call();
    }

}
