package com.codeawareness.pycharm.communication;

import com.codeawareness.pycharm.events.ResponseHandlerRegistry;
import com.codeawareness.pycharm.utils.Logger;
import com.intellij.openapi.application.ApplicationManager;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

/**
 * Manages IPC communication with the Muninn server.
 * Reuses the SocketManager established by CatalogConnection (single-socket model).
 * Handles async message reading in a background thread.
 */
public class IpcConnection {

    private SocketManager socketManager;
    private final String clientGuid;
    private final ResponseHandlerRegistry responseHandlerRegistry;
    private final MessageParser messageParser;
    private final AtomicBoolean connected = new AtomicBoolean(false);
    private final AtomicBoolean running = new AtomicBoolean(false);
    private Thread readerThread;
    private Consumer<Message> messageCallback;

    public IpcConnection(String clientGuid, ResponseHandlerRegistry responseHandlerRegistry) {
        this.clientGuid = clientGuid;
        this.responseHandlerRegistry = responseHandlerRegistry;
        this.messageParser = new MessageParser();
    }

    /**
     * Set a callback to handle incoming messages.
     */
    public void setMessageCallback(Consumer<Message> callback) {
        this.messageCallback = callback;
    }

    /**
     * Start IPC communication using an existing socket from CatalogConnection.
     * This reuses the same socket that completed the handshake (single-socket model,
     * matching VSCode behavior).
     */
    public void connect(SocketManager existingSocket) throws IOException {
        if (existingSocket == null || !existingSocket.isConnected()) {
            throw new IOException("Cannot start IPC: provided socket is not connected");
        }

        Logger.info("Starting IPC on existing Muninn socket...");
        this.socketManager = existingSocket;
        connected.set(true);

        // Start background message reader
        startMessageReader();

        Logger.info("IPC active on Muninn socket (GUID: " + clientGuid + ")");
    }

    /**
     * @deprecated Use {@link #connect(SocketManager)} instead.
     */
    public void connect() throws IOException {
        throw new IOException(
            "IpcConnection.connect() without socket is no longer supported. " +
            "Use connect(SocketManager) to reuse the CatalogConnection socket.");
    }

    /**
     * Start background thread to read messages from IPC socket.
     */
    private void startMessageReader() {
        running.set(true);
        readerThread = new Thread(() -> {
            Logger.info("IPC message reader thread started");

            while (running.get() && connected.get()) {
                try {
                    Logger.debug("Waiting for message from IPC socket...");
                    String data = socketManager.readUntilDelimiter(MessageProtocol.DELIMITER);

                    if (data != null && !data.isEmpty()) {
                        Logger.info("Received data from IPC socket (length: " + data.length() + " bytes)");
                        Logger.debug("Raw IPC data: " + data.substring(0, Math.min(200, data.length())));

                        // Parse messages
                        List<Message> messages = messageParser.parse(data + MessageProtocol.DELIMITER);
                        Logger.info("Parsed " + messages.size() + " message(s) from IPC");

                        // Handle each message
                        for (Message message : messages) {
                            handleMessage(message);
                        }
                    } else {
                        Logger.debug("Received empty data from IPC socket");
                    }
                } catch (IOException e) {
                    if (running.get() && connected.get()) {
                        Logger.error("Error reading from IPC socket", e);
                        connected.set(false);
                        break;
                    } else {
                        Logger.debug("IPC reader stopped (running=" + running.get() + ", connected=" + connected.get() + ")");
                    }
                } catch (Exception e) {
                    Logger.error("Unexpected error in message reader", e);
                }
            }

            Logger.info("IPC message reader thread stopped");
        }, "CodeAwareness-IPC-Reader");

        readerThread.setDaemon(true);
        readerThread.start();
    }

    /**
     * Handle an incoming message.
     */
    private void handleMessage(Message message) {
        Logger.info("Handling message: " + message.getDomain() + ":" + message.getAction() +
                   " (flow: " + message.getFlow() + ")");

        // Try response handler first
        String handlerKey = message.getDomain() + ":" + message.getAction();
        if (responseHandlerRegistry.handle(handlerKey, message)) {
            Logger.info("Message handled by response handler: " + handlerKey);
            return;
        }

        // Use callback if set
        if (messageCallback != null) {
            Logger.debug("Dispatching message to callback");
            ApplicationManager.getApplication().executeOnPooledThread(() -> {
                try {
                    messageCallback.accept(message);
                } catch (Exception e) {
                    Logger.error("Error in message callback", e);
                }
            });
        } else {
            Logger.warn("No handler or callback for message: " + handlerKey);
        }
    }

    /**
     * Send a message to the IPC service.
     */
    public void sendMessage(Message message) throws IOException {
        if (!connected.get()) {
            Logger.warn("Cannot send message: not connected to IPC service");
            throw new IOException("Not connected to IPC service");
        }

        String serialized = MessageProtocol.serialize(message);
        Logger.info("Sending message to IPC: " + message.getDomain() + ":" + message.getAction() +
                   " (length: " + serialized.length() + " bytes)");
        Logger.debug("Message details - flow: " + message.getFlow() + ", caw: " + message.getCaw());

        socketManager.write(serialized);
        Logger.info("Successfully sent message: " + message.getDomain() + ":" + message.getAction());
    }

    /**
     * Send a message and register a one-time response handler.
     */
    public void sendMessage(Message message, Consumer<Object> responseHandler) throws IOException {
        if (responseHandler != null) {
            String handlerKey = message.getDomain() + ":" + message.getAction();
            responseHandlerRegistry.register(handlerKey, responseHandler);
        }

        sendMessage(message);
    }

    /**
     * Close the IPC connection.
     * Note: This stops the reader thread but does NOT close the socket,
     * since the socket is owned by CatalogConnection.
     */
    public void close() {
        if (connected.get()) {
            Logger.info("Closing IPC connection");

            // Stop reader thread
            running.set(false);
            if (readerThread != null) {
                readerThread.interrupt();
                try {
                    readerThread.join(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }

            // Don't close socketManager - it's owned by CatalogConnection
            socketManager = null;

            connected.set(false);
            Logger.info("IPC connection closed");
        }
    }

    /**
     * Check if connected to IPC service.
     */
    public boolean isConnected() {
        return connected.get() && socketManager != null && socketManager.isConnected();
    }

    /**
     * Get the client GUID.
     */
    public String getClientGuid() {
        return clientGuid;
    }
}
