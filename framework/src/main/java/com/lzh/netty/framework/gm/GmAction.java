package com.lzh.netty.framework.gm;

import com.lzh.netty.socket.annotation.Action;
import com.lzh.netty.socket.annotation.RequestMapping;
import com.lzh.netty.socket.protocol.session.Session;
import org.springframework.beans.factory.annotation.Autowired;

@Action
public class GmAction {
    @Autowired
    private GmService gmService;

    @RequestMapping(1)
    public void gmRequest(Session session, String methodName, String value) {
        gmService.doGmMethod(session, methodName, value);
    }

}
