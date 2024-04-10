package com.lzh.game.socket.core.filter;

import com.lzh.game.socket.Request;

public interface FilterChain {

    void filter(Request request);

}
