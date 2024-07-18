package com.lzh.game.framework.socket.core.protocol;

import com.lzh.game.framework.socket.utils.Constant;
import com.lzh.game.framework.socket.core.protocol.message.MessageDefine;
import com.lzh.game.framework.socket.core.session.Session;

import java.io.Serializable;

public class Request extends AbstractCommand implements Serializable {

    private static final long serialVersionUID = 1550620526955432911L;

    private Session session;

    private Response response;

    private MessageDefine define;

    public static Request of(short msgId, MessageDefine define, int requestId, Object data) {
        Request request = new Request();
        request.define = define;
        request.setMsgId(msgId);
        request.setData(data);
        request.setDataClass(data.getClass());
        request.setRequestId(requestId);
        return request;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    public MessageDefine getDefine() {
        return define;
    }

    public boolean isOneWay() {
        return this.getType() == Constant.ONEWAY_SIGN;
    }
    //    private
}
