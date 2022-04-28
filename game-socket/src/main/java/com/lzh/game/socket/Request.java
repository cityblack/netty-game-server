package com.lzh.game.socket;

import com.lzh.game.common.util.Constant;
import com.lzh.game.socket.core.session.Session;

public interface Request extends RemotingCmd {

    Session getSession();

    int getPort();

    String getRemoteAddress();


    int getVersion();

    long requestId();

    @Override
    default int commandKey() {
        return Constant.REQUEST_SIGN;
    }
}
