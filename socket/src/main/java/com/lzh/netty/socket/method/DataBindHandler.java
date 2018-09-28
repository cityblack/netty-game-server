package com.lzh.netty.socket.method;

import org.springframework.core.MethodParameter;

public interface DataBindHandler {

    Object conventData(String value, MethodParameter parameter);
}
