package com.group4.server.model.message.types;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "disconnectMessage")
@XmlAccessorType(XmlAccessType.NONE)
public class UserDisconnectMessage implements TransmittableMessage {
}
