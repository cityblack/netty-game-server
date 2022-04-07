package com.lzh.game.socket.core.invoke;

import com.lzh.game.common.scoket.Request;
import org.springframework.core.MethodParameter;

/**
 * Inner param convent handler
 */
public interface InnerParamDataBindHandler {

    Object conventData(Request request, MethodParameter parameter);

    boolean isInnerParam(MethodParameter parameter);
}
