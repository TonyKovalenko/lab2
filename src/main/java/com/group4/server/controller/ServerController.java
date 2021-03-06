package com.group4.server.controller;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.*;


/**
 * ServerController class is primary for creating a server socket
 * and accepting connections to it,
 * with further connection processing in a new thread
 * by using {@link MessageController} class instances.
 *
 * @author Nadia Volyk, Anton Kovalenko
 * @see MessageController
 * @since 05-06-19
 */
public class ServerController extends Thread implements ServerControllable {

    private static final Logger log = Logger.getLogger(ServerController.class.getName());
    private int availableProcessors;
    private int port = 8888;
    private String ipAddress = "localhost";
    private ExecutorService executor;
    private Set<Socket> connectedSockets = new HashSet<>();
    private Set<MessageController> handledConnections = new HashSet<>();

    /**
     * Constructor for creating ServerController instances
     * with the amount of processors, that are available to JVM
     * and specified port.
     */
    public ServerController() {
        availableProcessors = Runtime.getRuntime().availableProcessors();
        if (validateWithDOM("connection-params.xml")) {
            try {
                List<String> connectionParams = getConnectionData("connection-params.xml");
                ipAddress = connectionParams.get(0);
                port = Integer.parseInt(connectionParams.get(1));
            } catch (ParserConfigurationException | SAXException | NumberFormatException | IOException ex) {
                log.error("Exception during connection data read from file" + ex);
                return;
            }
        }
        log.info("Connection parameters file was validated.");
    }

    /**
     * Method responsible for server sockets opening and connections accepting after server was started.
     * <p>
     * Processing starts with creating ServerSocket and the pool of threads,
     * with the count of {@link #availableProcessors}
     * <p>
     * Connections are accepted in an infinite loop, which terminates
     * when the server is shutting down.
     * <p>
     * Every new connection creates a new socket, which is then passed
     * for handling to {@link MessageController} class instance in a new thread.
     * <p>
     * In case of server shutdown, ServerSocket will stop accepting new connections
     * and close itself and the pool of threads after.
     */
    public void run() {
        executor = Executors.newFixedThreadPool(availableProcessors);
        log.info("Processing method started.");
        Socket userSocket;
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (!isInterrupted()) {
                Future<Socket> futureSocket = executor.submit(serverSocket::accept);
                userSocket = futureSocket.get();
                connectedSockets.add(userSocket);
                MessageController messageController = new MessageController(userSocket);
                handledConnections.add(messageController);
                executor.submit(messageController::handle);
            }
        } catch (IOException | ExecutionException | InterruptedException ex) {
            log.warn("Server failed to close properly. " + ex);
        } finally {
            try {
                handledConnections.forEach(MessageController::stopHandling);
                for (Socket socket : connectedSockets) {
                    socket.close();
                }
                executor.awaitTermination(1, TimeUnit.SECONDS);
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
