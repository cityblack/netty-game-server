package com.lzh.netty.socket.dispatcher.protocol;

import java.util.Set;

public interface ProtocolManage {

    void addProtocol(ProtocolModel model);

    boolean containProtocol(ProtocolModel model);

    Set<ProtocolModel> getAllProtocol();
}
