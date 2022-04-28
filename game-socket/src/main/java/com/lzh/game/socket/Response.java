package com.lzh.game.socket;


import com.lzh.game.common.util.Constant;

public interface Response extends RemotingCmd {

    int OK = 0;

    int FAIL = 1;

    int status();

    @Override
    default int commandKey() {
        return Constant.RESPONSE_SIGN;
    }
}
