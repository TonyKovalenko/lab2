package com.group4.server.controller;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ServerController extends Thread {

    private static final Logger log = Logger.getLogger(ServerController.class.getName());
    private final int availableProcessors;
    private final int port;
    private ExecutorService executor;
    private volatile boolean isRunning;

    boolean isRunning() {
        return isRunning;
    }

    void setRunning(boolean running) {
        isRunning = running;
    }

    ServerController() {
        availableProcessors = Runtime.getRuntime().availableProcessors();
        port = 8888;
    }

    public void run() {
        executor = Executors.newFixedThreadPool(availableProcessors);
        log.info("Processing method started.");
        Socket s = new Socket();
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (isRunning) {
                if(isInterrupted()) {
                    System.out.println("HERE");
                    executor.shutdown();
                    serverSocket.close();
                    s.close();
                }
                Future<Socket> futureSocket = executor.submit(serverSocket::accept);
                s = futureSocket.get();
                MessageController messageController = new MessageController(s);
                executor.submit(messageController::handle);
            }
        } catch (IOException | ExecutionException | InterruptedException ex) {
            log.error("Server failed to run properly" + ex);
            try {
                s.close();
            } catch (IOException e) {
                log.error("Exception while closing the socket");
            }
            executor.shutdown();
            setRunning(false);
        }
    }
}
