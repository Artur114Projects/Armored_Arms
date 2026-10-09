package com.artur114.armoredarms.core.util;

public interface ITextMessage {
    ITextMessage withFormating(MessageFormating formating);
    void sendMessage();
    String string();
}
