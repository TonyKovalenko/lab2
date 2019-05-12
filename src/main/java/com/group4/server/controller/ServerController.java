package com.group4.server.controller;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;


/**
 * ServerController class is primary for creating a server socket
 * and accepting connections to it,
 * with further connection processing in a new thread
 * by using {@link MessageController} class instances.
 *
 * @author Nadia Volyk, Anton Kovalenko
 * @since 05-06-19
 * @see MessageController
 */
public class ServerController extends Thread {

    private static final Logger log = Logger.getLogger(ServerController.class.getName());
    private final int availableProcessors;
    private final int port;
    private ExecutorService executor;
    private volatile boolean isRunning;


    /**
     * Method to check whether server is currently running
     * by checking {@link #isRunning} field
     *
     * @return true, if server is running
     *         false, otherwise
     */
    public boolean isRunning() {
        return isRunning;
    }

    /**
     * Method to set server running status
     * by setting {@link #isRunning} field
     *
     * @param running true, to run the server
     *                false, to stop
     */
    public void setRunning(boolean running) {
        isRunning = running;
    }

    /**
     * Constructor for creating ServerController instances
     * with the amount of processors, that are available to JVM
     * and specified port.
     */
    ServerController() {
        availableProcessors = Runtime.getRuntime().availableProcessors();
        port = 8888;
    }


    /**
     * Method responsible for server sockets opening and connections accepting after server was started.
     *
     * Processing starts with creating ServerSocket and the pool of threads,
     * with the count of {@link #availableProcessors}
     *
     * Connections are accepted in an infinite loop, which terminates
     * when the server is shutting down.
     *
     * Every new connection creates a new socket, which is then passed
     * for handling to {@link MessageController} class instance in a new thread.
     *
     * In case of server shutdown, ServerSocket will stop accepting new connections
     * and close itself and the pool of threads after.
     */
    public void run() {
        executor = Executors.newFixedThreadPool(availableProcessors);
        log.info("Processing method started.");
        Socket s = new Socket();
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (isRunning) {
                if (isInterrupted()) {
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
            setRunning(false);
            try {
                s.close();
                executor.awaitTermination(5, TimeUnit.SECONDS);
            } catch (IOException e) {
                log.error("Exception while closing the socket");
            } catch (InterruptedException ex) {
                executor.shutdownNow();
            } finally {
                executor.shutdown();
            }
        }
    }
}
