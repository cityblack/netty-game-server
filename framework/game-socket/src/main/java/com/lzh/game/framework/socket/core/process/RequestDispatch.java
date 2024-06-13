package com.lzh.game.framework.socket.core.process;

import com.lzh.game.framework.socket.core.protocol.Request;

public interface RequestDispatch {

    void handle(Request request);
}
