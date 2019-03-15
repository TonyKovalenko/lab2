package com.group4.server.model.MessageWrappers;

import com.group4.server.model.MessageTypes.MessageType;
import com.group4.server.model.MessageTypes.TransmittableMessage;
import com.group4.server.model.XMLAdapters.AtomicLongAdapter;
import com.group4.server.model.XMLAdapters.InstantAdapter;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicLong;

@XmlRootElement(name = "MessageWrapper")
@XmlAccessorType(XmlAccessType.NONE)
public class MessageWrapper {

    @XmlAttribute(name = "msgID")
    @XmlJavaTypeAdapter(value = AtomicLongAdapter.class)
    private static AtomicLong messageID;

    @XmlElement(name = "messageType")
    private MessageType messageType;

    @XmlJavaTypeAdapter(value = InstantAdapter.class)
    private Instant transmitTime;

    @XmlAnyElement(lax=true)
    private TransmittableMessage encapsulatedMessage;

    static {
        messageID = new AtomicLong();
    }

    {
        messageID.incrementAndGet();
    }

    public MessageWrapper() {
    }

    public MessageWrapper(TransmittableMessage encapsulatedMessage) throws IllegalArgumentException {
        this.encapsulatedMessage = encapsulatedMessage;
        messageType = resolveEncapsulatedMessageType(encapsulatedMessage);
        transmitTime = Instant.now();
    }

    private MessageType resolveEncapsulatedMessageType(TransmittableMessage encapsulatedMessage) {
        String type = encapsulatedMessage.getClass().getSimpleName();
        for(MessageType msgType : MessageType.values()) {
            if(msgType.getValue().equals(type)) {
                return msgType;
            }
        }
        throw new IllegalArgumentException("Unknown encapsulated message type found: " + type);
    }

    public TransmittableMessage getEncapsulatedMessage() {
        return encapsulatedMessage;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public long getMessageId() {
        return messageID.longValue();
    }
}
