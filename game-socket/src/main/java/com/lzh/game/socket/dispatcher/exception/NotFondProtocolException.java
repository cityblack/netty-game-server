package com.lzh.game.socket.dispatcher.exception;

public class NotFondProtocolException extends RuntimeException {

    private int protocol;

    public NotFondProtocolException(int protocol) {
        this.protocol = protocol;
    }

    public int getProtocol() {
        return protocol;
    }
}
