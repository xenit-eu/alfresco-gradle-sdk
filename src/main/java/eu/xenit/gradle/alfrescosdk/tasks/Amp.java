package eu.xenit.gradle.alfrescosdk.tasks;

import static eu.xenit.gradle.alfrescosdk.internal.DeprecationHelper.warnDeprecationOnce;

import groovy.lang.Closure;
import java.io.File;
import java.util.concurrent.Callable;
import java.util.function.Supplier;
import org.gradle.api.Action;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.CopySpec;
import org.gradle.api.file.FileCollection;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.internal.file.copy.DefaultCopySpec;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.bundling.Zip;
import org.gradle.util.ConfigureUtil;

/**
 * Assembles an AMP archive.
 */
public class Amp extends Zip {

    private static final Logger LOGGER = Logging.getLogger(Amp.class);
    public static final String AMP_EXTENSION = "amp";

    private ConfigurableFileCollection libs = getProject().files();

    private ConfigurableFileCollection licenses = getProject().files();

    @Deprecated
    private Supplier<File> web;

    @Deprecated
    private Supplier<File> config;

    private RegularFileProperty moduleProperties = getProject().getObjects().fileProperty();

    private RegularFileProperty fileMappingProperties = getProject().getObjects().fileProperty();

    private ConfigurableFileCollection deBundles = getProject().files();

    private final DefaultCopySpec ampCopySpec = (DefaultCopySpec) getRootSpec().addChildBeforeSpec(getMainSpec()).into("");

    public Amp() {
        getArchiveExtension().set(AMP_EXTENSION);
        getDestinationDirectory().set(getProject().getBuildDir().toPath().resolve("dist").toFile());
        //<editor-fold desc="CopySpec setup">
        ampCopySpec.into("lib", spec -> {
            spec.from((Callable<FileCollection>) () -> getLibs());
        });
        ampCopySpec.into("licenses", spec -> {
            spec.from((Callable<FileCollection>) () -> getLicenses());
        });
        ampCopySpec.into("config/dynamic-extensions/bundles", spec -> {
            spec.from((Callable<FileCollection>) () -> getDeBundles());
        });
        ampCopySpec.into("", spec -> {
            spec.from((Callable<File>) () -> getModuleProperties());
            spec.rename((original) -> "module.properties");
            spec.expand(getProject().getProperties());
        });
        ampCopySpec.into("", spec -> {
            spec.from((Callable<File>) () -> getFileMappingProperties());
            spec.rename((original) -> "file-mapping.properties");
        });
        ampCopySpec.into("web", spec -> {
            spec.from((Callable<File>) () -> web != null?web.get():null);
        });
        ampCopySpec.into("config", spec -> {
            spec.from((Callable<File>) () -> config!=null?config.get():null);
        });
        //</editor-fold>
    }


    /**
     * A configurable collection of files that will be added to the {@code /lib} folder.
     *
     * Any JAR files required by the module and the JAR file of the module itself are located here.
     *
     * @return A modifiable file collection with all libraries.
     */
    @InputFiles
    @Optional
    public ConfigurableFileCollection getLibs() {
        return libs;
    }

    public void setLibs(FileCollection libs) {
        this.libs = getProject().files(libs);
    }

    /**
     * A configurable collection of files that will be added to the {@code /licenses} folder.
     *
     * If the module requires any third party JARs that specify certain licenses, then those licenses can be located here.
     *
     * @return A modifiable file collection with all licenses.
     */
    @InputFiles
    @Optional
    public ConfigurableFileCollection getLicenses() {
        return licenses;
    }

    public void setLicenses(FileCollection licenses) {
        this.licenses = getProject().files(licenses);
    }

    //<editor-fold desc="ModuleProperties">

    /**
     * The file that will be used as the {@code /module.properties} file.
     *
     * The module.properties file is required to be present in the AMP file.
     * It contains metadata about the module, most importantly the id and version of the module that the AMP file contains.
     *
     * For the contents of the file, see <a href="https://docs.alfresco.com/5.1/concepts/dev-extensions-modules-module-properties.html">Module properties file in the Alfresco Developer guide</a>.
     *
     * @return The module.properties file
     */
    @InputFile
    public File getModuleProperties() {
        return moduleProperties.getAsFile().getOrNull();
    }

    public void setModuleProperties(File moduleProperties) {
        this.moduleProperties.set(moduleProperties);
    }

    public void setModuleProperties(Supplier<File> moduleProperties) {
        setModuleProperties(getProject().provider(moduleProperties::get));
    }

    public void setModuleProperties(Provider<File> moduleProperties) {
        this.moduleProperties.set(getProject().getLayout().file(moduleProperties));
    }
    //</editor-fold>

    //<editor-fold desc="FileMappingProperties">

    /**
     * The file that will be used as the {@code /file-mapping.properties} file.
     *
     * It is possible to customize the way the AMP file contents is mapped into the target WAR file by the MMT.
     * This is achieved with the file-mapping.properties file. If this file is not present then the default mapping will be used.
     *
     * For the contents of the file, see <a href="https://docs.alfresco.com/5.1/concepts/dev-extensions-modules-custom-amp.html">Customizing AMP to WAR mapping in the Alfresco Developer guide</a>
     *
     * @return The file-mapping.properties file
     */
    @InputFile
    @Optional
    public File getFileMappingProperties() {
        return this.fileMappingProperties.getAsFile().getOrNull();
    }

    public void setFileMappingProperties(File fileMappingProperties) {
        this.fileMappingProperties.set(fileMappingProperties);
    }

    public void setFileMappingProperties(Supplier<File> fileMappingProperties) {
        setFileMappingProperties(getProject().provider(fileMappingProperties::get));
    }

    public void setFileMappingProperties(Provider<File> fileMappingProperties) {
        this.fileMappingProperties.set(getProject().getLayout().file(fileMappingProperties));
    }
    //</editor-fold>

    /**
     * A configurable collection of files that will be placed in the {@code /config/dynamic-extensions/bundles} directory.
     *
     * JAR files in this directory are automatically loaded by the <a href="https://github.com/xenit-eu/dynamic-extensions-for-alfresco">Dynamic Extensions for Alfresco module</a>.
     *
     * @since 0.2.0
     * @return A modifiable file collection with all dynamic extensions bundles.
     */
    @InputFiles
    @Optional
    public ConfigurableFileCollection getDeBundles() {
        return deBundles;
    }

    public void setDeBundles(FileCollection deBundles) {
        this.deBundles = getProject().files(deBundles);
    }

    /**
     * Configures the {@link CopySpec} to copy files to {@code /web} directory.
     *
     * The {@code /web} directory of an AMP contains custom JSP files and static assets like CSS, images and client-side Javascript.
     *
     * @param copySpec action that configures the {@link CopySpec} for {@code /web}
     *
     * @since 0.2.0
     */
    public void web(Action<? super CopySpec> copySpec) {
        ampCopySpec.into("web", copySpec);
    }

    /**
     * Configures the {@link CopySpec} to copy files to the {@code /config} directory.
     *
     * Typically contains Spring configuration and UI configuration.
     * Files are organized in a directory structure that reflects the Java package structure of the application.
     * XML import files or ACPs can also be conveniently located here.
     * Any content that needs to be on the Tomcat classpath can be located here.
     *
     * @param copySpec action that configures the {@link CopySpec} for {@code /config}
     *
     * @since 0.2.0
     */
    public void config(Action<? super CopySpec> copySpec) {
        ampCopySpec.into("config", copySpec);
    }

    @Deprecated
    private CopySpec getDe() {
        return ampCopySpec.addChild().into("config/dynamic-extensions/bundles");
    }

    /**
     * Configures the {@link CopySpec} to copy files to the {@code /config/dynamic-extensions/bundles} directory.
     *
     * JAR files in this directory are automatically loaded by the <a href="https://github.com/xenit-eu/dynamic-extensions-for-alfresco">Dynamic Extensions for Alfresco module</a>.
     *
     * @param configureAction action that configures the {@link CopySpec} for {@code /config/dynamic-extensions/bundles}
     *
     * @deprecated since 0.2.0. Use {@link #getDeBundles()} or {@link #setDeBundles(FileCollection)} instead.
     */
    @Deprecated
    public void de(Action<? super CopySpec> configureAction) {
        warnDeprecationOnce(LOGGER, "Amp::de() is deprecated. Use `deBundles += File` instead.");
        configureAction.execute(getDe());
    }

    //<editor-fold desc="Deprecated getters/setters for web and config">

    /**
     * @return directory that will be copied to the {@code /web} directory
     * @deprecated since 0.2.0 without a replacement.
     */
    @Internal
    @Deprecated
    public File getWeb() {
        warnDeprecationOnce(LOGGER, "Amp::getWeb() is deprecated. For automatically configured tasks, use `sourceSets.main.amp.web.srcDirs` to get a list of source directories instead.");
        if(_web.get() == null){
            return null;
        }
        return _web.get().get();
    }

    /**
     * @param web directory that will be copied to the {@code /web} directory
     * @deprecated since 0.2.0. Use {@link #web(Action)} instead.
     */
    @Deprecated
    public void setWeb(File web) {
        warnDeprecationOnce(LOGGER, "Amp::setWeb(File) is deprecated. Use `web { from(File) }` instead.");
        this.web = () -> web;
    }

    /**
     * @return directory that will be copied to the {@code /config} directory
     * @deprecated since 0.2.0 without a replacement.
     */
    @Internal
    @Deprecated
    public File getConfig() {
        warnDeprecationOnce(LOGGER, "Amp::getConfig() is deprecated. For automatically configured tasks, use `sourceSets.main.amp.config.srcDirs` to get a list of source directories instead.");
        if(_config.get() == null){
            return null;
        }
        return _config.get().get();
    }

    /**
     * @param directory directory that will be copied to the {@code /config} directory
     * @deprecated since 0.2.0. Use {@link #config(Action)} instead.
     */
    @Deprecated
    public void setConfig(File directory) {
        warnDeprecationOnce(LOGGER, "Amp::setConfig(File) is deprecated. Use `config { from(File) } instead.");
        this.config = () -> directory;
    }

    /**
     * @param web supplier of the directory that will be copied to the {@code /web} directory
     * @deprecated since 0.2.0, use {@link #web(Action)} instead.
     */
    @Deprecated
    public void setWeb(Supplier<File> web) {
        warnDeprecationOnce(LOGGER, "Amp::setWeb(Supplier<File>) is deprecated. Use `web { from(File) }` instead.");
        this.web = web;
    }

    /**
     * @param config supplier of the directory that will be copied to the {@code /config} directory
     * @deprecated since 0.2.0, use {@link #config(Action)} instead.
     */
    @Deprecated
    public void setConfig(Supplier<File> config) {
        warnDeprecationOnce(LOGGER, "Amp::setConfig(Supplier<File>) is deprecated. Use `config { from(File) } instead.");
        this.config = config;
    }
    //</editor-fold>

    //<editor-fold desc="Groovy closure support">
    /**
     * @param web supplier of the directory that will be copied to the {@code /web} directory
     * @deprecated since 0.2.0, use {@link #web(Action)} instead.
     */
    @Deprecated
    public void setWeb(Closure<File> web) {
        setWeb(web::call);
    }

    /**
     * @param config supplier of the directory that will be copied to the {@code /config} directory
     * @deprecated since 0.2.0, use {@link #config(Action)} instead
     */
    @Deprecated
    public void setConfig(Closure<File> config) {
        setConfig(config::call);
    }

    /**
     * @param moduleProperties supplier of the file that will be copied to {@code /module.properties}
     * @see #setModuleProperties(Supplier)
     */
    public void setModuleProperties(Closure<File> moduleProperties) {
        setModuleProperties(moduleProperties::call);
    }

    /**
     * @param fileMappingProperties supplier of the file that will be copied to {@code /file-mapping.properties}
     * @see #setFileMappingProperties(Supplier)
     */
    public void setFileMappingProperties(Closure<File> fileMappingProperties) {
        setFileMappingProperties(fileMappingProperties::call);
    }

    /**
     * @see #de(Action)
     * @param configureClosure action that configures the {@link CopySpec} for {@code /config/dynamic-extensions/bundles}
     * @deprecated since 0.2.0. Use {@link #getDeBundles()} or {@link #setDeBundles(FileCollection)} instead.
     */
    @Deprecated
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
