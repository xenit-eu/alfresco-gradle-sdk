# Alfresco Gradle SDK

Gradle plugins and tasks to support Alfresco development


## Plugins

For Alfresco development, you need to apply 2 plugins. This will
configure your project with an amp task that includes the jar built
in this project.

```
plugins {
    id 'eu.xenit.alfresco'
    id 'eu.xenit.amp'
}
```

The ```eu.xenit.alfresco``` plugin introduces the ```alfrescoProvided```
configuration that makes it possible to define Alfresco dependencies
that are already provided in the Alfresco war, and should not be
included in the amp file. These dependencies are available when running
unit tests.

## Amp configuration

An amp has some typical files and folders. You can override them in
the ```ampConfig``` extension:

| property         | description                                                     | default                        | required |
|------------------|-----------------------------------------------------------------|--------------------------------|----------|
| moduleProperties | The file that describes your extension.                         | src/main/amp/module.properties | true     |
| configDir        | This folder will be put on the classpath of the war.            | src/main/amp/config            | false    |
| web              | This folder will end up in the "web" directory of the amp file. | src/main/amp/web               | false    |

## Usage

To build an amp, just run the following command:

```
./gradlew amp
```