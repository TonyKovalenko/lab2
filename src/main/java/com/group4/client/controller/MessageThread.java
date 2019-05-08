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

import javax.xml.bind.JAXBException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;

public class MessageThread extends Thread {
    private final static int DELAY = 5000;
    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;
    private Timer pingTimer;
    private int inPings;
    private int outPings;
    private boolean connected;

    private ReconnectionThread reconnectionThread;

    public boolean isConnected() {
        return connected;
    }

    @Override
    public void run() {
        while (connected) {
            try {
                if (reader.ready()) {
                    String s = reader.readLine();
                    System.out.println("INPUT: " + s);
                    try {
                        MessageWrapper message = MarshallingUtils.unmarshallMessage(s);
                        System.out.println("message accepted: " + message.getMessageType());
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
                        e.printStackTrace();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void connect() throws IOException {
        socket = new Socket("localhost", 8888);
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        writer = new PrintWriter(socket.getOutputStream(), true);

        System.out.println("Connected");
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
    }

    public void disconnect() {
        try {
            connected = false;
            if (reconnectionThread != null) {
                reconnectionThread.finishReconnecting();
            }
            if (pingTimer != null) {
                pingTimer.cancel();
            }
            if (socket != null) {
                socket.close();
            }
            if (reader != null) {
                reader.close();
            }
            if (writer != null) {
                writer.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Disconnected");
    }

    public void reconnect() {
        reconnectionThread = new ReconnectionThread();
        reconnectionThread.reconnect();
    }

    public void sendMessage(TransmittableMessage innerMessage) {
        MessageWrapper message = new MessageWrapper(innerMessage);
        try {
            writer.println(MarshallingUtils.marshallMessage(message));
            System.out.println("out: " + MarshallingUtils.marshallMessage(message));
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }

    private class ReconnectionThread extends Thread {
        private boolean isRunning;

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
                    System.out.println("Still can't connect!");
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        }

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
        }

        public void finishReconnecting() {
            isRunning = false;
        }
    }
}
