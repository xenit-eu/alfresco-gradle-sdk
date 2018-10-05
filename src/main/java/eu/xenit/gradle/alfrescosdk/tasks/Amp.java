package eu.xenit.gradle.alfrescosdk.tasks;

import java.io.File;
import java.util.concurrent.Callable;
import org.gradle.api.file.Directory;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.FileCollection;
import org.gradle.api.file.RegularFile;
import org.gradle.api.file.RegularFileProperty;
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

    final private DirectoryProperty web = newInputDirectory();

    final private DirectoryProperty config = newInputDirectory();

    final private RegularFileProperty moduleProperties = newInputFile();

    final private RegularFileProperty fileMappingProperties = newInputFile();

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
            spec.from(new Callable<RegularFile>() {
                @Override
                public RegularFile call() throws Exception {
                    return getModuleProperties().get();
                }
            });
            spec.rename((original) -> "module.properties");
            spec.expand(getProject().getProperties());
        });
        ampCopySpec.into("web", spec -> {
            spec.from(new Callable<Directory>() {
                @Override
                public Directory call() throws Exception {
                    return getWeb().getOrNull();
                }
            });
        });
        ampCopySpec.into("config", spec -> {
            spec.from(new Callable<Directory>() {
                @Override
                public Directory call() throws Exception {
                    return getConfig().getOrNull();
                }
            });
        });
        ampCopySpec.into("", spec -> {
            spec.from(new Callable<RegularFile>() {
                @Override
                public RegularFile call() throws Exception {
                    return getFileMappingProperties().getOrNull();
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
    public DirectoryProperty getWeb() {
        return web;
    }

    public void setWeb(Directory web) {
        this.web.set(web);
    }

    public void setWeb(File web) {
        this.web.set(web);
    }

    @InputDirectory
    @Optional
    public DirectoryProperty getConfig() {
        return config;
    }

    public void setConfig(Directory directory) {
        config.set(directory);
    }

    public void setConfig(File directory) {
        config.set(directory);
    }

    @InputFile
    public RegularFileProperty getModuleProperties() {
        return moduleProperties;
    }

    public void setModuleProperties(RegularFile moduleProperties) {
        this.moduleProperties.set(moduleProperties);
    }

    public void setModuleProperties(File moduleProperties) {
        this.moduleProperties.set(moduleProperties);
    }

    @InputFile
    @Optional
    public RegularFileProperty getFileMappingProperties() {
        return fileMappingProperties;
    }

    public void setFileMappingProperties(RegularFile fileMappingProperties) {
        this.fileMappingProperties.set(fileMappingProperties);
    }

    public void setFileMappingProperties(File fileMappingProperties) {
        this.fileMappingProperties.set(fileMappingProperties);
    }

}
