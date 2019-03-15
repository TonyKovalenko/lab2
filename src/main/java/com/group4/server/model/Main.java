package com.group4.server.model;

import com.group4.server.model.MessageTypes.AuthorizationMessage;
import com.group4.server.model.MessageTypes.PingMessage;
import com.group4.server.model.MessageWrappers.MessageWrapper;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import java.io.File;

public class Main {

    private static Class<?>[] clazzes = {MessageWrapper.class, PingMessage.class, AuthorizationMessage.class};

    public static void main(String[] args) throws Exception {
        JAXBContext context = JAXBContext.newInstance(clazzes);
        Marshaller marshaller = context.createMarshaller();
        MessageWrapper auth = new MessageWrapper(new AuthorizationMessage("it'sme", "mypass"));
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        marshaller.marshal(auth, new File("auth.xml"));

        MessageWrapper ping = new MessageWrapper(new PingMessage("okCool", true));
        marshaller.marshal(ping, new File("ping.xml"));

        Unmarshaller unmarshaller = context.createUnmarshaller();

        StreamSource source = new StreamSource(new File("ping.xml"));
        MessageWrapper responseMsg = (MessageWrapper) unmarshaller.unmarshal(source);
        switch (responseMsg.getMessageType())  {
            case PING:
                PingMessage pingMsg = (PingMessage) responseMsg.getEncapsulatedMessage();
                System.out.println("Ping message from: " + pingMsg.getUserNickname() + ", is alive: " + pingMsg.isAlive());
                break;
            case AUTHORIZE:
                AuthorizationMessage authMsg = (AuthorizationMessage) responseMsg.getEncapsulatedMessage();
                System.out.println("Auth message from: " + authMsg.getUserNickname() + ", login: " + authMsg.getUserNickname() + ", password: " + authMsg.getPassword());
        }
    }
}
