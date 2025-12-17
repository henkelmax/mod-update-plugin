package de.maxhenkel.modupdate;

import de.maxhenkel.modupdate.updateserver.ModUpdateExtension;
import org.gradle.api.Action;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.Nested;
import org.gradle.api.tasks.Optional;

public abstract class ModExtension {

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

    @Nested
    public abstract ModUpdateExtension getUpdate();

    public void update(Action<? super ModUpdateExtension> action) {
        action.execute(getUpdate());
    }

}
