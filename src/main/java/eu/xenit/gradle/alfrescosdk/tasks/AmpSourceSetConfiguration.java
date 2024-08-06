package eu.xenit.gradle.alfrescosdk.tasks;

import java.io.File;
import java.util.Map;
import java.util.Properties;
import org.gradle.api.Action;
import org.gradle.api.NonNullApi;
import org.gradle.api.file.SourceDirectorySet;

@NonNullApi
public interface AmpSourceSetConfiguration {
    AmpSourceSet getSourceSet();

    AmpSourceSetConfiguration module(String moduleProperties);

    AmpSourceSetConfiguration module(File moduleProperties);

    default AmpSourceSetConfiguration module(Map<String, String> moduleProperties) {
        return module(properties -> properties.putAll(moduleProperties));
    }

    AmpSourceSetConfiguration module(Action<? super Properties> configure);

    AmpSourceSetConfiguration fileMapping(String fileMappingProperties);

    AmpSourceSetConfiguration fileMapping(File fileMappingProperties);

    default AmpSourceSetConfiguration fileMapping(Map<String, String> fileMappingProperties) {
        return fileMapping(properties -> properties.putAll(fileMappingProperties));
    }

    AmpSourceSetConfiguration fileMapping(Action<? super Properties> configure);

    SourceDirectorySet getConfig();

    default AmpSourceSetConfiguration config(Action<? super SourceDirectorySet> configure) {
        configure.execute(getConfig());
        return this;
    }


    SourceDirectorySet getWeb();

    default AmpSourceSetConfiguration web(Action<? super SourceDirectorySet> configure) {
        configure.execute(getWeb());
        return this;
    }

    AmpSourceSetConfiguration dynamicExtension(boolean dynamicExtension);

    default AmpSourceSetConfiguration dynamicExtension() {
        return dynamicExtension(true);
    }

}
