package com.group4.client.controller;

import com.group4.server.model.message.types.TransmittableMessage;

import java.io.IOException;

public abstract class MessageThread extends Thread {
    /**
     * Returns {@code true}  if client is connected to server, otherwise {@code false} .
     *
     * @return {@code true}  if the view is opened
     */
    public abstract boolean isConnected();

    /**
     * Wait for incoming messages from server while connected.
     * After unmarshalling message transfers control to corresponding controller
     */
    @Override
    public abstract void run();

    /**
     * Connects to the server. Host and port for socket are loaded from "config.properties"/
     * Starts ping timer to check server efficiency
     * @throws IOException
     */
    public abstract void connect() throws IOException;

    /**
     * Disconnects from server
     */
    public abstract void disconnect();

    /**
     * Starts reconnecting to server
     */
    public abstract void reconnect();

    /**
     * Sends specified message to server
     * @param innerMessage
     */
    public abstract void sendMessage(TransmittableMessage innerMessage);
}
