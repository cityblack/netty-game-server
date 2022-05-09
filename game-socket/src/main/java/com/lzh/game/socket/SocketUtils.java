package com.lzh.game.socket;

import com.lzh.game.common.util.Constant;

import java.util.concurrent.atomic.AtomicInteger;

public class SocketUtils {

    private static final AtomicInteger REQUEST = new AtomicInteger();

    public static GameRequest createRequest(int commandKey, int cmd, Object data) {
        return createRequest(commandKey, cmd, data, Constant.REQUEST_SIGN);
    }

    public static GameRequest createRequest(int cmd, Object data) {
        return createRequest(Constant.REQUEST_COMMAND_KEY, cmd, data);
    }

    public static GameRequest createRequest(int commandKey, int cmd, Object data, byte type) {
        GameRequest request = new GameRequest();
        request.setCmd(cmd);
        request.setData(data);
        request.setCommonKey(commandKey);
        request.setRemoteId(REQUEST.incrementAndGet());
        request.setType(type);
        return request;
    }
}
