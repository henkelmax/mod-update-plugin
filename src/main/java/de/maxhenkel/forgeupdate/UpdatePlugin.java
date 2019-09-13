package de.maxhenkel.forgeupdate;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class UpdatePlugin implements Plugin<Project> {

    static final String TASK_NAME = "forgeUpdate";

    public void apply(Project target) {
        target.getTasks().create(TASK_NAME, UpdateTask.class);
    }


}