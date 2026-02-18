package com.codeawareness.pycharm.communication;

import com.codeawareness.pycharm.utils.Logger;
import com.codeawareness.pycharm.utils.PathUtils;
import com.google.gson.JsonObject;

import java.io.IOException;

/**
 * Manages the initial connection and handshake with the Muninn IPC server.
 * On Unix, connects to the single 'muninn' socket. On Windows, connects to
 * the named pipe.
 *
 * After a successful handshake, the server assigns a CAW ID. The underlying
 * SocketManager is then handed off to IpcConnection for ongoing communication.
 */
public class CatalogConnection {

    private SocketManager socketManager;
    private String assignedCaw;
    private boolean connected = false;

    /**
     * Connect to the Muninn IPC server and perform handshake.
     * After this call, {@link #getAssignedCaw()} returns the server-assigned CAW ID
     * and {@link #getSocketManager()} returns the connected socket for reuse.
     */
    public void connect() throws IOException {
        Logger.info("Connecting to Muninn IPC server...");

        String muninnPath = PathUtils.getMuninnSocketPath();
        Logger.info("Muninn socket path: " + muninnPath);
        socketManager = new SocketManager(muninnPath);

        try {
            socketManager.connect();
            connected = true;
            Logger.info("Socket connection established to Muninn");

            // Send handshake and wait for CAW ID
            performHandshake();

            Logger.info("Successfully connected to Muninn (assigned CAW: " + assignedCaw + ")");
        } catch (IOException e) {
            connected = false;
            Logger.error("Failed to connect to Muninn at: " + muninnPath, e);
            throw e;
        }
    }

    /**
     * Send handshake message and read the response to get the assigned CAW ID.
     */
    private void performHandshake() throws IOException {
        Logger.info("Sending handshake to Muninn...");

        Message handshake = MessageBuilder.buildHandshake();
        String serialized = MessageProtocol.serialize(handshake);
        socketManager.write(serialized);

        // Read handshake response
        Logger.info("Waiting for handshake response...");
        String responseData = socketManager.readUntilDelimiter(MessageProtocol.DELIMITER);

        if (responseData == null || responseData.isEmpty()) {
            throw new IOException("Empty handshake response from Muninn");
        }

        Logger.info("Received handshake response (length: " + responseData.length() + " bytes)");
        Logger.debug("Handshake response: " + responseData);

        // Parse the handshake response to extract CAW ID
        try {
            Message response = MessageProtocol.deserialize(responseData);

            if (!"system".equals(response.getDomain()) || !"handshake".equals(response.getAction())) {
                throw new IOException("Unexpected handshake response: " +
                    response.getDomain() + ":" + response.getAction());
            }

            JsonObject data = response.getDataAsObject();
            if (data == null || !data.has("caw")) {
                throw new IOException("Handshake response missing 'caw' field");
            }

            assignedCaw = data.get("caw").getAsString();
            Logger.info("Handshake complete, assigned CAW ID: " + assignedCaw);

        } catch (IllegalArgumentException e) {
            throw new IOException("Failed to parse handshake response: " + e.getMessage(), e);
        }
    }

    /**
     * Send a disconnect message to Muninn.
     */
    public void disconnect() throws IOException {
        if (!connected || assignedCaw == null) {
            return;
        }

        try {
            Logger.debug("Sending disconnect to Muninn");
            Message message = MessageBuilder.buildClientDisconnect(assignedCaw);
            String serialized = MessageProtocol.serialize(message);
            socketManager.write(serialized);
            Logger.info("Sent disconnect to Muninn");
        } catch (IOException e) {
            Logger.error("Error sending disconnect to Muninn", e);
            throw e;
        }
    }

    /**
     * Close the connection.
     */
    public void close() {
        if (connected) {
            try {
                disconnect();
            } catch (IOException e) {
                Logger.warn("Error disconnecting from Muninn: " + e.getMessage());
            }

            if (socketManager != null) {
                socketManager.close();
            }

            connected = false;
            Logger.info("Muninn connection closed");
        }
    }

    /**
     * Check if connected to Muninn.
     */
    public boolean isConnected() {
        return connected && socketManager != null && socketManager.isConnected();
    }

    /**
     * Get the CAW ID assigned by the server during handshake.
     */
    public String getAssignedCaw() {
        return assignedCaw;
    }

    /**
     * Get the underlying SocketManager for reuse by IpcConnection.
     * The socket remains connected after handshake and should be used
     * for all subsequent IPC communication (single-socket model).
     */
    public SocketManager getSocketManager() {
        return socketManager;
    }
}
