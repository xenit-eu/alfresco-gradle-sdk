package eu.xenit.gradle.alfrescosdk.tasks;

import java.io.File;
import java.util.Properties;
import org.gradle.api.Action;

public interface AmpSourceSetConfiguration {
    AmpSourceSetConfiguration module(String moduleProperties);

    AmpSourceSetConfiguration module(File moduleProperties);

    AmpSourceSetConfiguration module(Action<? super Properties> configure);

    AmpSourceSetConfiguration fileMapping(String fileMappingProperties);

    AmpSourceSetConfiguration fileMapping(File fileMappingProperties);

    AmpSourceSetConfiguration fileMapping(Action<? super Properties> configure);

    AmpSourceDirectories getConfig();

    default AmpSourceSetConfiguration config(Action <? super AmpSourceDirectories> configure) {
        configure.execute(getConfig());
        return this;
    }

    AmpSourceDirectories getWeb();

    default AmpSourceSetConfiguration web(Action<? super AmpSourceDirectories> configure) {
        configure.execute(getWeb());
        return this;
    }

}
