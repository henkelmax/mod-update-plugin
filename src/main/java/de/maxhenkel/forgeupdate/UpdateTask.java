package de.maxhenkel.forgeupdate;

import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.TaskAction;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.TimeZone;

public class UpdateTask extends DefaultTask {

    private String serverURL;
    private String apiKey;

    private String modID;
    private String publishDate;
    private String gameVersion;
    private String modLoader;
    private String modVersion;
    private String[] updateMessages;
    private String releaseType;
    private String[] tags;

    private static final SimpleDateFormat ISO_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");

    static {
        ISO_DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    public UpdateTask() {
        publishDate = "";
        modLoader = "forge";
        updateMessages = new String[0];
        tags = new String[0];
    }

    @TaskAction
    public void updateTask() throws Exception {
        URL uri = new URL(serverURL);
        String server = uri.getProtocol() + "://" + uri.getHost() + (uri.getPort() < 0 ? "" : (":" + uri.getPort())) + uri.getPath();

        if (!server.endsWith("/")) {
            server += "/";
        }

        JSONObject update = new JSONObject();
        if (publishDate.isEmpty()) {
            update.put("publishDate", ISO_DATE_FORMAT.format(Calendar.getInstance().getTime()));
        } else {
            update.put("publishDate", publishDate);
        }
        update.put("gameVersion", gameVersion);
        update.put("modLoader", modLoader);
        update.put("version", modVersion);
        JSONArray msgs = new JSONArray();
        Arrays.stream(updateMessages).forEach(message -> msgs.put(message));
        update.put("updateMessages", msgs);
        update.put("releaseType", releaseType);
        JSONArray t = new JSONArray();
        Arrays.stream(tags).forEach(message -> t.put(message));
        update.put("tags", t);

        HttpResponse<JsonNode> response = Unirest
                .post(server + "updates/{modid}")
                .routeParam("modid", modID)
                .header("Content-Type", "application/json")
                .header("apikey", apiKey)
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

    @Input
    public String getServerURL() {
        return serverURL;
    }

    public void setServerURL(String serverURL) {
        this.serverURL = serverURL;
    }

    @Input
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
    public String[] getUpdateMessages() {
        return updateMessages;
    }

    public void setUpdateMessages(String[] updateMessages) {
        this.updateMessages = updateMessages;
    }

    @Input
    public String getReleaseType() {
        return releaseType;
    }

    public void setReleaseType(String releaseType) {
        this.releaseType = releaseType;
    }

    @Input
    public String[] getTags() {
        return tags;
    }

    public void setTags(String[] tags) {
        this.tags = tags;
    }
}