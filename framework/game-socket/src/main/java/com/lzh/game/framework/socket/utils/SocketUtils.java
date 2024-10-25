package com.lzh.game.framework.socket.utils;

import com.lzh.game.framework.socket.Constant;
import com.lzh.game.framework.socket.core.protocol.Request;

import java.util.concurrent.atomic.AtomicInteger;

public class SocketUtils {

    private static final AtomicInteger REQUEST = new AtomicInteger();

    public static Request createRequest(short msgId, Object data) {
        return createRequest(msgId, data, Constant.REQUEST_SIGN);
    }

    public static Request createOneWayRequest(short msgId, Object data) {
        return createRequest(msgId, data, Constant.ONEWAY_SIGN);
    }

    public static Request createRequest(short msgId, Object data, byte type) {
        Request request = new Request();
        request.setMsgId(msgId);
        request.setData(data);
        request.setRequestId(REQUEST.incrementAndGet());
        request.setType(type);
        return request;
    }
}
