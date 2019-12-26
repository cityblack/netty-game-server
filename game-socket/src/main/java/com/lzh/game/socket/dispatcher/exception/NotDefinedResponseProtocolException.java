package com.lzh.game.socket.dispatcher.exception;

/**
 * 当request返回值不为空， 且没设置返回协议号的时候
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
