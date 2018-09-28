package com.lzh.netty.socket.dispatcher.action;

import com.lzh.netty.socket.dispatcher.protocol.ProtocolModel;
import com.lzh.netty.socket.method.InvocableHandlerMethod;

import java.util.Set;

/**
 * Invoke {@link com.lzh.netty.socket.annotation.RequestMapping} mapping method
 */
public interface ActionSupport {

    InvocableHandlerMethod getActionHandler(int protocolId);

}
