package com.group4.client.controller;

import com.group4.server.model.MessageTypes.*;
import com.group4.server.model.MessageWrappers.MessageWrapper;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MessageThread extends Thread {
    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;

    private static Class<?>[] clazzes = {MessageWrapper.class, PingMessage.class,
            AuthorizationMessage.class, AnswerMessage.class, ChatMessage.class,
            NewGroupChatMessage.class, RegistrationRequest.class, UsersInChatMessage.class
    };
    private JAXBContext context;
    private Map<MessageType, List<MessageWrapper>> sentMessages = new HashMap<>();

    @Override
    public void run() {
        while (true) {
            try {
                if(reader.ready()) {
                    StringReader dataReader = new StringReader(reader.readLine());
                    MessageWrapper message = (MessageWrapper) context.createUnmarshaller().unmarshal(dataReader);
                    if (message.getMessageType() == MessageType.ANSWER) {
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
            } catch (IOException | JAXBException e) {
                e.printStackTrace();
            }
        }
    }

    public void connect() {
        try {
            socket = new Socket("localhost", 8888);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(socket.getOutputStream(), true);
            context = JAXBContext.newInstance(clazzes);
        } catch (IOException | JAXBException e) {
            e.printStackTrace();
        }
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

        StringWriter stringWriter = new StringWriter();
        try {
            context.createMarshaller().marshal(message, stringWriter);
            writer.println(stringWriter.toString());
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
