package de.maxhenkel.modupdate;

import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.TaskAction;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.*;

public class UpdateTask extends DefaultTask {

    private String serverURL;
    private String apiKey;

    private String modID;
    private String publishDate;
    private String gameVersion;
    private String modLoader;
    private String modVersion;
    private List<String> updateMessages;
    private File changelogFile;
    private String releaseType;
    private List<String> tags;

    private static final SimpleDateFormat ISO_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");

    static {
        ISO_DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    public UpdateTask() {
        modLoader = "forge";
        updateMessages = null;
        tags = new ArrayList<>();
    }

    @TaskAction
    public void updateTask() throws Exception {
        URL uri = new URL(serverURL);
        String server = uri.getProtocol() + "://" + uri.getHost() + (uri.getPort() < 0 ? "" : (":" + uri.getPort())) + uri.getPath();

        if (!server.endsWith("/")) {
            server += "/";
        }

        JSONObject update = new JSONObject();
        if (publishDate == null || publishDate.isEmpty()) {
            update.put("publishDate", ISO_DATE_FORMAT.format(Calendar.getInstance().getTime()));
        } else {
            update.put("publishDate", publishDate);
        }
        update.put("gameVersion", gameVersion);
        update.put("modLoader", modLoader);
        update.put("version", modVersion);
        JSONArray msgs = new JSONArray();
        gatherChangelog().forEach(msgs::put);
        update.put("updateMessages", msgs);
        update.put("releaseType", releaseType);
        JSONArray t = new JSONArray();
        tags.forEach(t::put);
        update.put("tags", t);

        HttpResponse<JsonNode> response = Unirest
                .post(server + "updates/{modid}")
                .routeParam("modid", modID)
                .header("Content-Type", "application/json")
                .header("apikey", apiKey == null ? getApiKeyFromEnvironment() : apiKey)
                .body(update)
                .asJson();
        if (!response.isSuccess()) {
            if (response.getStatus() == 401) {
                throw new UpdateFailedException("Update failed. You are not authorized: " + response.getStatus() + " (" + response.getStatusText() + ")");
            }
            JSONObject error = response.getBody().getObject();
            if (error.has("err")) {
                JSONArray err = error.getJSONArray("err");
                for (int i = 0; i < err.length(); i++) {
                    JSONObject e = err.getJSONObject(i);
                    if (e.has("message")) {
                        getLogger().error("Server returned: {}", e.getString("message"));
                    }
                }
            }
            throw new UpdateFailedException("Update failed. Response Code " + response.getStatus() + " (" + response.getStatusText() + ")");
        }
    }

    @Nullable
    private String getApiKeyFromEnvironment() {
        String apiKey = System.getenv("MOD_UPDATE_API_KEY");
        if (apiKey == null) {
            apiKey = System.getenv("FORGE_UPDATE_API_KEY");
        }
        if (apiKey == null) {
            apiKey = readRootProjectFile("mod_update_api_key.txt");
        }
        if (apiKey == null) {
            apiKey = readRootProjectFile("forge_update_api_key.txt");
        }
        return apiKey;
    }

    private String readRootProjectFile(String fileName) {
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

    private List<String> gatherChangelog() {
        List<String> changelog = new ArrayList<>();
        if (updateMessages != null) {
            changelog.addAll(updateMessages);
        }
        if (changelogFile != null) {
            try {
                Files.readAllLines(changelogFile.toPath(), StandardCharsets.UTF_8).stream().map(s -> s.trim().replaceFirst("^\\s*-\\s?", "").trim()).filter(s -> !s.isEmpty()).forEach(changelog::add);
            } catch (IOException e) {
                getLogger().lifecycle("Failed to read changelog file", e);
            }
        }
        return changelog;
    }

    @Input
    public String getServerURL() {
        return serverURL;
    }

    public void setServerURL(String serverURL) {
        this.serverURL = serverURL;
    }

    @Input
    @Optional
    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    @Input
    public String getModID() {
        return modID;
    }

    public void setModID(String modID) {
        this.modID = modID;
    }

    @Input
    @Optional
    public String getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(String publishDate) {
        this.publishDate = publishDate;
    }

    @Input
    public String getGameVersion() {
        return gameVersion;
    }

    public void setGameVersion(String gameVersion) {
        this.gameVersion = gameVersion;
    }

    @Input
    @Optional
    public String getModLoader() {
        return modLoader;
    }

    public void setModLoader(String modLoader) {
        this.modLoader = modLoader;
    }

    @Input
    public String getModVersion() {
        return modVersion;
    }

    public void setModVersion(String modVersion) {
        this.modVersion = modVersion;
    }

    @Input
    @Optional
    public List<String> getUpdateMessages() {
        return updateMessages;
    }

    public void setUpdateMessages(List<String> updateMessages) {
        this.updateMessages = updateMessages;
    }

    @InputFile
    @Optional
    public File getChangelogFile() {
        return changelogFile;
    }

    public void setChangelogFile(File changelogFile) {
        this.changelogFile = changelogFile;
    }

    @Input
    public String getReleaseType() {
        return releaseType;
    }

    public void setReleaseType(String releaseType) {
        this.releaseType = releaseType;
    }

    @Input
    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }
}