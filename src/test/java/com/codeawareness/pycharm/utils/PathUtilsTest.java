package com.codeawareness.pycharm.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for PathUtils.
 */
class PathUtilsTest {

    @Test
    void testExpandHome() {
        String home = PathUtils.getHomeDirectory();

        assertEquals(home + "/foo", PathUtils.expandHome("~/foo"));
        assertEquals(home, PathUtils.expandHome("~"));
        assertEquals("/absolute/path", PathUtils.expandHome("/absolute/path"));
        assertEquals("relative/path", PathUtils.expandHome("relative/path"));
        assertNull(PathUtils.expandHome(null));
    }

    @Test
    void testGetMuninnSocketPath() {
        String path = PathUtils.getMuninnSocketPath();
        assertNotNull(path);

        if (PathUtils.isWindows()) {
            assertTrue(path.startsWith("\\\\.\\pipe\\"));
            assertTrue(path.contains("muninn"));
        } else {
            String socketDir = PathUtils.getSocketDirectory();
            assertNotNull(socketDir);
            assertTrue(path.startsWith(socketDir), "Path should start with socket directory");
            assertTrue(path.endsWith("muninn"));
        }
    }

    @Test
    void testGetCatalogSocketPathDelegatesToMuninn() {
        // getCatalogSocketPath is deprecated and delegates to getMuninnSocketPath
        assertEquals(PathUtils.getMuninnSocketPath(), PathUtils.getCatalogSocketPath());
    }

    @Test
    void testGetIpcSocketPathDelegatesToMuninn() {
        // getIpcSocketPath is deprecated — uses single muninn socket regardless of guid
        String path = PathUtils.getIpcSocketPath("any-guid");
        assertEquals(PathUtils.getMuninnSocketPath(), path);
    }

    @Test
    void testGetIpcSocketPathNullGuid() {
        assertThrows(IllegalArgumentException.class, () -> {
            PathUtils.getIpcSocketPath(null);
        });
    }

    @Test
    void testNormalizePath() {
        assertNotNull(PathUtils.normalizePath("/foo/bar"));
        assertNotNull(PathUtils.normalizePath("~/foo/bar"));
        assertNull(PathUtils.normalizePath(null));
    }

    @Test
    void testGetHomeDirectory() {
        String home = PathUtils.getHomeDirectory();
        assertNotNull(home);
        assertFalse(home.isEmpty());
    }
}
