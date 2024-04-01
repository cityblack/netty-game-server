package com.lzh.game.socket;

import com.lzh.game.common.util.Constant;

import java.util.concurrent.atomic.AtomicInteger;

public class SocketUtils {

    private static final AtomicInteger REQUEST = new AtomicInteger();

    public static GameRequest createCommonRequest(int cmd, Object data) {
        return createRequest(cmd, data, Constant.REQUEST_SIGN);
    }

    public static GameRequest createOneWayRequest(int cmd, Object data) {
        return createRequest(cmd, data, Constant.ONEWAY_SIGN);
    }

    public static GameRequest createRequest(int cmd, Object data, byte type) {
        GameRequest request = new GameRequest();
        request.setCmd(cmd);
        request.setData(data);
        request.setRemoteId(REQUEST.incrementAndGet());
        request.setType(type);
        return request;
    }
}
