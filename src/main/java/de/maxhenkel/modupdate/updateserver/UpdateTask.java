package de.maxhenkel.modupdate.updateserver;

import de.maxhenkel.modupdate.ModExtension;
import de.maxhenkel.modupdate.ModTaskBase;
import kong.unirest.core.ContentType;
import kong.unirest.core.HttpResponse;
import kong.unirest.core.Unirest;
import org.gradle.api.tasks.TaskAction;

import javax.annotation.Nullable;
import java.net.URI;
import java.net.URL;
import java.util.*;

public abstract class UpdateTask extends ModTaskBase {

    public static final String TASK_NAME = "modUpdate";

    @TaskAction
    public void updateTask() throws Exception {
        ModExtension mod = getModExtension().get();
        URL url = URI.create(mod.getUpdate().getServerURL().get()).toURL();
        String server = url.getProtocol() + "://" + url.getHost() + (url.getPort() < 0 ? "" : (":" + url.getPort())) + url.getPath();
        if (!server.endsWith("/")) {
            server += "/";
        }

        String apiKey = mod.getUpdate().getApiKey().getOrNull();

        if (apiKey == null) {
            apiKey = getApiKeyFromEnvironment();
        }

        if (apiKey == null) {
            throw new UpdateFailedException("Update failed. No API key found");
        }

        HttpResponse<ModUpdateResponse> response = Unirest
                .post(server + "updates/{modid}")
                .routeParam("modid", mod.getModID().get())
                .contentType(ContentType.APPLICATION_JSON)
                .header("apikey", apiKey)
                .body(ModUpdatePayload.create(
                        this,
                        mod.getPublishDate().getOrNull(),
                        mod.getGameVersion().get(),
                        mod.getModLoader().get(),
                        mod.getModVersion().get(),
                        mod.getUpdateMessages().getOrNull(),
                        mod.getChangelogFile().getOrNull(),
                        mod.getReleaseType().get(),
                        mod.getTags().getOrNull()
                ))
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

}