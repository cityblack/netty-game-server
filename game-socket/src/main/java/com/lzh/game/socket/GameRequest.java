package com.lzh.game.socket;

import com.lzh.game.common.util.Constant;

import java.io.Serializable;

public class GameRequest extends AbstractRemotingCommand
        implements Request, Serializable {

    private static final long serialVersionUID = 1550620526955432911L;

    private int version;

    @Override
    public int getVersion() {
        return version;
    }

    @Override
    public boolean isOneway() {
        return this.type() == Constant.ONEWAY_SIGN;
    }

    @Override
    public int getPort() {
        return getSession().getPort();
    }

    @Override
    public String getRemoteAddress() {
        return getSession().getRemoteAddress();
    }

    public void setVersion(int version) {
        this.version = version;
    }
}
