package com.group4.server.model.message.types;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "serverShutdownMessage")
public class ServerShutdownMessage implements TransmittableMessage {
}
