package com.lzh.game.socket.core;

/**
 * @author zehong.l
 * @date 2024-04-07 15:08
 **/
public class AbstractRemotingCommand {

    private int msgId;
    private int requestId;

    private Object date;

    private Class<?> dataClass;

    public int getRequestId() {
        return requestId;
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }

    public Object getDate() {
        return date;
    }

    public void setDate(Object date) {
        this.date = date;
    }

    public Class<?> getDataClass() {
        return dataClass;
    }

    public void setDataClass(Class<?> dataClass) {
        this.dataClass = dataClass;
    }

    public int getMsgId() {
        return msgId;
    }

    public void setMsgId(int msgId) {
        this.msgId = msgId;
    }
}
