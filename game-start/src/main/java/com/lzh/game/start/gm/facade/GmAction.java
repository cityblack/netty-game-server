package com.lzh.game.start.gm.facade;

import com.lzh.game.socket.annotation.Action;
import com.lzh.game.socket.annotation.RequestMapping;
import com.lzh.game.common.scoket.session.Session;
import com.lzh.game.start.cmd.CmdMessage;
import com.lzh.game.start.gm.packet.GmParam;
import com.lzh.game.start.gm.service.GmService;
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
