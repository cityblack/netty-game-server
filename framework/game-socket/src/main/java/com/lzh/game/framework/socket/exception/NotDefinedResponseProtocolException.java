package com.lzh.game.framework.socket.exception;

/**
 *
 */
public class NotDefinedResponseProtocolException extends RuntimeException {

    private int protocol;

    public NotDefinedResponseProtocolException(int protocol) {
        this.protocol = protocol;
    }

    public int getProtocol() {
        return protocol;
    }
}
