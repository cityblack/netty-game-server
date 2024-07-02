package com.lzh.game.framework.socket.core.invoke.convert.impl;

import com.lzh.game.framework.socket.core.invoke.convert.RequestConvert;
import com.lzh.game.framework.socket.core.protocol.Request;
import lombok.AllArgsConstructor;

/**
 * @author zehong.l
 * @since 2024-06-28 12:14
 **/
@AllArgsConstructor
public class ComposeProtocolConvert implements RequestConvert {

    private int index;

    private Object[] fields;

    @Override
    public Object convert(Request request) {
        return fields[index];
    }

    @Override
    public boolean match(Class type) {
        return true;
    }

}