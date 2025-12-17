package de.maxhenkel.modupdate.updateserver;

import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Optional;

public abstract class ModUpdateExtension {

    @Input
    public abstract Property<String> getServerURL();

    @Input
    @Optional
    public abstract Property<String> getApiKey();

}
