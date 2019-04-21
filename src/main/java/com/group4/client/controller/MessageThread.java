package com.group4.client.controller;

import com.group4.client.view.DialogWindow;
import com.group4.server.model.message.types.*;
import com.group4.server.model.message.wrappers.MessageWrapper;
import javafx.application.Platform;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.io.*;
import java.net.Socket;
import java.util.*;

public class MessageThread extends Thread {
    private final static int DELAY = 30000;
    private final static String lineBreakEscape = "<br />";
    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;
    private Timer pingTimer;
    private int inPings;
    private int outPings;
    private boolean connected;

    private static Class<?>[] clazzes = {MessageWrapper.class, PingMessage.class,
            AuthorizationRequest.class, AuthorizationResponse.class,
            ChatMessage.class, ChatRoomCreationRequest.class, ChatRoomCreationResponse.class, ChatInvitationMessage.class, UsersInChatMessage.class,
            RegistrationRequest.class, RegistrationResponse.class,
            ChangeCredentialsRequest.class, ChangeCredentialsResponse.class,
            ChatUpdateMessageRequest.class
    };
    private JAXBContext context;
    private Map<MessageType, List<MessageWrapper>> sentMessages = new HashMap<>();

    @Override
    public void run() {
        while (connected) {
            try {
                if(reader.ready()) {
                    try (StringReader dataReader = new StringReader(reader.readLine().replaceAll(lineBreakEscape, "\n"))) {
                        MessageWrapper message = (MessageWrapper) context.createUnmarshaller().unmarshal(dataReader);
                        System.out.println("message accepted: ");
                        switch (message.getMessageType()) {
                            case AUTHORIZATION_RESPONSE:
                                LoginController.getInstance().processMessage(message, message);
                                break;
                            case REGISTRATION_RESPONSE:
                                RegistrationController.getInstance().processMessage(message, message);
                                break;
                            case PING:
                                inPings++;
                                break;
                            default:
                                Controller.getInstance().processMessage(message, message);
                                break;
                        }
                    }
                }
            } catch (IOException | JAXBException e) {
                e.printStackTrace();
            }
        }
    }

    public void connect() throws IOException {
        try {
            socket = new Socket("localhost", 8888);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(socket.getOutputStream(), true);
            context = JAXBContext.newInstance(clazzes);
        } catch ( JAXBException e) {
            e.printStackTrace();
        }

        System.out.println("connected");
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
                    System.out.println(outPings + " out");
                } else {
                    System.out.println("Connection was broken!");
                    disconnect();
                    reconnect();
                }
            }
        }, DELAY, DELAY);
    }

    public void disconnect() {
        try {
            pingTimer.cancel();
            connected = false;
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
        System.out.println("disconnected");
    }

    public void reconnect() {
        Platform.runLater(() -> DialogWindow.showErrorWindowWithoutButtons("Reconnect"));
        new Thread(() -> {
            MessageThread newThread = new MessageThread();
            while (!connected) {
                try {
                    newThread.connect();
                    Controller.getInstance().setThread(newThread);
                    newThread.start();
                    connected = true;
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
        }).start();
    }

    public void sendMessage(TransmittableMessage innerMessage) {
        MessageWrapper message = new MessageWrapper(innerMessage);
        MessageType type = message.getMessageType();
        List<MessageWrapper> messageList = sentMessages.get(type);
        if (messageList == null) {
            messageList = new LinkedList<>();
            messageList.add(message);
            sentMessages.put(type, messageList);
        } else {
            messageList.add(message);
        }

        StringWriter stringWriter = new StringWriter();
        try {
            context.createMarshaller().marshal(message, stringWriter);
            System.out.println(stringWriter.toString());
            writer.println(stringWriter.toString().replaceAll("\n", lineBreakEscape));
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }
}
