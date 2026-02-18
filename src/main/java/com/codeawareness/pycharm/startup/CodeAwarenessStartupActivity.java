package com.codeawareness.pycharm.startup;

import com.codeawareness.pycharm.CodeAwarenessApplicationService;
import com.codeawareness.pycharm.CodeAwarenessProjectService;
import com.codeawareness.pycharm.utils.Logger;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupActivity;
import org.jetbrains.annotations.NotNull;

/**
 * Delayed startup activity for Kawa Code plugin.
 * Waits for the IDE to fully initialize, then auto-connects to Muninn
 * and initializes the project service.
 */
public class CodeAwarenessStartupActivity implements StartupActivity.DumbAware {

    private static final long INITIALIZATION_DELAY_MS = 2000; // 2 second delay

    @Override
    public void runActivity(@NotNull Project project) {
        Logger.info("Kawa Code startup activity triggered for project: " + project.getName());

        // Delay initialization to allow IDE and other plugins to fully initialize
        ApplicationManager.getApplication().executeOnPooledThread(() -> {
            try {
                Logger.info("Waiting " + INITIALIZATION_DELAY_MS + "ms before initializing Kawa Code plugin...");
                Thread.sleep(INITIALIZATION_DELAY_MS);

                if (project.isDisposed()) {
                    Logger.warn("Project disposed before Kawa Code initialization");
                    return;
                }

                // Auto-connect to Muninn on a background thread
                CodeAwarenessApplicationService appService =
                    ApplicationManager.getApplication().getService(CodeAwarenessApplicationService.class);

                boolean justConnected = false;
                if (appService != null && !appService.isConnected()) {
                    try {
                        appService.connect();
                        justConnected = true;
                        Logger.info("Auto-connected to Kawa Code backend");
                    } catch (Exception e) {
                        Logger.warn("Muninn not available, will retry when status bar widget is toggled: " + e.getMessage());
                    }
                }

                // Initialize project service and request auth on EDT
                final boolean connected = justConnected;
                ApplicationManager.getApplication().invokeLater(() -> {
                    if (project.isDisposed()) {
                        Logger.warn("Project disposed before Kawa Code project service init");
                        return;
                    }

                    Logger.info("Initializing Kawa Code plugin for project: " + project.getName());
                    CodeAwarenessProjectService projectService =
                        project.getService(CodeAwarenessProjectService.class);

                    // If we just connected, re-request auth info (the constructor's
                    // requestAuthInfo() likely failed because the connection wasn't up yet)
                    if (connected && projectService != null) {
                        projectService.requestAuthInfo();
                    }
                });
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                Logger.warn("Kawa Code startup activity interrupted");
            } catch (Exception e) {
                Logger.error("Error in Kawa Code startup activity", e);
            }
        });
    }
}
