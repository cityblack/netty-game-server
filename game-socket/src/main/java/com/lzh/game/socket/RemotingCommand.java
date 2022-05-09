package com.lzh.game.socket;

import com.lzh.game.socket.core.session.Session;

public interface RemotingCommand {

    int cmd();

    byte[] byteData();

    Object data();

    int commandKey();

    int remoteId();

    Session getSession();

    // request / response / oneway
    byte type();
}
