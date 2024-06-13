package com.lzh.game.framework.socket.exception;

public class NotFondProtocolException extends RuntimeException {

    private int protocol;

    public NotFondProtocolException(int protocol) {
        this.protocol = protocol;
    }

    public int getProtocol() {
        return protocol;
    }
}
