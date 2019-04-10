package com.group4.client.controller;

import com.group4.client.view.DialogWindow;
import com.group4.server.model.MessageTypes.*;
import com.group4.server.model.MessageWrappers.MessageWrapper;
import javafx.application.Platform;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.io.*;
import java.net.Socket;
import java.util.*;

public class MessageThread extends Thread {
    private final static int DELAY = 3000;
    private static String lineBreakEscape = "<br />";
    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;
    private Timer pingTimer;
    private int inPings = 0;
    private int outPings = 0;

    private static Class<?>[] clazzes = {MessageWrapper.class, PingMessage.class,
            AuthorizationRequest.class, AnswerMessage.class, ChatMessage.class,
            NewGroupChatMessage.class, RegistrationRequest.class, UsersInChatMessage.class
    };
    private JAXBContext context;
    private Map<MessageType, List<MessageWrapper>> sentMessages = new HashMap<>();

    @Override
    public void run() {
        while (true) {
            try {
                if(reader.ready()) {
                    try (StringReader dataReader = new StringReader(reader.readLine().replaceAll(lineBreakEscape, "\n"))) {
                        MessageWrapper message = (MessageWrapper) context.createUnmarshaller().unmarshal(dataReader);
                        System.out.println("message accepted: " + message.getMessageId());
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
                        }
                    }

                    /*if (message.getMessageType() == MessageType.ANSWER) {
                        AnswerMessage innerMessage = (AnswerMessage) message.getEncapsulatedMessage();
                        long requestId = innerMessage.getRequestId();
                        MessageWrapper requestMessage = extractRequestMessage(requestId);
                        switch (requestMessage.getMessageType()) {
                            case AUTHORIZATION_REQUEST:
                                LoginController.getInstance().processMessage(requestMessage, message);
                                break;
                            case REGISTRATION_RESPONSE:
                                RegistrationController.getInstance().processMessage(requestMessage, message);
                                break;
                            default:
                                Controller.getInstance().processMessage(requestMessage, message);
                        }
                    } else {
                        Controller.getInstance().processMessage(null, message);
                    }*/
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

        inPings = outPings = 0;
        this.pingTimer = new Timer();
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
                    pingTimer.cancel();
                    disconnect();
                    reconnect();
                    synchronized (Controller.getInstance().getThread()) {
                        try {
                            System.out.println(this);
                            System.out.println(Controller.getInstance().getThread());
                            Controller.getInstance().getThread().wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }, DELAY, DELAY);
    }

    public void disconnect() {
        try {
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void reconnect() {
        Platform.runLater(() -> DialogWindow.showErrorWindowWithoutButtons("Reconnect"));
        new Thread(() -> {
            boolean connected = false;
            while (!connected) {
                try {
                    Controller.getInstance().getThread().connect();
                    synchronized (Controller.getInstance().getThread()) {
                        Controller.getInstance().getThread().notify();
                    }
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

    public MessageWrapper extractRequestMessage(long requestId) {
        MessageWrapper requestMessage = null;
        for (Map.Entry<MessageType, List<MessageWrapper>> entry : sentMessages.entrySet()) {
            for (MessageWrapper m : entry.getValue()) {
                if (m.getMessageId() == requestId) {
                    requestMessage = m;
                }
            }
            if (requestMessage != null) {
                entry.getValue().remove(requestMessage);
            }
        }
        return requestMessage;
    }
}
