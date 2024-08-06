package eu.xenit.gradle.alfrescosdk.internal;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.gradle.api.UncheckedIOException;

public final class PropertiesUtil {
    private PropertiesUtil() {
        throw new UnsupportedOperationException("This utility class can not be instantiated");
    }

    public static Properties loadProperties(File propertyFile) {
        try {
            try(FileInputStream inputStream = new FileInputStream(propertyFile)) {
                return loadProperties(inputStream);
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static Properties loadProperties(InputStream inputStream) {
        Properties properties = new Properties();
        try {
            properties.load(inputStream);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return properties;
    }

    public static void saveProperties(Properties properties, File propertyFile) {
        try {
            try (FileOutputStream propertiesFileOutputStream = new FileOutputStream(propertyFile)) {
                properties.store(propertiesFileOutputStream, null);
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

}
