package eu.xenit.gradle.alfrescosdk.tasks;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;
import javax.annotation.Nullable;
import org.gradle.api.Project;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.FileCollection;
import org.gradle.api.file.RegularFile;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.bundling.Zip;

public class Amp extends Zip {

    public static final String AMP_EXTENSION = "amp";

    private FileCollection libs;

    private FileCollection licenses;

    final private DirectoryProperty web = newInputDirectory();

    final private DirectoryProperty config = newInputDirectory();

    final private RegularFileProperty moduleProperties = newInputFile();

    final private RegularFileProperty fileMappingProperties = newInputFile();

    public Amp() {
        setExtension(AMP_EXTENSION);
        into("libs", spec -> {
            if (libs != null) {
                spec.from(libs);
            }
        });
        into("licenses", spec -> {
            if (licenses != null) {
                spec.from(licenses);
            }
        });
        into("config", spec -> {
            if(config.isPresent()) {
                spec.from(config);
            }
        });
        into("web", spec -> {
            if(web.isPresent()) {
                spec.from(web);
            }
        });
        into("", spec -> {
            spec.from(moduleProperties);
            spec.rename((original) -> "module.properties");
            spec.expand(getProject().getProperties());
        });
        into("", spec -> {
            if(fileMappingProperties.isPresent()) {
                spec.from(fileMappingProperties);
                spec.rename((original) -> "file-mapping.properties");
            }
        });
    }

    @Nullable
    @InputFiles
    @Optional
    public FileCollection getLibs() {
        return libs;
    }

    public void setLibs(FileCollection libs) {
        this.libs = libs;
    }

    @Nullable
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

    @InputDirectory
    @Optional
    public DirectoryProperty getConfig() {
        return web;
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
