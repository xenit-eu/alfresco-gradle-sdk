package eu.xenit.gradle.alfrescosdk.tasks;

import groovy.lang.Closure;
import java.io.File;
import java.util.Map;
import java.util.Properties;
import org.gradle.api.Action;
import org.gradle.api.NonNullApi;
import org.gradle.api.file.SourceDirectorySet;
import org.gradle.api.tasks.SourceSet;
import org.gradle.util.ConfigureUtil;

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

    default AmpSourceSetConfiguration config(Closure configureClosure) {
        ConfigureUtil.configure(configureClosure, getConfig());
        return this;
    }

    default AmpSourceSetConfiguration config(Action<? super SourceDirectorySet> configure) {
        configure.execute(getConfig());
        return this;
    }


    SourceDirectorySet getWeb();

    default AmpSourceSetConfiguration web(Closure configureClosure) {
        ConfigureUtil.configure(configureClosure, getWeb());
        return this;
    }


    default AmpSourceSetConfiguration web(Action<? super SourceDirectorySet> configure) {
        configure.execute(getWeb());
        return this;
    }

    AmpSourceSetConfiguration dynamicExtension(boolean dynamicExtension);

    default AmpSourceSetConfiguration dynamicExtension() {
        return dynamicExtension(true);
    }

}
