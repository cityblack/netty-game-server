package com.lzh.netty.socket.protocol;

import com.lzh.netty.socket.protocol.coder.AbstractResponse;

public interface Response {

    Object data();

    int codeState();

    int protocolId();

    void setData(Object o);

    void setCodeState(int state);

    void setProtocolId(int id);

    /**
     * Transform response to protocol response so that send to client
     * @return
     */
    AbstractResponse.Response toProtoResponse();
}
