package com.group4.server.model.message.types;

import javax.xml.bind.annotation.*;

@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class ChatUpdateMessageResponse implements TransmittableMessage {

    @XmlElement
    private long chatId;

    @XmlElement
    private boolean actionDone;

    public ChatUpdateMessageResponse() {
    }

    public ChatUpdateMessageResponse(long chatId, boolean actionDone) {
        this.chatId = chatId;
        this.actionDone = actionDone;
    }

    public long getChatId() {
        return chatId;
    }

    public void setChatId(long chatId) {
        this.chatId = chatId;
    }

    public boolean isActionDone() {
        return actionDone;
    }

    public void setActionDone(boolean actionDone) {
        this.actionDone = actionDone;
    }
}
