package com.group4.server.model.messageWrappers;

import com.group4.server.model.messageTypes.MessageType;
import com.group4.server.model.messageTypes.TransmittableMessage;
import com.group4.server.model.XMLAdapters.AtomicLongAdapter;
import com.group4.server.model.XMLAdapters.InstantAdapter;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicLong;

@XmlRootElement(name = "messageWrapper")
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
        messageType = MessageType.getMessageType(encapsulatedMessage.getClass().getSimpleName());
        transmitTime = Instant.now();
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
