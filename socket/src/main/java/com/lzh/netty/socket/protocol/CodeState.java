package com.lzh.netty.socket.protocol;

public enum CodeState {

    OK(200),
    NOT_FOND_PROTOCOL(404),
    SERVER_ERROR(500);

    private final int code;

    CodeState(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
