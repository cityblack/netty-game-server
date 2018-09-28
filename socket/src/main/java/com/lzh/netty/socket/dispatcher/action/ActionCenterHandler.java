package com.lzh.netty.socket.dispatcher.action;

import com.lzh.netty.socket.dispatcher.ServerExchange;
import com.lzh.netty.socket.exception.RequestException;
import com.lzh.netty.socket.method.InvocableHandlerMethod;
import com.lzh.netty.socket.protocol.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ActionCenterHandler implements ActionCenter {

    private ActionSupport support;

    @Override
    public void executeAction(ServerExchange exchange) {

        GameResponse response = (GameResponse) exchange.getResponse();
        GameRequest request = (GameRequest)exchange.getRequest();

        InvocableHandlerMethod invoke = support.getActionHandler(request.getProtocolId());
        if (invoke == null) {
            response.setCodeState(CodeState.NOT_FOND_PROTOCOL.getCode());
            if (log.isWarnEnabled()) {
                log.warn("not fond defined protocol. please check the protocol id is exist. protocol id: {}",request.getProtocolId());
            }
        } else {
            try {
                Object o = invoke.invokeForRequest(request);
                response.setProtocolId(invoke.getResponseProtoModel().getValue());
                response.setData(o);
                response.setCodeState(CodeState.OK.getCode());
            } catch (RequestException e) {
                response.setCodeState(e.getCodeState());
            } catch (Exception e) {
                response.setCodeState(CodeState.SERVER_ERROR.getCode());
                e.printStackTrace();
            }
        }
    }

    public void setSupport(ActionSupport support) {
        this.support = support;
    }

}
