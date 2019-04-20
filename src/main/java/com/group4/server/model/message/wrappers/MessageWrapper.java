package com.group4.server.model.message.wrappers;

import com.group4.server.model.message.types.MessageType;
import com.group4.server.model.message.types.TransmittableMessage;
import com.group4.server.model.message.adapters.AtomicLongAdapter;
import com.group4.server.model.message.adapters.InstantAdapter;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicLong;

@XmlRootElement(name = "messageWrapper")
@XmlAccessorType(XmlAccessType.NONE)
public class MessageWrapper {

    @XmlElement(name = "messageType")
    private MessageType messageType;

    @XmlJavaTypeAdapter(value = InstantAdapter.class)
    private Instant transmitTime;

    @XmlAnyElement(lax=true)
    private TransmittableMessage encapsulatedMessage;

    public MessageWrapper() {
    }

    public MessageWrapper(TransmittableMessage encapsulatedMessage) throws IllegalArgumentException {
        this.encapsulatedMessage = encapsulatedMessage;
        messageType = MessageType.getMessageType(encapsulatedMessage.getClass().getSimpleName());
        transmitTime = Instant.now();
    }

    public TransmittableMessage getEncapsulatedMessage() {
        return encapsulatedMessage;
    }

    public MessageType getMessageType() {
        return messageType;
    }
}
