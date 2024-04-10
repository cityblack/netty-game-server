package com.lzh.game.socket.core.process;

import com.lzh.game.socket.core.protocol.Request;

public interface RequestDispatch {

    void handle(Request request);
}
