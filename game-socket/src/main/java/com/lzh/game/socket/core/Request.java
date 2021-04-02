package com.lzh.game.socket.core;

import com.lzh.game.socket.core.session.Session;

public interface Request<T> {

    Session getSession();

    int getPort();

    String getRemoteAddress();

    T data();

    int getCmd();

    int getVersion();
}
