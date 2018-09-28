package com.lzh.netty.framework.gm;

import com.lzh.netty.socket.protocol.session.Session;

public interface GmService {

    void doGmMethod(Session session, String methodName, String value);

}
