package com.lzh.game.socket.core.filter;

import com.lzh.game.socket.core.RemoteContext;

public interface FilterChain {

    void filter(RemoteContext context);

}
