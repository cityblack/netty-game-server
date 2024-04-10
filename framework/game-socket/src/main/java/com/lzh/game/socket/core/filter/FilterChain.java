package com.lzh.game.socket.core.filter;

import com.lzh.game.socket.core.protocol.Request;

public interface FilterChain {

    void filter(Request request);

}
