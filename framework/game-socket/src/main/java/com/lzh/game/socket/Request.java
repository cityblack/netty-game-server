package com.lzh.game.socket;

public interface Request extends RemotingCommand {

    int getPort();

    String getRemoteAddress();

    int getVersion();

    boolean isOneway();
}
