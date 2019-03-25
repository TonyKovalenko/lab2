package com.group4.server.controller;

import com.group4.server.model.MessageTypes.AuthorizationMessage;
import com.group4.server.model.MessageTypes.PingMessage;
import com.group4.server.model.MessageWrappers.MessageWrapper;

import javax.xml.bind.*;
import java.io.*;
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
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            final StringReader dataReader = new StringReader(in.readLine());
            responseMsg = (MessageWrapper) unmarshaller.unmarshal(dataReader);
        } catch (IOException | JAXBException ex) {
            //log.error("Exception happened", ex);
            ex.printStackTrace();
            return;
        }
        switch (responseMsg.getMessageType())  {
            case PING:
            case AUTHORIZE:
        }
    }


}