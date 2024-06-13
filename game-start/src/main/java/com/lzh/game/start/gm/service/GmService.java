package com.lzh.game.start.gm.service;


import com.lzh.game.framework.socket.core.session.Session;

public interface GmService {

    void doGmMethod(Session session, String methodName, String value);

}
