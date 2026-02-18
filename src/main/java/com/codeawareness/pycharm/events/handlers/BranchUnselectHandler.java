package com.codeawareness.pycharm.events.handlers;

import com.codeawareness.pycharm.CodeAwarenessProjectService;
import com.codeawareness.pycharm.communication.Message;
import com.codeawareness.pycharm.diff.DiffViewerManager;
import com.codeawareness.pycharm.events.EventHandler;
import com.codeawareness.pycharm.utils.Logger;
import com.intellij.openapi.project.Project;

/**
 * Handles branch:unselect events from the Kawa Code backend.
 * Clears the selected branch and closes any open branch diff viewers.
 */
public class BranchUnselectHandler implements EventHandler {

    private final Project project;
    private final DiffViewerManager diffViewerManager;

    public BranchUnselectHandler(Project project, DiffViewerManager diffViewerManager) {
        this.project = project;
        this.diffViewerManager = diffViewerManager;
    }

    @Override
    public String getAction() {
        return "code:branch:unselect";
    }

    @Override
    public void handle(Message message) {
        Logger.debug("Handling branch:unselect event");

        CodeAwarenessProjectService projectService = project.getService(CodeAwarenessProjectService.class);
        if (projectService != null) {
            projectService.setSelectedBranch(null);
            diffViewerManager.cleanupAll();
            Logger.info("Branch unselected, diff files cleaned up");
        }
    }
}
