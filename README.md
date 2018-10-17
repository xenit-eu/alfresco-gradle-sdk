# Alfresco Gradle SDK

Gradle plugins and tasks to support Alfresco development


## Plugins

For Alfresco development, you need to apply 2 plugins. This will
configure your project with an amp task that includes the jar built
in this project.

```
plugins {
    id 'eu.xenit.alfresco' version "0.1.3"
    id 'eu.xenit.amp' version "0.1.3"
}
```

The ```eu.xenit.alfresco``` plugin introduces the ```alfrescoProvided```
configuration that makes it possible to define Alfresco dependencies
that are already provided in the Alfresco war, and should not be
included in the amp file. These dependencies are available when running
unit tests.

## Amp configuration

An amp has some typical files and folders. The module.properties file is required.
You can override their locations in the ```ampConfig``` extension:

| property         | description                                                     | default location               | required |
|------------------|-----------------------------------------------------------------|--------------------------------|----------|
| moduleProperties | The file that describes your extension.                         | src/main/amp/module.properties | false    |
| configDir        | This folder will be put on the classpath of the war.            | src/main/amp/config            | false    |
| web              | This folder will end up in the "web" directory of the amp file. | src/main/amp/web               | false    |

## Usage

To build an amp, run the amp task. In case you have the gradlew wrapper in your project, run.

```
./gradlew amp
```

In case you have gradle installed on your system, run.

```
gradle amp
```
The built amp should be visible in build/dist/

## Example project

You can copy the example project in src/integration-test/resources/examples/simple-alfresco-project.
You need to change the build.gradle file by adding the versions to the plugins.