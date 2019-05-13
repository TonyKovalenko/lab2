package com.group4.client.controller;

import com.group4.client.view.DialogWindow;
import com.group4.server.model.entities.User;
import com.group4.server.model.message.types.PingMessage;
import com.group4.server.model.message.types.TransmittableMessage;
import com.group4.server.model.message.utils.MarshallingUtils;
import com.group4.server.model.message.wrappers.MessageWrapper;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import org.apache.log4j.Logger;

import javax.xml.bind.JAXBException;
import java.io.*;
import java.net.Socket;
import java.util.Optional;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Represents thread that allows to work with server
 */
public class MessageThread extends Thread {
    private static final Logger log = Logger.getLogger(MessageThread.class);
    private final static int DELAY = 5000;
    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;
    private Timer pingTimer;
    private int inPings;
    private int outPings;
    private boolean connected;

    private ReconnectionThread reconnectionThread;

    /**
     * Returns {@code true}  if client is connected to server, otherwise {@code false} .
     *
     * @return {@code true}  if the view is opened
     */
    public boolean isConnected() {
        return connected;
    }

    /**
     * Wait for incoming messages from server while connected.
     * After unmarshalling message transfers control to corresponding controller
     */
    @Override
    public void run() {
        while (connected) {
            try {
                if (reader.ready()) {
                    String s = reader.readLine();
                    try {
                        MessageWrapper message = MarshallingUtils.unmarshalMessage(s);
                        log.info("Message accepted: " + message.getMessageType());
                        switch (message.getMessageType()) {
                            case AUTHORIZATION_RESPONSE:
                                LoginController.getInstance().processMessage(message);
                                break;
                            case REGISTRATION_RESPONSE:
                                RegistrationController.getInstance().processMessage(message);
                                break;
                            case PING:
                                inPings++;
                                break;
                            case ALL_USERS_RESPONSE:
                                AdminController.getInstance().processMessage(message);
                                break;
                            default:
                                Controller.getInstance().processMessage(message);
                                break;
                        }
                    } catch (JAXBException e) {
                        log.error("Can't unwrap incoming message from server.", e);
                    }
                }
            } catch (IOException e) {
                log.error("IOException happened while trying to read incoming message from server", e);
            }
        }
    }

    /**
     * Connects to the server. Host and port for socket are loaded from "config.properties"/
     * Starts ping timer to check server efficiency
     * @throws IOException
     */
    public void connect() throws IOException {
        String host;
        int port;
        try (InputStream input = new FileInputStream("config.properties")) {
            Properties prop = new Properties();
            prop.load(input);

            host = prop.getProperty("host");
            port = Integer.valueOf(prop.getProperty("port"));
        } catch (FileNotFoundException e) {
            log.warn("Unable to find config.properties");
            return;
        } catch (IOException e) {
            log.error("IOException happened while trying to load config.properties", e);
            return;
        }

        socket = new Socket(host, port);
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        writer = new PrintWriter(socket.getOutputStream(), true);

        log.info("Connected");
        inPings = outPings = 0;
        connected = true;
        pingTimer = new Timer();
        pingTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (inPings == outPings) {
                    PingMessage message = new PingMessage();
                    sendMessage(message);
                    outPings++;
                } else {
                    disconnect();
                    reconnect();
                }
            }
        }, DELAY, DELAY);
        log.info("Ping timer is scheduled");
    }

    /**
     * Disconnects from server
     */
    public void disconnect() {
        connected = false;
        if (reconnectionThread != null) {
            reconnectionThread.finishReconnecting();
            log.info("Finished reconnecting.");
        }
        if (pingTimer != null) {
            pingTimer.cancel();
            log.info("Ping timer is canceled");
        }
        if (socket != null) {
            try {
                socket.close();
                log.info("Socket is closed");
            } catch (IOException e) {
                log.error("IOException happened while trying to close socket", e);
            }
        }
        if (reader != null) {
            try {
                reader.close();
                log.info("Socket reader is closed");
            } catch (IOException e) {
                log.error("IOException happened while trying to close socket reader", e);
            }
        }
        if (writer != null) {
            writer.close();
            log.info("Socket writer is closed");
        }
        log.info("Disconnected from server");
    }

    /**
     * Starts reconnecting to server
     */
    public void reconnect() {
        reconnectionThread = new ReconnectionThread();
        reconnectionThread.reconnect();
    }

    /**
     * Sends specified message to server
     * @param innerMessage
     */
    public void sendMessage(TransmittableMessage innerMessage) {
        MessageWrapper message = new MessageWrapper(innerMessage);
        try {
            writer.println(MarshallingUtils.marshalMessage(message));
        } catch (JAXBException e) {
            log.error("Can't wrap and send outcoming message.", e);
        }
    }

    /**
     * Thread for reconnecting to server
     */
    private class ReconnectionThread extends Thread {
        private boolean isRunning;

        /**
         * Tries to connect to server while the reconnection thread is running and until successful connection.
         * If connection fails, the thread tries to wait 1 second and ties again.
         */
        @Override
        public void run() {
            MessageThread newThread = new MessageThread();
            while (!connected && isRunning) {
                try {
                    newThread.connect();
                    Controller.getInstance().setThread(newThread);
                    newThread.start();
                    connected = true;
                    User currentUser = Controller.getInstance().getCurrentUser();
                    if (currentUser != null) {
                        LoginController.getInstance().sendAuthorizationRequest(currentUser.getNickname(), currentUser.getPassword());
                    }
                    Platform.runLater(() -> DialogWindow.getLastInstance().close());
                } catch (IOException e) {
                    log.info("Still can't connect!");
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e1) {
                        log.error("Some thread has interrupted the ReconnectionThread.", e1);
                    }
                }
            }
            log.info("Finished reconnecting.");
        }

        /**
         * Starts reconnecting
         */
        public void reconnect() {
            Platform.runLater(() -> {
                DialogWindow.showDialogWindow("Error",
                        "Connection failed",
                        "Connection was broken!\nPlease try to wait or exit the application.",
                        Alert.AlertType.ERROR,
                        "Exit");
                Optional<ButtonType> result = DialogWindow.getLastInstance().showAndWait();
                if (result.isPresent() && result.get().getButtonData() == ButtonBar.ButtonData.OK_DONE) {
                    Controller.getInstance().exit();
                }
            });
            isRunning = true;
            this.start();
            log.info("Started reconnection.");
        }

        /**
         * Finishes reconnecting
         */
        public void finishReconnecting() {
            isRunning = false;
        }
    }
}
