package de.maxhenkel.modupdate.updateserver;

import org.gradle.api.Task;
import org.gradle.api.file.RegularFile;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public record ModUpdatePayload(
        String publishDate,
        String gameVersion,
        String modLoader,
        String version,
        List<String> updateMessages,
        String releaseType,
        List<String> tags
) {

    private static final SimpleDateFormat ISO_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");

    public static ModUpdatePayload create(
            Task task,
            @Nullable String publishDate,
            String gameVersion,
            String modLoader,
            String modVersion,
            @Nullable List<String> updateMessages,
            @Nullable RegularFile changelogFile,
            String releaseType,
            @Nullable List<String> tags
    ) {
        if (publishDate == null || publishDate.isEmpty()) {
            publishDate = ISO_DATE_FORMAT.format(Calendar.getInstance().getTime());
        }
        updateMessages = gatherChangelog(task, updateMessages, changelogFile);
        if (tags == null) {
            tags = new ArrayList<>();
        }
        return new ModUpdatePayload(publishDate, gameVersion, modLoader, modVersion, updateMessages, releaseType, tags);
    }

    private static List<String> gatherChangelog(Task task, List<String> updateMessages, @Nullable RegularFile changelogFile) {
        List<String> changelog = new ArrayList<>();
        if (updateMessages != null) {
            changelog.addAll(updateMessages);
        }
        if (changelogFile != null) {
            try {
                Files.readAllLines(changelogFile.getAsFile().toPath(), StandardCharsets.UTF_8).stream().map(s -> s.trim().replaceFirst("^\\s*-\\s?", "").trim()).filter(s -> !s.isEmpty()).forEach(changelog::add);
            } catch (IOException e) {
                task.getLogger().lifecycle("Failed to read changelog file", e);
            }
        }
        return changelog;
    }

}
