package com.lzh.game.socket;

import com.lzh.game.socket.core.filter.Filter;

public interface GameServerManage {

    void addLastFilter(Filter filter);

    void addFirstFilter(Filter filter);
}
