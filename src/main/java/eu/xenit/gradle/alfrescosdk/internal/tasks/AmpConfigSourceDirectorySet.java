package eu.xenit.gradle.alfrescosdk.internal.tasks;

import org.gradle.api.file.SourceDirectorySet;
import org.gradle.api.model.ObjectFactory;

import java.text.MessageFormat;

public final class AmpConfigSourceDirectorySet {
    public static final String NAME = "alfrescoAmpConfig";
    
    public static SourceDirectorySet create(String name, ObjectFactory objectFactory) {
        SourceDirectorySet sourceSet = objectFactory.sourceDirectorySet(NAME, MessageFormat.format("{0} Alfresco source {1}", NAME, name));
        sourceSet.include("**/*");
        return sourceSet;
    }
}
