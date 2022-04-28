package com.lzh.game.socket.core.invoke;

import com.lzh.game.socket.Request;
import org.springframework.core.MethodParameter;

/**
 * Request inner param convent handler
 */
public interface InnerParamBindHandler {

    Object conventData(Request request, MethodParameter parameter);

    boolean isInnerParam(MethodParameter parameter);

    InnerParamBindHandler EMPTY = new InnerParamBindHandler() {
        @Override
        public Object conventData(Request request, MethodParameter parameter) {
            return null;
        }

        @Override
        public boolean isInnerParam(MethodParameter parameter) {
            return false;
        }
    };
}
