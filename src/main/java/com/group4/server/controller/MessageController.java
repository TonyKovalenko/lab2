package com.group4.server.controller;

import com.group4.server.model.Entities.User;
import com.group4.server.model.MessageHandlers.RegistrationHandler;
import com.group4.server.model.MessageTypes.*;
import com.group4.server.model.MessageWrappers.MessageWrapper;

import javax.xml.bind.*;
import java.io.*;
import java.net.Socket;
import java.util.TreeMap;

public class MessageController {

    private static Class<?>[] clazzes = {
            MessageWrapper.class,
            PingMessage.class,
            AuthorizationMessage.class,
            User.class,
            RegistrationRequest.class,
            RegistrationResponse.class
    };

    private JAXBContext context;
    private Marshaller marshaller;
    private Unmarshaller unmarshaller;
    private Socket socket;

    MessageController(Socket socket) throws JAXBException {
        this.socket = socket;
        this.context = JAXBContext.newInstance(clazzes);
        this.marshaller = context.createMarshaller();
        this.unmarshaller = context.createUnmarshaller();
    }

    void handle() {
        BufferedReader in;
        PrintWriter out;
        StringWriter stringWriter;
        StringReader stringReader;
        MessageWrapper requestMessage;
        MessageWrapper responseMessage;
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            stringReader = new StringReader(in.readLine());
            requestMessage = (MessageWrapper) unmarshaller.unmarshal(stringReader);
        } catch (IOException | JAXBException ex) {
            //log.error("Exception happened", ex);
            ex.printStackTrace();
            return;
        }

        switch (requestMessage.getMessageType()) {
            case REGISTRATION_REQUEST:
                RegistrationResponse registrationResponse = RegistrationHandler.INSTANCE.handle((RegistrationRequest) requestMessage.getEncapsulatedMessage());
                if (registrationResponse.isRegistrationSuccessful()) {
                    //TODO add user stream
                }
                stringWriter = new StringWriter();
                try {
                    marshaller.marshal(new MessageWrapper(registrationResponse), stringWriter);
                } catch (JAXBException ex) {
                    //log.error("Exception happened", ex);
                }
                out.println(stringWriter.toString());
            case PING:
            case AUTHORIZE:
        }
    }


}