package com.codeawareness.pycharm.events.handlers;

import com.codeawareness.pycharm.CodeAwarenessProjectService;
import com.codeawareness.pycharm.communication.Message;
import com.codeawareness.pycharm.events.EventHandler;
import com.codeawareness.pycharm.utils.Logger;
import com.intellij.openapi.project.Project;

/**
 * Handles sync:setup response events from the Kawa Code backend.
 * Refreshes highlights by re-requesting the active file path.
 */
public class SyncRefreshHandler implements EventHandler {

    private final Project project;

    public SyncRefreshHandler(Project project) {
        this.project = project;
    }

    @Override
    public String getAction() {
        return "sync:setup";
    }

    @Override
    public void handle(Message message) {
        if (message.getFlow() == Message.Flow.REQ) {
            return;
        }

        Logger.debug("Handling sync:setup response, refreshing highlights");

        CodeAwarenessProjectService projectService = project.getService(CodeAwarenessProjectService.class);
        if (projectService != null) {
            projectService.getActiveFileTracker().notifyActiveFileChanged(projectService.getActiveFile());
        }
    }
}
