package com.codeawareness.pycharm;

import com.codeawareness.pycharm.communication.CatalogConnection;
import com.codeawareness.pycharm.communication.IpcConnection;
import com.codeawareness.pycharm.events.EventDispatcher;
import com.codeawareness.pycharm.events.ResponseHandlerRegistry;
import com.codeawareness.pycharm.utils.Logger;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.components.Service;

/**
 * Application-level service for Kawa Code plugin.
 * Manages global state including the connection to Muninn.
 *
 * Connection flow (single-socket model, matching VSCode):
 * 1. Connect to Muninn socket
 * 2. Send handshake, receive server-assigned CAW ID
 * 3. Reuse same socket for all IPC communication
 */
@Service
public final class CodeAwarenessApplicationService implements Disposable {

    private volatile String clientGuid;
    private CatalogConnection catalogConnection;
    private IpcConnection ipcConnection;
    private final ResponseHandlerRegistry responseHandlerRegistry;
    private final EventDispatcher eventDispatcher;
    private volatile boolean connected = false;

    public CodeAwarenessApplicationService() {
        this.responseHandlerRegistry = new ResponseHandlerRegistry();
        this.eventDispatcher = new EventDispatcher();
        Logger.info("Kawa Code Application Service initialized");
    }

    /**
     * Get the client GUID (CAW ID) assigned by the Muninn server during handshake.
     * Returns null before connect() completes.
     */
    public String getClientGuid() {
        return clientGuid;
    }

    /**
     * Get the catalog connection.
     */
    public CatalogConnection getCatalogConnection() {
        return catalogConnection;
    }

    /**
     * Get the IPC connection.
     */
    public IpcConnection getIpcConnection() {
        return ipcConnection;
    }

    /**
     * Get the response handler registry.
     */
    public ResponseHandlerRegistry getResponseHandlerRegistry() {
        return responseHandlerRegistry;
    }

    /**
     * Get the event dispatcher.
     */
    public EventDispatcher getEventDispatcher() {
        return eventDispatcher;
    }

    /**
     * Check if connected to Kawa Code backend.
     */
    public boolean isConnected() {
        return connected;
    }

    /**
     * Set connection status.
     */
    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    /**
     * Initialize and connect to Kawa Code backend.
     * Single-socket connection flow:
     * 1. Connect to Muninn socket and perform handshake (gets CAW ID)
     * 2. Hand off the socket to IpcConnection for ongoing communication
     */
    public void connect() {
        if (connected) {
            Logger.warn("Already connected to Kawa Code");
            return;
        }

        Logger.info("Connecting to Kawa Code backend...");

        try {
            // Step 1: Connect to Muninn and perform handshake
            catalogConnection = new CatalogConnection();
            catalogConnection.connect();

            // Get the server-assigned CAW ID
            clientGuid = catalogConnection.getAssignedCaw();
            Logger.info("Server assigned CAW ID: " + clientGuid);

            // Step 2: Start IPC on the same socket
            ipcConnection = new IpcConnection(clientGuid, responseHandlerRegistry);

            // Set up message callback to use event dispatcher
            ipcConnection.setMessageCallback(message -> {
                eventDispatcher.dispatch(message);
            });

            // Reuse the CatalogConnection's socket for IPC
            ipcConnection.connect(catalogConnection.getSocketManager());

            connected = true;
            Logger.info("Successfully connected to Kawa Code backend (CAW: " + clientGuid + ")");

        } catch (Exception e) {
            Logger.warn("Could not connect to Kawa Code backend (Muninn not running?): " + e.getMessage());
            // Clean up on failure
            disconnect();
            throw new RuntimeException("Failed to connect to Kawa Code", e);
        }
    }

    /**
     * Disconnect from Kawa Code backend.
     */
    public void disconnect() {
        if (!connected && ipcConnection == null && catalogConnection == null) {
            return;
        }

        Logger.info("Disconnecting from Kawa Code backend...");

        // Stop IPC reader first (doesn't close socket)
        if (ipcConnection != null) {
            ipcConnection.close();
        }

        // Then close the socket via CatalogConnection (which owns it)
        if (catalogConnection != null) {
            catalogConnection.close();
        }

        connected = false;
        Logger.info("Disconnected from Kawa Code");
    }

    @Override
    public void dispose() {
        Logger.info("Disposing Kawa Code Application Service");
        disconnect();
    }
}
