package com.group4.server.model.message.utils;

import com.group4.server.model.message.types.*;
import com.group4.server.model.message.wrappers.MessageWrapper;
import org.apache.log4j.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.io.StringReader;
import java.io.StringWriter;

/**
 * Handles operation of marshalling and unmarshalling messages
 */
public class MarshallingUtils {
    private static final Logger log = Logger.getLogger(MarshallingUtils.class);
    private final static String lineBreakEscape = "<br />";
    private static Class<?>[] clazzes = {MessageWrapper.class, PingMessage.class,
            RegistrationRequest.class, RegistrationResponse.class,
            AuthorizationRequest.class, AuthorizationResponse.class,
            ChatMessage.class, ChatRoomCreationRequest.class, ChatRoomCreationResponse.class,
            ChatInvitationMessage.class, ChatSuspensionMessage.class,
            OnlineListMessage.class,
            ChangeCredentialsRequest.class, ChangeCredentialsResponse.class,
            ChatUpdateMessage.class, ChatUpdateMessageResponse.class,
            GetAllUsersRequest.class, GetAllUsersResponse.class,
            UserLogoutMessage.class, UserDisconnectMessage.class,
            DeleteUserRequest.class, DeleteUserResponse.class, SetBanStatusMessage.class,
            ServerRestartMessage.class, ServerShutdownMessage.class
    };
    private static JAXBContext context;

    static {
        try {
            context = JAXBContext.newInstance(clazzes);
        } catch (JAXBException e) {
            log.error("Can't create JAXB context.");
        }
    }

    /**
     * Marshals message
     * @param message message to be marshaled
     * @return marshaled message
     * @throws JAXBException if message can't be marshaled or context wasn't created
     */
    public static String marshalMessage(MessageWrapper message) throws JAXBException {
        if (context == null) {
            throw new JAXBException("Context wasn't created");
        }
        StringWriter stringWriter = new StringWriter();
        context.createMarshaller().marshal(message, stringWriter);
        return stringWriter.toString().replaceAll("\n", lineBreakEscape);
    }

    /**
     * Unmarshals message
     * @param s marshaled message
     * @return unmarshaled meassage
     * @throws JAXBException if message can't be unmarshaled or context wasn't created
     */
    public static MessageWrapper unmarshalMessage(String s) throws JAXBException {
        if (context == null) {
            throw new JAXBException("Context wasn't created");
        }
        try (StringReader dataReader = new StringReader(s.replaceAll(lineBreakEscape, "\n"))) {
            MessageWrapper message = (MessageWrapper) context.createUnmarshaller().unmarshal(dataReader);
            return message;
        }
    }

}
