package com.codeawareness.pycharm.utils;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Utilities for path handling and normalization across platforms.
 */
public class PathUtils {

    private static final boolean IS_WINDOWS = System.getProperty("os.name").toLowerCase().contains("win");
    private static final boolean IS_MAC = System.getProperty("os.name").toLowerCase().contains("mac");
    private static final String HOME_DIR = System.getProperty("user.home");
    private static final String MUNINN_BUNDLE_ID = "com.codeawareness.muninn";

    /**
     * Check if running on Windows.
     */
    public static boolean isWindows() {
        return IS_WINDOWS;
    }

    /**
     * Expand tilde (~) in path to home directory.
     * Example: "~/foo" -> "/home/user/foo"
     */
    public static String expandHome(String path) {
        if (path == null) {
            return null;
        }
        if (path.startsWith("~/") || path.equals("~")) {
            return path.replaceFirst("^~", HOME_DIR);
        }
        return path;
    }

    private static final String MUNINN_SOCKET_NAME = "muninn";

    /**
     * Get the Kawa Code socket directory path.
     *
     * On macOS, checks candidate directories in order and returns the first
     * one where the muninn socket file actually exists. This handles both
     * App Store (sandboxed) and development (non-sandboxed) builds of Muninn.
     *
     * Candidate order on macOS:
     * 1. App Sandbox container: ~/Library/Containers/{bundleId}/Data/Library/Application Support/Kawa Code/sockets/
     * 2. Non-sandboxed Application Support: ~/Library/Application Support/Kawa Code/sockets/
     *
     * Linux: ~/.kawa-code/sockets/
     * Windows: Uses named pipes (no directory needed)
     */
    public static String getSocketDirectory() {
        if (IS_WINDOWS) {
            return null; // Windows uses named pipes, not files
        }
        if (IS_MAC) {
            String[] candidates = {
                // App Sandbox container (App Store builds)
                Paths.get(HOME_DIR,
                    "Library", "Containers", MUNINN_BUNDLE_ID, "Data",
                    "Library", "Application Support", "Kawa Code", "sockets"
                ).toString(),
                // Non-sandboxed (development builds)
                Paths.get(HOME_DIR,
                    "Library", "Application Support", "Kawa Code", "sockets"
                ).toString()
            };

            for (String dir : candidates) {
                File muninnSocket = new File(dir, MUNINN_SOCKET_NAME);
                if (muninnSocket.exists()) {
                    return dir;
                }
            }

            // No running Muninn found; return the non-sandboxed path so the
            // caller gets a meaningful error message
            return candidates[1];
        }
        return expandHome("~/.kawa-code/sockets");
    }

    /**
     * Get the Muninn socket path.
     * This is the single socket used for all communication with Muninn.
     * Windows: \\.\pipe\muninn
     */
    public static String getMuninnSocketPath() {
        if (IS_WINDOWS) {
            return "\\\\.\\pipe\\" + MUNINN_SOCKET_NAME;
        }
        return Paths.get(getSocketDirectory(), MUNINN_SOCKET_NAME).toString();
    }

    /**
     * @deprecated Use {@link #getMuninnSocketPath()} instead. Kept for test compatibility.
     */
    public static String getCatalogSocketPath() {
        return getMuninnSocketPath();
    }

    /**
     * @deprecated Muninn uses a single socket, not per-client sockets. Kept for test compatibility.
     */
    public static String getIpcSocketPath(String clientGuid) {
        if (clientGuid == null) {
            throw new IllegalArgumentException("Client GUID cannot be null");
        }
        return getMuninnSocketPath();
    }

    /**
     * Normalize a file path for the current platform.
     */
    public static String normalizePath(String path) {
        if (path == null) {
            return null;
        }
        path = expandHome(path);
        return Paths.get(path).normalize().toString();
    }

    /**
     * Check if a file exists.
     */
    public static boolean exists(String path) {
        if (path == null) {
            return false;
        }
        return new File(path).exists();
    }

    /**
     * Create directories if they don't exist.
     */
    public static boolean ensureDirectoryExists(String path) {
        if (path == null) {
            return false;
        }
        File dir = new File(path);
        if (dir.exists()) {
            return dir.isDirectory();
        }
        return dir.mkdirs();
    }

    /**
     * Get the home directory.
     */
    public static String getHomeDirectory() {
        return HOME_DIR;
    }

    /**
     * Convert a file path to a URI-compatible format.
     */
    public static String toUri(String path) {
        if (path == null) {
            return null;
        }
        path = normalizePath(path);
        return Paths.get(path).toUri().toString();
    }
}
