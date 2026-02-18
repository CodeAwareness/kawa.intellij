package com.codeawareness.pycharm.events.handlers;

import com.codeawareness.pycharm.CodeAwarenessProjectService;
import com.codeawareness.pycharm.communication.Message;
import com.codeawareness.pycharm.events.EventHandler;
import com.codeawareness.pycharm.utils.Logger;
import com.google.gson.JsonObject;
import com.intellij.openapi.project.Project;

/**
 * Handles auth:login broadcast events from the Kawa Code backend.
 * Updates authentication state and triggers sync setup.
 */
public class AuthLoginHandler implements EventHandler {

    private final Project project;

    public AuthLoginHandler(Project project) {
        this.project = project;
    }

    @Override
    public String getAction() {
        return "auth:login";
    }

    @Override
    public void handle(Message message) {
        Logger.debug("Handling auth:login event");

        JsonObject data = message.getDataAsObject();
        if (data == null) {
            Logger.warn("auth:login message has no data");
            return;
        }

        CodeAwarenessProjectService projectService = project.getService(CodeAwarenessProjectService.class);
        if (projectService == null) {
            return;
        }

        // Extract user information (same structure as auth:info)
        if (data.has("user") && data.get("user").isJsonObject()) {
            JsonObject user = data.getAsJsonObject("user");
            String userName = user.has("name") ? user.get("name").getAsString() : null;
            String userEmail = user.has("email") ? user.get("email").getAsString() : null;

            projectService.setUserName(userName);
            projectService.setUserEmail(userEmail);
            projectService.setAuthenticated(true);
            projectService.setupSync();

            Logger.info("Logged in as: " + userName + " (" + userEmail + ")");
        }

        // Extract temp directory if present
        if (data.has("tmpDir")) {
            projectService.setTmpDir(data.get("tmpDir").getAsString());
        }
    }
}
