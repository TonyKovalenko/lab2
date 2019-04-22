package com.group4.server.controller;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

public class ServerController extends Thread {

    private static final Logger log = Logger.getLogger(ServerController.class.getName());
    private final int availableProcessors;
    private final int port;
    private ExecutorService executor;
    private volatile boolean isRunning;

    public boolean isRunning() {
        return isRunning;
    }

    public void setRunning(boolean running) {
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
                    serverSocket.close();
                    break;
                }
                Future<Socket> futureSocket = executor.submit(serverSocket::accept);
                s = futureSocket.get();
                MessageController messageController = new MessageController(s);
                executor.submit(messageController::handle);
            }
        } catch (IOException | ExecutionException | InterruptedException ex) {
            log.error("Server failed to close properly" + ex);
        } finally {
            try {
                s.close();
                executor.awaitTermination(5, TimeUnit.SECONDS);
            } catch (IOException e) {
                log.error("Exception while closing the socket");
            } catch (InterruptedException ex) {
                executor.shutdownNow();
            }
            setRunning(false);
        }
    }
}
