package de.maxhenkel.modupdate;

import de.maxhenkel.modupdate.updateserver.UpdateTask;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class UpdatePlugin implements Plugin<Project> {

    public static final String GROUP_NAME = "upload";

    public void apply(Project target) {
        target.getTasks().register(UpdateTask.TASK_NAME, UpdateTask.class, task -> task.setGroup(GROUP_NAME));
    }

}