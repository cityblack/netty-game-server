package com.lzh.game.socket.utils;

import com.lzh.game.common.util.Constant;
import com.lzh.game.socket.core.protocol.Request;

import java.util.concurrent.atomic.AtomicInteger;

public class SocketUtils {

    private static final AtomicInteger REQUEST = new AtomicInteger();

    public static Request createCommonRequest(int cmd, Object data) {
        return createRequest(cmd, data, Constant.REQUEST_SIGN);
    }

    public static Request createOneWayRequest(int cmd, Object data) {
        return createRequest(cmd, data, Constant.ONEWAY_SIGN);
    }

    public static Request createRequest(int cmd, Object data, byte type) {
        Request request = new Request();
        request.setMsgId(cmd);
        request.setData(data);
        request.setRequestId(REQUEST.incrementAndGet());
        request.setType(type);
        return request;
    }
}
