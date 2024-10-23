package com.lzh.game.framework.gateway;

import com.lzh.game.framework.socket.core.protocol.message.Protocol;
import lombok.Data;

import java.util.*;

/**
 * @author zehong.l
 * @since 2024-06-28 18:03
 **/
@Data
@Protocol(1003)
public class RequestData {

    private long id = 10086;

    private int age = 30;

    private List<Integer> list = List.of(1,2,3,4);

    private Set<Long> set = Set.of(1L, 100L, 1000L, 10000L);

    private Map<Long, Integer> map = Map.of(100L, 100);
}
