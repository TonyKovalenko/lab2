package com.group4.server.model.MessageTypes;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@XmlRootElement(name = "answerMessage")
@XmlAccessorType(XmlAccessType.NONE)
public class AnswerMessage implements TransmittableMessage, Serializable {
    @XmlElement
    private AnswerType answerType;
    @XmlElement
    private long requestId;

    public AnswerMessage() {
    }

    public AnswerMessage(AnswerType answerType, long requestId) {
        this.answerType = answerType;
        this.requestId = requestId;
    }

    public AnswerType getAnswerType() {
        return answerType;
    }

    public void setAnswerType(AnswerType answerType) {
        this.answerType = answerType;
    }

    public long getRequestId() {
        return requestId;
    }

    public void setRequestId(long requestId) {
        this.requestId = requestId;
    }
}
