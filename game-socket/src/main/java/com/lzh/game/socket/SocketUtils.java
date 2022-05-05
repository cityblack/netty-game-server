package com.lzh.game.socket;

import java.util.concurrent.atomic.AtomicInteger;

public class SocketUtils {

    private static final AtomicInteger REQUEST = new AtomicInteger();

    public static Request createRequest(int commandKey, int cmd, Object data) {
        GameRequest request = new GameRequest();
        request.setCmd(cmd);
        request.setData(data);
        request.setCommonKey(commandKey);
        request.setRemoteId(REQUEST.incrementAndGet());
        return request;
    }
}
