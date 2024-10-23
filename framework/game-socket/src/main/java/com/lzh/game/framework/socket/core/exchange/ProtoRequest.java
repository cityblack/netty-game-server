package com.lzh.game.framework.socket.core.exchange;

import com.lzh.game.framework.socket.core.bootstrap.client.AsyncResponse;

import java.util.concurrent.ExecutorService;

/**
 * @author zehong.l
 * @since 2024-10-18 16:06
 **/
public interface ProtoRequest {

    /**
     * @param param {@link com.lzh.game.framework.socket.core.protocol.message.Protocol}, {@link com.lzh.game.framework.socket.core.protocol.Request}
     */
    void oneWay(Object param);

    /**
     * @param param {@link com.lzh.game.framework.socket.core.protocol.message.Protocol}, {@link com.lzh.game.framework.socket.core.protocol.Request}
     */
    <T> AsyncResponse<T> request(Object param, Class<T> returnType, ExecutorService service);

    /**
     * @see #request(Object, Class, ExecutorService)
     */
    default <T> AsyncResponse<T> request(Object param, Class<T> returnType) {
        return request(param, returnType, null);
    }
}
