package com.lzh.game.framework.socket.proto;

import com.lzh.game.framework.socket.core.protocol.message.Protocol;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zehong.l
 * @since 2024-06-28 18:03
 **/
@Data
@Protocol(-10086)
@AllArgsConstructor
@NoArgsConstructor
public class RequestData {

    private long id;

    private int age;

    private String name;

    private double price;

    private float tail;
}
