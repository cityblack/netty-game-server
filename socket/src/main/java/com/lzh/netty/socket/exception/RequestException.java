package com.lzh.netty.socket.exception;


import com.lzh.netty.socket.protocol.CodeState;

public class RequestException extends RuntimeException {

    private int codeState;

    public RequestException(int codeState) {
        this.codeState = codeState;
    }

    public int getCodeState() {
        return codeState;
    }
}
