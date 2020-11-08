package com.sanchit.groupchatappj.models;

import org.jetbrains.annotations.NotNull;
import java.util.Date;

/**
 * Model class containing details of message from
 * a particular user
 *
 * @author Sanchit Vasdev
 * @version 1.0, 11/06/2020
 */
public final class Message {
    private String messageUser;
    private String messageText;
    private String messageUserId;
    private long messageTime;

    @NotNull
    public final String getMessageText() {
        return this.messageText;
    }

    @NotNull
    public final String getMessageUserId() {
        return this.messageUserId;
    }

    public final long getMessageTime() {
        return this.messageTime;
    }

    public Message(@NotNull String messageUser, @NotNull String messageText, @NotNull String messageUserId) {
        super();
        this.messageUser = messageUser;
        this.messageText = messageText;
        this.messageTime = (new Date()).getTime();
        this.messageUserId = messageUserId;
    }

    public Message() {
        this("", "", "");
    }
}
