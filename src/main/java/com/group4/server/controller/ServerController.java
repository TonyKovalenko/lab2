package com.group4.server.controller;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerController {
    private static final Logger log = Logger.getLogger(ServerController.class.getName());
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

    public void startServer() throws Exception {
        log.info("Server was started.");
        isRunning = true;
        process();
    }

    public void stopServer() {
        isRunning = false;
        log.info("Server was stopped.");
    }

    public static void main(String[] args) throws Exception {
        ServerController controller = new ServerController();
        controller.startServer();
    }

    public void process() throws Exception {
        log.info("Processing method started.");
        ServerSocket serverSocket = new ServerSocket(port);
        try {
            while (isRunning) {
                Socket s = serverSocket.accept();
                MessageController messageController = new MessageController(s);
                executor.submit(messageController::handle);
            }
        } finally {
            executor.shutdown();
            log.info("Executor service was shut down");
        }
    }
}
