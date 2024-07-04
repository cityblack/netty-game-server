package com.lzh.game.framework.socket.core.invoke.convert.impl;

import com.lzh.game.framework.socket.core.invoke.convert.RequestConvert;
import com.lzh.game.framework.socket.core.protocol.Request;

/**
 * @author zehong.l
 * @since 2024-07-04 16:42
 **/
public class StringConvert implements RequestConvert<String> {

    @Override
    public String convert(Request request) {
        return (String) request.getData();
    }

    @Override
    public boolean match(Class<?> type) {
        return type == String.class;
    }
}
