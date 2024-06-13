package com.lzh.game.start.gm.facade;

import com.lzh.game.framework.socket.annotation.RequestMapping;
import com.lzh.game.framework.socket.core.session.Session;
import com.lzh.game.start.cmd.impl.CmdMessage;
import com.lzh.game.start.gm.packet.GmParam;
import com.lzh.game.start.gm.service.GmService;
import com.lzh.game.framework.socket.starter.Action;
import org.springframework.beans.factory.annotation.Autowired;

@Action
public class GmAction {

    @Autowired
    private GmService gmService;

    @RequestMapping(CmdMessage.CM_GM)
    public void gmRequest(Session session, GmParam param) {
        gmService.doGmMethod(session, param.getMethodName(), param.getValue());
    }

}
