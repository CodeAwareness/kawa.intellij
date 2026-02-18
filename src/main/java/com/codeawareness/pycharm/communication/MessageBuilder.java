package com.codeawareness.pycharm.communication;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * Builder for constructing Kawa Code messages.
 */
public class MessageBuilder {

    private Message.Flow flow;
    private String domain;
    private String action;
    private JsonElement data;
    private String caw;

    // Package-private constructor to allow testing
    MessageBuilder() {
    }

    /**
     * Create a new request message builder.
     */
    public static MessageBuilder request() {
        MessageBuilder builder = new MessageBuilder();
        builder.flow = Message.Flow.REQ;
        return builder;
    }

    /**
     * Create a new response message builder.
     */
    public static MessageBuilder response() {
        MessageBuilder builder = new MessageBuilder();
        builder.flow = Message.Flow.RES;
        return builder;
    }

    /**
     * Create a new error message builder.
     */
    public static MessageBuilder error() {
        MessageBuilder builder = new MessageBuilder();
        builder.flow = Message.Flow.ERR;
        return builder;
    }

    /**
     * Set the domain.
     */
    public MessageBuilder domain(String domain) {
        this.domain = domain;
        return this;
    }

    /**
     * Set the action.
     */
    public MessageBuilder action(String action) {
        this.action = action;
        return this;
    }

    /**
     * Set the data as a JsonElement.
     */
    public MessageBuilder data(JsonElement data) {
        this.data = data;
        return this;
    }

    /**
     * Set the data as a JsonObject.
     */
    public MessageBuilder data(JsonObject data) {
        this.data = data;
        return this;
    }

    /**
     * Set the client GUID.
     */
    public MessageBuilder caw(String caw) {
        this.caw = caw;
        return this;
    }

    /**
     * Build the message.
     */
    public Message build() {
        if (flow == null) {
            throw new IllegalStateException("Flow must be set");
        }
        return new Message(flow, domain, action, data, caw);
    }

    /**
     * Build a handshake message to register with the Huginn IPC server.
     * Matches the format used by VSCode:
     * {flow: 'req', domain: 'system', action: 'handshake', data: {clientType: 'intellij'}}
     *
     * The server responds with: {domain: 'system', action: 'handshake', data: {caw: 'assigned-id'}}
     */
    public static Message buildHandshake() {
        JsonObject data = new JsonObject();
        data.addProperty("clientType", "intellij");

        return MessageBuilder.request()
                .domain("system")
                .action("handshake")
                .data(data)
                .build();
    }

    /**
     * @deprecated Use {@link #buildHandshake()} instead.
     */
    public static Message buildClientId(String guid) {
        return buildHandshake();
    }

    /**
     * Build a clientDisconnect message.
     */
    public static Message buildClientDisconnect(String guid) {
        return MessageBuilder.request()
                .domain("*")
                .action("clientDisconnect")
                .caw(guid)
                .build();
    }

    /**
     * Build an active-path notification message.
     */
    public static Message buildActivePath(String guid, String filePath, String docName) {
        JsonObject data = new JsonObject();
        data.addProperty("fpath", filePath);
        data.addProperty("doc", docName);
        data.addProperty("caw", guid);

        return MessageBuilder.request()
                .domain("code")
                .action("active-path")
                .data(data)
                .caw(guid)
                .build();
    }

    /**
     * Build a file-saved notification message.
     */
    public static Message buildFileSaved(String guid, String filePath, String docName) {
        JsonObject data = new JsonObject();
        data.addProperty("fpath", filePath);
        data.addProperty("doc", docName);
        data.addProperty("caw", guid);

        return MessageBuilder.request()
                .domain("code")
                .action("file-saved")
                .data(data)
                .caw(guid)
                .build();
    }

    /**
     * Build an auth:info request message.
     * VSCode transmit('auth:info') sends {domain:'auth', action:'info'}.
     */
    public static Message buildAuthInfo(String guid) {
        return MessageBuilder.request()
                .domain("auth")
                .action("info")
                .caw(guid)
                .build();
    }

    /**
     * Build a sync:setup request message.
     */
    public static Message buildSyncSetup(String guid) {
        return MessageBuilder.request()
                .domain("code")
                .action("sync:setup")
                .caw(guid)
                .build();
    }

    /**
     * Build a branch:select request message.
     * VSCode transmit('branch:select') sends {domain:'branch', action:'select'}.
     */
    public static Message buildBranchSelect(String guid, String branch, String fpath) {
        JsonObject data = new JsonObject();
        data.addProperty("branch", branch);
        data.addProperty("fpath", fpath);
        data.addProperty("caw", guid);

        return MessageBuilder.request()
                .domain("branch")
                .action("select")
                .data(data)
                .caw(guid)
                .build();
    }

    /**
     * Build a diff-peer request message.
     */
    public static Message buildDiffPeer(String guid, String origin, String filePath, String peerGuid) {
        JsonObject data = new JsonObject();
        data.addProperty("origin", origin);
        data.addProperty("fpath", filePath);

        // Peer must be an object with _id field (matching Emacs/VSCode format and Gardener expectations)
        JsonObject peer = new JsonObject();
        peer.addProperty("_id", peerGuid);
        data.add("peer", peer);

        data.addProperty("caw", guid);

        return MessageBuilder.request()
                .domain("code")
                .action("diff-peer")
                .data(data)
                .caw(guid)
                .build();
    }
}
