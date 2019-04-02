package com.group4.client.controller;

import com.group4.server.model.MessageTypes.AnswerMessage;
import com.group4.server.model.MessageTypes.MessageType;
import com.group4.server.model.MessageWrappers.MessageWrapper;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.*;

public class MessageThread extends Thread {
    private Socket socket;
    private BufferedInputStream reader;
    private PrintWriter writer;

    private Map<MessageType, List<MessageWrapper>> sentMessages = new HashMap<>();

    @Override
    public void run() {
        while (true) {
            try {
                if(reader.available() > 1) {
                    //TODO unmarshall message
                    MessageWrapper message = new MessageWrapper();
                    if (message.getMessageType() == null /*MessageType.ANSWER*/) {
                        AnswerMessage innerMessage = (AnswerMessage) message.getEncapsulatedMessage();
                        long requestId = innerMessage.getRequestId();
                        MessageWrapper requestMessage = extractRequestMessage(requestId);
                        switch (requestMessage.getMessageType()) {
                            case AUTHORIZE:
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
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void connect() {
        try {
            socket = new Socket("localhost", 8888);
            reader = new BufferedInputStream(socket.getInputStream());
            writer = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void disconnect() {

    }

    public void sendMessage(MessageWrapper message) {
        MessageType type = message.getMessageType();
        List<MessageWrapper> messageList = sentMessages.get(type);
        if (messageList == null) {
            messageList = new LinkedList<>();
            messageList.add(message);
            sentMessages.put(type, messageList);
        } else {
            messageList.add(message);
        }

        //TODO marshall and send message
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
