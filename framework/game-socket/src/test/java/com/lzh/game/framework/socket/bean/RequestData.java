package com.lzh.game.framework.socket.bean;

import com.lzh.game.framework.socket.core.protocol.message.Protocol;
import lombok.Data;

/**
 * @author zehong.l
 * @since 2024-06-28 18:03
 **/
@Protocol(10086)
@Data
public class RequestData {

    private long id;

    private int age;
}
