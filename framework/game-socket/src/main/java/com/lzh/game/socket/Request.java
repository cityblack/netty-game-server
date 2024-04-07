package com.lzh.game.socket;

import com.lzh.game.socket.core.AbstractRemotingCommand;
import com.lzh.game.socket.core.session.Session;

import java.io.Serializable;

public class Request extends AbstractRemotingCommand implements Serializable {

    private static final long serialVersionUID = 1550620526955432911L;

    private Session session;

    public static Request of(int msgId, int requestId, Object data) {
        Request request = new Request();
        request.setMsgId(msgId);
        request.setDate(data);
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

    //    private
}
