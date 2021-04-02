package com.lzh.game.socket.core.invoke;

import com.lzh.game.socket.core.Request;
import org.springframework.core.MethodParameter;

/**
 * Inner param convent handler
 */
public interface InnerParamDataBindHandler<T> {

    Object conventData(Request<T> request, MethodParameter parameter);

    boolean isInnerParam(MethodParameter parameter);
}
