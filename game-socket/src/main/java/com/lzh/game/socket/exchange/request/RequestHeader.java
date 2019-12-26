package com.lzh.game.socket.exchange.request;

import lombok.Data;

@Data
public class RequestHeader {

    private int cmd;

    private int version;

    public static RequestHeader of(int protocol, int version) {
        RequestHeader header = new RequestHeader();
        header.setCmd(protocol);
        header.setVersion(version);
        return header;
    }
}
