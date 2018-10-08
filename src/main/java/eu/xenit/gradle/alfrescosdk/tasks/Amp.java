package eu.xenit.gradle.alfrescosdk.tasks;

import groovy.lang.Closure;
import java.io.File;
import java.util.concurrent.Callable;
import java.util.function.Supplier;
import org.gradle.api.file.FileCollection;
import org.gradle.api.internal.file.copy.DefaultCopySpec;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.bundling.Zip;

public class Amp extends Zip {

    public static final String AMP_EXTENSION = "amp";

    private FileCollection libs = getProject().files();

    private FileCollection licenses = getProject().files();

    private Supplier<File> web = () -> null;

    private Supplier<File> config = () -> null;

    private Supplier<File> moduleProperties = () -> null;

    private Supplier<File> fileMappingProperties = () -> null;

    public Amp() {
        setExtension(AMP_EXTENSION);
        setDestinationDir(getProject().getBuildDir().toPath().resolve("dist").toFile());
        DefaultCopySpec ampCopySpec = (DefaultCopySpec) getRootSpec().addChildBeforeSpec(getMainSpec()).into("");
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

    @InputDirectory
    @Optional
    public File getWeb() {
        return web.get();
    }

    public void setWeb(File web) {
        this.web = () -> web;
    }

    @InputDirectory
    @Optional
    public File getConfig() {
        return config.get();
    }

    public void setConfig(File directory) {
        this.config = () -> directory;
    }

    @InputFile
    public File getModuleProperties() {
        return moduleProperties.get();
    }

    public void setModuleProperties(File moduleProperties) {
        this.moduleProperties = () -> moduleProperties;
    }

    @InputFile
    @Optional
    public File getFileMappingProperties() {
        return fileMappingProperties.get();
    }

    public void setFileMappingProperties(File fileMappingProperties) {
        this.fileMappingProperties = () -> fileMappingProperties;
    }

    public void setWeb(Supplier<File> web) {
        this.web = web;
    }

    public void setConfig(Supplier<File> config) {
        this.config = config;
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
