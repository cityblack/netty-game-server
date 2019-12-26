package com.lzh.game.socket.dispatcher.action.convent;

import com.lzh.game.socket.exchange.Request;
import org.springframework.core.MethodParameter;

/**
 * Inner param convent handler
 */
public interface InnerParamDataBindHandler<T> {

    Object conventData(Request<T> request, MethodParameter parameter);

    boolean isInnerParam(MethodParameter parameter);
}
