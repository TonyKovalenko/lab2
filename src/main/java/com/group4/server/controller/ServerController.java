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

    public void process() throws Exception {
        ServerSocket serverSocket = new ServerSocket(port);
        try {
            while (isRunning) {
                Socket s = serverSocket.accept();
                MessageController messageController = new MessageController(s);
                executor.submit(messageController::handle);
            }
        } finally {
            executor.shutdown();
        }
    }
}
