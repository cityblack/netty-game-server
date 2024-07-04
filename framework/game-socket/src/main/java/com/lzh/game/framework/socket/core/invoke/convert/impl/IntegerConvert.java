package com.lzh.game.framework.socket.core.invoke.convert.impl;

import com.lzh.game.framework.socket.core.invoke.convert.RequestConvert;
import com.lzh.game.framework.socket.core.protocol.Request;

/**
 * @author zehong.l
 * @since 2024-07-04 16:40
 **/
public class IntegerConvert implements RequestConvert<Integer> {

    @Override
    public Integer convert(Request request) {
        return (Integer) request.getData();
    }

    @Override
    public boolean match(Class<?> type) {
        return type == Integer.class || type == int.class;
    }
}
