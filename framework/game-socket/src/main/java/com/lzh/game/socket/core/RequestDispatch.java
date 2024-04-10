package com.lzh.game.socket.core;

import com.lzh.game.socket.Request;

public interface RequestDispatch {

    void handle(Request request);
}
