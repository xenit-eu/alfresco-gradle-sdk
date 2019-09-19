# Upgrade guide

## 0.1.x -> 0.2.0

1. The `ampConfig` configuration block has been deprecated. It has been replaced with `amp` configuration in `sourceSets`
2. The `Amp` task has receive a makeover to be more in line with the behavior of other gradle tasks.
    1. The `web` and `config` properties have been changed to be `CopySpec`s.
    2. The `de` `CopySpec` has been changed to a `deBundles` property.

<table>
<tr>
<th>Old</th>
<th>New</th>
</tr>
<tr>
<td>

```groovy
ampConfig {
    moduleProperties = project.file("src/main/amp/module.properties")
    fileMappingProperties = project.file("src/main/amp/file-mapping.properties")
    configDir = project.file("src/main/amp/config")
    webDir = project.file("src/main/amp/web")
}
```
</td>
<td>

```groovy
sourceSets {
    main {
        amp {
            module("src/main/amp/module.properties")
            fileMapping("src/main/amp/file-mapping.properties")
            config.srcDir = "src/main/amp/config"
            web.srcDir = "src/main/amp/web"
        }
    }
}

```
</td>
</tr>
<tr>
<td>

```groovy
ampConfig {
    moduleProperties = project.file("src/main/amp/module.properties")
    dynamicExtension = true
}
```
</td>
<td>

```groovy
sourceSets {
    main {
        amp {
            // You can inline the contents of your module.properties as a groovy map
            module ([
                "module.id": "my-module",
                "module.version": project.version
            ])
            dynamicExtension()
        }
    }
}
```
</td>
</tr>
</table>

<table>
<tr>
<th>Old</th>
<th>New</th>
</tr>
<tr>
<td>

```groovy
amp {
    web = project.file("src/main/amp/web")
    config = project.file("src/main/amp/config")
}
```
</td>
<td>

```groovy
amp {
    web {
        from "src/main/amp/web"
    }
    config {
        from "src/main/amp/config"
    }
}
```
</td>
</tr>
<tr>
<td>

```groovy
amp {
    de {
        from components.java
    }
}
```
</td>
<td>

```groovy
amp {
    deBundles += tasks.jar
}
```
</td>
</tr>
<tr>
<td>

```groovy
amp {
    de {
        from tasks.jar
        from configurations.runtimeClasspath
    }
}
```
</td>
<td>

```groovy
amp {
    deBundles += tasks.jar
    deBundles += configurations.runtimeClasspath
}
```
</td>
</tr>
</table>
