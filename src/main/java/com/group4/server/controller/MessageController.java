package com.group4.server.controller;

import com.group4.server.model.MessageTypes.AuthorizationMessage;
import com.group4.server.model.MessageTypes.PingMessage;
import com.group4.server.model.MessageWrappers.MessageWrapper;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class MessageController {

    private static Class<?>[] clazzes = {MessageWrapper.class, PingMessage.class, AuthorizationMessage.class};
    private JAXBContext context;
    private Marshaller marshaller;
    private Unmarshaller unmarshaller;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    MessageController(Socket socket) throws JAXBException {
        this.socket = socket;
        this.context = JAXBContext.newInstance(clazzes);
        this.marshaller = context.createMarshaller();
        this.unmarshaller = context.createUnmarshaller();
    }

    void handle() {
        MessageWrapper responseMsg;
        try {
            StreamSource streamSource = new StreamSource(socket.getInputStream());
            responseMsg = (MessageWrapper) unmarshaller.unmarshal(streamSource);
        } catch (IOException | JAXBException ex) {
            //log.error("Exception happened", ex);
            return;
        }
        switch (responseMsg.getMessageType())  {
            case PING:
//                PingMessage pingMsg = (PingMessage) responseMsg.getEncapsulatedMessage();
//                System.out.println("Ping message from: " + pingMsg.getUserNickname() + ", is alive: " + pingMsg.isAlive());
//                break;
            case AUTHORIZE:
//                AuthorizationMessage authMsg = (AuthorizationMessage) responseMsg.getEncapsulatedMessage();
//                System.out.println("Auth message from: " + authMsg.getUserNickname() + ", login: " + authMsg.getUserNickname() + ", password: " + authMsg.getPassword());
        }
    }


}