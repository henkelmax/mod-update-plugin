package de.maxhenkel.modupdate;

import de.maxhenkel.modupdate.curseforge.TestTask;
import de.maxhenkel.modupdate.updateserver.UpdateTask;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class UpdatePlugin implements Plugin<Project> {

    public static final String GROUP_NAME = "upload";
    public static final String EXTENSION_NAME = "modUpload";

    public void apply(Project target) {
        ModExtension extension = target.getExtensions().create(EXTENSION_NAME, ModExtension.class);
        target.getTasks().register(UpdateTask.TASK_NAME, UpdateTask.class, task -> {
            task.getModExtension().set(extension);
            task.setGroup(GROUP_NAME);
        });
        target.getTasks().register("testAbc", TestTask.class, task -> {
            task.getModExtension().set(extension);
            task.setGroup(GROUP_NAME);
        });
    }

}