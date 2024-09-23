package com.lzh.game.framework.socket.core.filter;

import com.lzh.game.framework.socket.core.protocol.Request;

import java.util.function.Consumer;

public interface FilterChain {

    void filter(Request request);

}
