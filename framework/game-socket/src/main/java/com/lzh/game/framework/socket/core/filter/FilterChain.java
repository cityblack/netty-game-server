package com.lzh.game.framework.socket.core.filter;

import com.lzh.game.framework.socket.core.protocol.Request;

public interface FilterChain {

    void filter(Request request);

}
