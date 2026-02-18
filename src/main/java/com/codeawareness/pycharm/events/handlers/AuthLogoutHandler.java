package com.codeawareness.pycharm.events.handlers;

import com.codeawareness.pycharm.CodeAwarenessProjectService;
import com.codeawareness.pycharm.communication.Message;
import com.codeawareness.pycharm.events.EventHandler;
import com.codeawareness.pycharm.utils.Logger;
import com.intellij.openapi.project.Project;

/**
 * Handles auth:logout broadcast events from the Kawa Code backend.
 * Clears authentication state, highlights, and diff files.
 */
public class AuthLogoutHandler implements EventHandler {

    private final Project project;

    public AuthLogoutHandler(Project project) {
        this.project = project;
    }

    @Override
    public String getAction() {
        return "auth:logout";
    }

    @Override
    public void handle(Message message) {
        Logger.debug("Handling auth:logout event");

        CodeAwarenessProjectService projectService = project.getService(CodeAwarenessProjectService.class);
        if (projectService == null) {
            return;
        }

        projectService.setAuthenticated(false);
        projectService.setUserName(null);
        projectService.setUserEmail(null);
        projectService.setSelectedPeer(null);
        projectService.setSelectedBranch(null);

        projectService.getHighlightManager().clearAllHighlights();
        projectService.getDiffViewerManager().cleanupAll();

        Logger.info("Logged out, cleared all state");
    }
}
