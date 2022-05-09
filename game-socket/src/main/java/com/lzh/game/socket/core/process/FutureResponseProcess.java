package com.lzh.game.socket.core.process;

import com.lzh.game.common.serialization.ProtoBufUtils;
import com.lzh.game.socket.GameResponse;
import com.lzh.game.socket.core.RemoteContext;
import com.lzh.game.socket.core.RequestFuture;

import java.util.Objects;

public class FutureResponseProcess implements Process<GameResponse> {

    @Override
    public void process(RemoteContext context, GameResponse response) {
        response.setSession(context.getSession());
        RequestFuture<?> future = RequestFuture.getFuture(response.remoteId());
        if (Objects.isNull(future)) {
            return;
        }
        Class<?> type = future.getResponseType();
        response.setData(ProtoBufUtils.deSerialize(response.byteData(), type));
        RequestFuture.received(response, false);
    }
}
