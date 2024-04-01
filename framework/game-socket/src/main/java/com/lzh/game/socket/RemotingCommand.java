package com.lzh.game.socket;

import com.lzh.game.socket.core.session.Session;

public interface RemotingCommand {

    int cmd();

    byte[] byteData();

    Object data();

    int remoteId();

    Session getSession();

    // request / response
    byte type();
}
