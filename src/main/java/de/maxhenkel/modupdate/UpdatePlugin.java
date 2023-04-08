package de.maxhenkel.modupdate;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class UpdatePlugin implements Plugin<Project> {

    static final String GROUP_NAME = "upload";
    static final String TASK_NAME = "modUpdate";

    public void apply(Project target) {
        target.getTasks().create(TASK_NAME, UpdateTask.class).setGroup(GROUP_NAME);
    }


}