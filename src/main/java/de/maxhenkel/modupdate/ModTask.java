package de.maxhenkel.modupdate;

import org.gradle.api.DefaultTask;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.Optional;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public abstract class ModTask extends DefaultTask {

    @Input
    public abstract Property<String> getServerURL();

    @Input
    @Optional
    public abstract Property<String> getApiKey();

    @Input
    public abstract Property<String> getModID();

    @Input
    @Optional
    public abstract Property<String> getPublishDate();

    @Input
    public abstract Property<String> getGameVersion();

    @Input
    public abstract Property<String> getModLoader();

    @Input
    public abstract Property<String> getModVersion();

    @Input
    @Optional
    public abstract ListProperty<String> getUpdateMessages();

    @InputFile
    @Optional
    public abstract RegularFileProperty getChangelogFile();

    @Input
    public abstract Property<String> getReleaseType();

    @Input
    @Optional
    public abstract ListProperty<String> getTags();

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
