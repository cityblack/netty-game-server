package com.lzh.game.start.gm.facade;

import com.lzh.game.framework.socket.core.invoke.Receive;
import com.lzh.game.framework.socket.core.session.Session;
import com.lzh.game.framework.socket.starter.anno.Action;
import com.lzh.game.start.gm.packet.GmParam;
import com.lzh.game.start.gm.service.GmService;
import org.springframework.beans.factory.annotation.Autowired;

@Action
public class GmAction {

    @Autowired
    private GmService gmService;

    @Receive
    public void gmRequest(Session session, GmParam param) {
        gmService.doGmMethod(session, param.getMethodName(), param.getValue());
    }

}
