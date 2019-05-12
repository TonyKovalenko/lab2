package com.group4.server.model.message.utils;

import com.group4.server.model.message.types.*;
import com.group4.server.model.message.wrappers.MessageWrapper;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.io.StringReader;
import java.io.StringWriter;

public class MarshallingUtils {
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
            DeleteUserRequest.class, DeleteUserResponse.class, SetBanStatusMessage.class
    };
    private static JAXBContext context;

    static {
        try {
            context = JAXBContext.newInstance(clazzes);
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }

    public static String marshallMessage(MessageWrapper message) throws JAXBException {
        if (context == null) {
            throw new JAXBException("Context wasn't created");
        }
        StringWriter stringWriter = new StringWriter();
        context.createMarshaller().marshal(message, stringWriter);
        return stringWriter.toString().replaceAll("\n", lineBreakEscape);
    }

    public static MessageWrapper unmarshallMessage(String s) throws JAXBException {
        if (context == null) {
            throw new JAXBException("Context wasn't created");
        }
        try (StringReader dataReader = new StringReader(s.replaceAll(lineBreakEscape, "\n"))) {
            MessageWrapper message = (MessageWrapper) context.createUnmarshaller().unmarshal(dataReader);
            return message;
        }
    }

}
