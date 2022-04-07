package com.lzh.game.common.scoket;

import com.lzh.game.common.scoket.coder.Cmd;
import com.lzh.game.common.scoket.session.Session;

public interface Request extends Cmd {

    Session getSession();

    int getPort();

    String getRemoteAddress();

    Object data();

    int getVersion();
}
