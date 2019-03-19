package com.group4.server.controller;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerController {
    private final int availableProcessors;
    private final int port;
    private ExecutorService executor;
    private volatile boolean isRunning;

    {
        availableProcessors = Runtime.getRuntime().availableProcessors();
        port = 8888;
    }

    public ServerController() {
        executor = Executors.newFixedThreadPool(availableProcessors);
        isRunning = false;
    }

    public ServerController(ExecutorService executor) {
        this.executor = executor;
        this.isRunning = false;
    }

    public void startServer() {
        isRunning = true;
    }

    public void stopServer() {
        isRunning = false;
    }

    public void process(int port) throws Exception {
        ServerSocket serverSocket = new ServerSocket(port);
        Socket[] s = { null };
        MessageController[] messageController = { null };
        while (isRunning) {
            s[0] = serverSocket.accept();
            messageController[0] = new MessageController(s[0]);
            executor.submit(() -> messageController[0].handle());
        }
    }

}
