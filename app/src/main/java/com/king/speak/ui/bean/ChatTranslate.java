package com.king.speak.ui.bean;

import java.io.Serializable;

/**
 * Created by maesinfo on 2018/9/14.
 */

public class ChatTranslate implements Serializable {

    private String messageId;
    private String sourceContent;
    private String translateContent;
    private String translateVoice;

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getSourceContent() {
        return sourceContent;
    }

    public void setSourceContent(String sourceContent) {
        this.sourceContent = sourceContent;
    }

    public String getTranslateContent() {
        return translateContent;
    }

    public void setTranslateContent(String translateContent) {
        this.translateContent = translateContent;
    }

    public String getTranslateVoice() {
        return translateVoice;
    }

    public void setTranslateVoice(String translateVoice) {
        this.translateVoice = translateVoice;
    }
}
