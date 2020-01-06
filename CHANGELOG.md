# Alfresco Gradle SDK - Changelog

## Unreleased

### Fixed

 * [#33](https://github.com/xenit-eu/alfresco-gradle-sdk/pull/33) Support Gradle 6.0

## Version 1.0.0 - 2019-09-19

### Fixed

 * [#24](https://github.com/xenit-eu/alfresco-gradle-sdk/issues/24) DefaultAmpSourceDirectories uses gradle internal API that is removed in 5.x 
 
**This release drops support for gradle versions < 5.1**

## Version 0.2.1 - 2019-09-03

### Fixed

 * [#11](https://github.com/xenit-eu/alfresco-gradle-sdk/issues/11) Module property where value is of GString type not included in module.properties
 * [#13](https://github.com/xenit-eu/alfresco-gradle-sdk/issues/13) Made error message when using outdated version of gradle more clear

## Version 0.2.0 - 2019-04-23

### New features

 * [#10](https://github.com/xenit-eu/alfresco-gradle-sdk/pull/10): Support for creating multiple amps from different sourceSets

### Fixed

 * [#8](https://github.com/xenit-eu/alfresco-gradle-sdk/issues/8): Configure `Amp` task file properties with `CopySpec`

### Deprecations

 * `ampConfig`
 * Some properties of the `Amp` task

For migration instructions, see [UPGRADING.md](https://github.com/xenit-eu/alfresco-gradle-sdk/blob/0.2.0/UPGRADING.md)

**This release drops support for gradle versions < 4.10**

## Version 0.1.5 - 2018-10-31

 * [#6](https://github.com/xenit-eu/alfresco-gradle-sdk/pull/6) Fix using `ampConfig` with non-default settings. Other settings set in ampConfig were ignored in favor of the defaults.

## Version 0.1.4 - 2018-10-26

 * [#4](https://github.com/xenit-eu/alfresco-gradle-sdk/pull/4) Add support for dynamic extension AMPs
 * [#5](https://github.com/xenit-eu/alfresco-gradle-sdk/pull/5) Document support for dynamic extension AMPs

## Version 0.1.3 - 2018-10-09

 * [#1](https://github.com/xenit-eu/alfresco-gradle-sdk/pull/1) Fix defaults of `ampConfig` configuration to be relative to project directory

## Version 0.1.2 - 2018-10-08

 * Add `alfrescoProvided` dependencies to `testImplementation` instead of `testRuntime`
 * Add `ampConfig` configuration to configure location of module.properties, file-mapping.properties, web and config directories

## Version 0.1.1 - 2018-10-08

 * Publishing `eu.xenit.alfresco` plugin

## Version 0.1.0 - 2018-10-08

Initial release after the Alfresco hackathon
