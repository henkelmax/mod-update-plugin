package de.maxhenkel.modupdate.updateserver;

import kong.unirest.core.ContentType;
import kong.unirest.core.HttpResponse;
import kong.unirest.core.Unirest;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.TaskAction;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.util.*;

public class UpdateTask extends DefaultTask {

    public static final String TASK_NAME = "modUpdate";

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

    public UpdateTask() {
        modLoader = "forge";
        updateMessages = null;
        tags = new ArrayList<>();
    }

    @TaskAction
    public void updateTask() throws Exception {
        URL url = URI.create(serverURL).toURL();
        String server = url.getProtocol() + "://" + url.getHost() + (url.getPort() < 0 ? "" : (":" + url.getPort())) + url.getPath();
        if (!server.endsWith("/")) {
            server += "/";
        }

        HttpResponse<ModUpdateResponse> response = Unirest
                .post(server + "updates/{modid}")
                .routeParam("modid", modID)
                .contentType(ContentType.APPLICATION_JSON)
                .header("apikey", apiKey == null ? getApiKeyFromEnvironment() : apiKey)
                .body(ModUpdatePayload.create(this, publishDate, gameVersion, modLoader, modVersion, updateMessages, changelogFile, releaseType, tags))
                .asObject(ModUpdateResponse.class);

        if (!response.isSuccess()) {
            if (response.getStatus() == 401) {
                throw new UpdateFailedException("Update failed. You are not authorized: " + response.getStatus() + " (" + response.getStatusText() + ")");
            }
            ModUpdateResponse body = response.getBody();
            for (ModUpdateResponse.ApiErrorDetail err : body.err()) {
                getLogger().error("Server returned: {}", err.message());
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