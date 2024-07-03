package com.lzh.game.framework.socket.proto;

import com.lzh.game.framework.socket.core.protocol.message.Protocol;
import lombok.Data;

/**
 * @author zehong.l
 * @since 2024-06-28 18:03
 **/
@Protocol(1003)
@Data
public class RequestData {

    private long id;

    private int age;
}
