package com.codeawareness.pycharm.events.handlers;

import com.codeawareness.pycharm.CodeAwarenessApplicationService;
import com.codeawareness.pycharm.CodeAwarenessProjectService;
import com.codeawareness.pycharm.communication.Message;
import com.codeawareness.pycharm.communication.MessageBuilder;
import com.codeawareness.pycharm.diff.DiffViewerManager;
import com.codeawareness.pycharm.events.EventHandler;
import com.codeawareness.pycharm.utils.Logger;
import com.google.gson.JsonObject;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;

/**
 * Handles branch:select events from the Kawa Code backend.
 * Updates the selected branch in project state and opens a diff viewer
 * when Gardener responds with file paths.
 */
public class BranchSelectHandler implements EventHandler {

    private final Project project;
    private final DiffViewerManager diffViewerManager;

    public BranchSelectHandler(Project project, DiffViewerManager diffViewerManager) {
        this.project = project;
        this.diffViewerManager = diffViewerManager;
    }

    @Override
    public String getAction() {
        return "code:branch:select";
    }

    @Override
    public void handle(Message message) {
        Logger.debug("Handling branch:select event, flow: " + message.getFlow());

        JsonObject data = message.getDataAsObject();
        if (data == null) {
            Logger.warn("branch:select message has no data");
            return;
        }

        // If this is a response/broadcast with peerFile + userFile, open diff viewer
        // (Muninn often omits the flow field, so null flow is treated as response)
        if (message.getFlow() != Message.Flow.REQ) {
            if (data.has("peerFile") && data.has("userFile")) {
                String peerFile = data.get("peerFile").getAsString();
                String userFile = data.get("userFile").getAsString();
                String title = data.has("title") ? data.get("title").getAsString() : null;

                Logger.info("Opening branch diff: " + userFile + " vs " + peerFile);
                diffViewerManager.showDiffFiles(userFile, peerFile, title);
                return;
            }
        }

        // Extract branch name and store it
        String branchName = data.has("branch") ? data.get("branch").getAsString() : null;
        if (branchName == null) {
            Logger.warn("branch:select message has no branch name");
            return;
        }

        CodeAwarenessProjectService projectService = project.getService(CodeAwarenessProjectService.class);
        if (projectService == null) {
            return;
        }

        projectService.setSelectedBranch(branchName);
        Logger.info("Selected branch: " + branchName);

        // Send branch:select request to Gardener with file path
        VirtualFile activeFile = projectService.getActiveFile();
        if (activeFile == null) {
            Logger.debug("No active file for branch:select request");
            return;
        }

        try {
            CodeAwarenessApplicationService appService =
                ApplicationManager.getApplication().getService(CodeAwarenessApplicationService.class);

            if (appService == null || !appService.isConnected()) {
                Logger.debug("Cannot send branch:select: not connected");
                return;
            }

            Message branchMsg = MessageBuilder.buildBranchSelect(
                appService.getClientGuid(),
                branchName,
                activeFile.getPath()
            );

            if (appService.getIpcConnection() != null) {
                appService.getIpcConnection().sendMessage(branchMsg);
                Logger.debug("Sent branch:select request for: " + branchName);
            }
        } catch (Exception e) {
            Logger.warn("Failed to send branch:select request", e);
        }
    }
}
