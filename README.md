# Alfresco Gradle SDK 
[![Build Status](https://travis-ci.org/xenit-eu/alfresco-gradle-sdk.svg?branch=master)](https://travis-ci.org/xenit-eu/alfresco-gradle-sdk)
[![Gradle Plugin Portal](https://img.shields.io/maven-metadata/v/https/plugins.gradle.org/m2/eu/xenit/alfresco/eu.xenit.alfresco.gradle.plugin/maven-metadata.xml.svg?colorB=007ec6&label=eu.xenit.alfresco)](https://plugins.gradle.org/plugin/eu.xenit.alfresco)
[![Gradle Plugin Portal](https://img.shields.io/maven-metadata/v/https/plugins.gradle.org/m2/eu/xenit/amp/eu.xenit.amp.gradle.plugin/maven-metadata.xml.svg?colorB=007ec6&label=eu.xenit.amp)](https://plugins.gradle.org/plugin/eu.xenit.amp)

Gradle plugins and tasks to support Alfresco development.
Gradle 5.0 and above are supported.

## Usage

When following the default project layout and conventions, using the SDK is as simple as applying both plugins.
Much additional configuration is possible to tailor the project to your needs.

```groovy
plugins {
    id 'eu.xenit.alfresco' version "0.2.0" // Have a look at https://plugins.gradle.org/plugin/eu.xenit.alfresco for the latest version
    id 'eu.xenit.amp' version "0.2.0"
}
```

### Conventional project layout

The conventional layout for building an Alfresco amp contains a `build.gradle` file applying at least the `eu.xenit.amp` plugin.
The `main` sourceset is automatically configured. Next to the standard `java` and `resources` directories, it also contains an `amp` directory,
which contains resources that will be placed in the AMP file. 

```
simple-alfresco-project
├── build.gradle
└── src
    └── main
        ├── amp
        │   ├── config
        │   │   └── alfresco
        │   │       └── module
        │   │           └── example-default-amp
        │   │               └── module-context.xml
        │   └── module.properties # Optional, will automatically be generated from project metadata when not present
        └── java
```

Running the `amp` task with `./gradlew amp` will build an `amp` file that will be placed in `build/dist/${project.name}-${project.version}.amp`.

Additional libraries needed for your project will be taken from the `runtimeClasspath` configuration and will automatically be placed inside your AMPs `lib/` folder.

Therefore, it is important not add libraries to your `runtimeClasspath` that are already provided by Alfresco, as these will likely result in conflicting dependencies on your classpath.
The `eu.xenit.alfresco` plugin helps by adding an `alfrescoProvided` configuration, where you can add libraries that you know are already provided by Alfresco.
It ensures the libraries are only added to your `compileClasspath`, and that they are present on the compile- and runtime classpath of your unittests in the `test` sourceset.

## Example project

You can copy the example project in [src/integrationTest/resources/examples/simple-alfresco-project](https://github.com/xenit-eu/alfresco-gradle-sdk/tree/master/src/integrationTest/resources/examples/simple-alfresco-project).
You need to change the build.gradle file by adding the versions to the plugins.

## Plugins

### The `eu.xenit.alfresco` plugin

The ```eu.xenit.alfresco``` plugin introduces the ```alfrescoProvided```
configuration that makes it possible to define Alfresco dependencies
that are already provided in the Alfresco war. These provided dependencies are used for compilation,
but are not added as runtime dependencies, as they are already provided by alfresco itself. These dependencies are available
when running unit tests.

A `${sourceSet.name}AlfrescoProvided` configuration is also set up for all the other sourcesets that have enabled the `amp` configuration.
These are not automatically attached to the dependencies of a test sourceset.

### The `eu.xenit.amp` plugin

The amp plugin applies the `eu.xenit.amp-base` plugin and then configures the `main` sourceset with an `amp` configuration block using the default settings.

Then, it configures optional features that are usually desirable:

 * It creates an artifact configuration, `amp` or `${sourceSet.name}Amp`, that will contain the amp built by the task with the same name.
 * When a matching `Jar` task does not exist for a sourceset, it creates that task so it can be consumed by the amp task.
 * Adds all created `Amp` tasks as a dependency of the `assemble` lifecycle task.
 * When the `idea` plugin is applied, configures all amp config directories as resource directories for IntelliJ IDEA.

### The `eu.xenit.amp-base` plugin

The amp base plugin creates and configures an `amp` task for each sourceset that has enabled the `amp` configuration.

#### Configuration

Creating an amp from a sourceset can be enabled and configured by using an `amp` block inside a sourceset.

```groovy
sourceSets {
    main {
        amp {
            // This is the default configuration used by the eu.xenit.amp plugin. The defaults are presented here in simplified form.
            File moduleProperties = project.file("src/main/amp/module.properties")
            if(moduleProperties.exists()) {
                module(moduleProperties)
            } else {
                module([
                    "module.id": project.group+"."+project.name,
                    "module.version": project.version,
                    "module.title": project.name,
                    "module.description": project.description,
                ])
            }
            File fileMappingProperties = project.file("src/main/amp/file-mapping.properties")
            if(fileMappingProperties) {
                fileMapping(fileMappingProperties)
            }
            config {
                srcDir = "src/main/amp/config"
            }
            web {
                srcDir = "src/main/amp/web"
            }
        }
    }
}
```

##### `module`

A `module.properties` describes your AMP file. This file is required by Alfresco to recognize the AMP during installation.

A description of the properties can be found in the [Alfresco developers documentation](https://docs.alfresco.com/5.1/concepts/dev-extensions-modules-module-properties.html). 

The `module.properties` file can be configured with a static file or with a map that can be dynamically configured in the project.

_Defaults:_ If a file exists in `src/${sourceSet.name}/amp/module.properties`, it is used. If the file does not exist, a configuration is generated based on project properties.

_Examples:_
```groovy
sourceSets {
    main {
        amp {
            // 1.a File based configuration
            module("path/to/module.properties") 
            // 1.b File based configuration with a `File`
            module(project.file("path/to/module.properties"))
            // 2. Dynamic configuration with a lazily evaluated closure
            module { 
                put("module.id", project.name)
                put("module.version", project.version)
            }
            // 3. Dynamic configuration with an eagerly evaluated map
            module([ 
                "module.id": project.name,
                "module.version": project.version,
            ])
        }
    }
}
```

##### `fileMapping`

The `file-mapping.properties` file that enables you to change the mapping from files inside the amp to their location in the Alfresco war. 

A description of the file mapping can be found in the [Alfresco developers documentation](https://docs.alfresco.com/5.1/concepts/dev-extensions-modules-custom-amp.html)

Similar to `module`, this file can be configured with a static file or with a map.

_Defaults:_ If a file exists in `src/${sourceSet.name}/amp/file-mapping.properties`, it is used. If the file does not exist, nothing is configured

_Examples:_ The same configuration methods as for `module` are available.

##### `config`

Source directories for `config` will be placed in the `config` directory in the AMP.
When using the default file mappings, these will be placed on the Alfresco classpath when the module is installed.

Source directories that are specified but do not exist are ignored.

_Defaults_: By default the `src/${sourceSet.name}/amp/config` directory is added to this configuration.

_Examples:_
```groovy
sourceSets {
    main {
        amp {
            // 1. Set single source directory
            config.srcDir("src/main/amp/otherConfig")
            // 2. Add a source directory to the existing list
            config.srcDirs += "src/main/amp/additionalConfig"
            // 3. With a configuration block
            config {
                srcDirs += "src/main/amp/additionalConfig"
            }
        }
    }
}
```

##### `web`

Source directories for `web` will be placed in the `web` directory in the AMP.
When using the default file mappings, these will be placed in the root of the webapp. It contains customized jsp files and static assets such as images, CSS and client-side javascript.

Source directories that are specified but do not exist are ignored.

_Defaults_: By default the `src/${sourceSet.name}/amp/web` directory is added to this configuration.

_Examples:_ The same configuration methods as for `config` are available.

##### `dynamicExtension`

Configures the AMP to package a dynamic extension.

Enabling this functionality will place the built jar and its runtime dependencies in the Dynamic Extensions for Alfresco bundles directory,
instead of on their conventional location for an Alfresco AMP.

_Examples:_
```groovy
sourceSets {
    main {
        amp {
            // 1. Package a dynamic extension in an AMP
            dynamicExtension()
            // 2. Explicitly create a normal AMP without packaging a dynamic extension
            dynamicExtension(false)
        }
    }
}
```

#### Tasks

For every sourceset in which amps are enabled, a set of tasks is created.
For the main sourceset these tasks are names as they are presented here, for other sourcesets, the name of the respective sourceset is added to the name.

##### `amp`

The amp task is a custom task of [type `eu.xenit.gradle.alfrescosdk.tasks.Amp`](#_Amp_type).

The task is configured automatically to make its inputs match the configuration from `sourceSets`.

It is supported to override any property of the task. 
It is preferred to configure most of the task in the configuration block in `sourceSets`, as this will guarantee that e.g. IDE integration is also configured properly.
Some properties, like `libs` and `licenses` are not configurable with the `sourceSets` configuration, and must be configured directly on the task when necessary.

For sourcesets other than the main sourceset, the task is named `${sourceSet.name}Amp`.

##### `processModuleProperties` & `processFileMappingProperties`

The `process*Properties` tasks are tasks of [type `WriteProperties`](https://docs.gradle.org/current/javadoc/org/gradle/api/tasks/WriteProperties.html).

These tasks are configured to take the properties set in the `sourceSets` configuration and write them to a properties file which will be added to the amp.

It is supported to override any property of the tasks, but it should not be necessary

For sourcesets other than the main sourceset, the tasks are named `process${sourceSet.name}MooduleProperties` and `process${sourceSet.name}FileMappingProperties`.

#### Task types

#### `Amp` type

The `eu.xenit.gradle.alfrescosdk.tasks.Amp` task type is a subclass of [`Zip`](https://docs.gradle.org/current/javadoc/org/gradle/api/tasks/bundling/Zip.html) that adds convenience methods to creates AMP files.

| Property | Type | Description |
| -------- | ---- | ----------- |
| `moduleProperties` | `File` | The file that will be uses as `/module.properties` inside the AMP. |
| `fileMappingProperties` | `File` | The file that will be uses as `/file-mapping.properties` inside the AMP. |
| `libs`   | `ConfigurableFileCollection` | Collection of files that will be placed inside the `/lib` directory. |
| `licenses` | `ConfigurableFileCollection` | Collection of files that will be placed inside the `/licenses` directory. |
| `deBundles` | `ConfigurableFileCollection` | Collection of files that will be placed inside the `/config/dynamic-extensions/bundles` directory and will be loaded by Dynamic Extensions. |

| CopySpec | Description |
| -------- | ----------- |
| `config` | CopySpec for files that will be placed inside the `/config` directory. |
| `web`    | CopySpec for files that will be placed inside the `/web` directory. |

_Example:_

```groovy
task amp(type: Amp) {
    moduleProperties = project.file("src/main/amp/module.properties")
    fileMappingProperties = project.file("src/main/amp/file-mapping.properties")
    libs = configurations.runtimeClasspath + jar.outputs.files
    config {
        from "src/main/amp/config/module" {
            into "alfresco/module/configured-default-amp"
        }
        from "src/main/amp/config/webscripts" {
            into "alfresco/extension/templates/webscripts"
        }
    }
}
```

#### Publishing
Tasks of the `Amp` type can be passed to the `artifact` function of the [`maven-publish` plugin](https://docs.gradle.org/current/userguide/publishing_maven.html).
This means that an AMP built with the `amp` task can be published in the same way as a `jar` would be published.

_Example:_
```groovy
publishing {
    publications{
        maven(MavenPublication) {
            artifact tasks.amp
        }
    }
}
```

### Contributors
- Bhagya Silva ([@bhagyas](https://linkedin.com/in/bhagyas)) - [Loftux AB](https://loftux.com?ref=github)
