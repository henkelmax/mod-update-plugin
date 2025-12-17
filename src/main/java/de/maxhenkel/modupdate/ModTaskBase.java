package de.maxhenkel.modupdate;

import org.gradle.api.DefaultTask;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Nested;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public abstract class ModTaskBase extends DefaultTask {

    @Nested
    public abstract Property<ModExtension> getModExtension();

    protected String readRootProjectFile(String fileName) {
        try {
            File rootDir = getProject().getRootProject().getRootDir();
            File file = new File(rootDir, fileName);
            if (!file.exists() || file.isDirectory()) {
                return null;
            }
            return new String(Files.readAllBytes(file.toPath()));
        } catch (IOException e) {
            return null;
        }
    }

}
