package com.lzh.game.framework.socket.proto;

import com.lzh.game.framework.socket.core.protocol.message.Protocol;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author zehong.l
 * @since 2024-06-28 18:03
 **/
@Data
@Protocol(-10086)
@NoArgsConstructor
public class RequestData {

    public RequestData(long id, int age, String name, double price, float tail) {
        this.id = id;
        this.age = age;
        this.name = name;
        this.price = price;
        this.tail = tail;
        this.list = List.of(1,2,3);
        this.set = Set.of(1L, 100L, 1000L, 10000L);
        this.map = Map.of(100L, 100);
    }

    private long id;

    private int age;

    private String name;

    private double price;

    private float tail;

    private List<Integer> list;

    private Set<Long> set;

    private Map<Long, Integer> map ;
}
