package com.group4.server.controller;

import com.group4.server.model.message.types.TransmittableMessage;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Interface for processing a message from user
 * then forming and sending a response if needed.
 */
public interface MessageControllable {

    void sendResponse(TransmittableMessage message, PrintWriter out, StringWriter stringWriter);
    void broadcastToOnlineUsers(TransmittableMessage message, StringWriter stringWriter);
    void handle();
}
