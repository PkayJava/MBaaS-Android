package com.angkorteam.mbaas.sdk.android.library.command;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by socheat on 5/14/16.
 */
public class MessageGroup {

    @Expose
    @SerializedName("conversationId")
    private String conversationId;

    @Expose
    @SerializedName("message")
    private String message;

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
