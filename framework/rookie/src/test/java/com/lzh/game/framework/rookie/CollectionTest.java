package com.lzh.game.framework.rookie;

import io.netty.buffer.UnpooledByteBufAllocator;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zehong.l
 * @since 2024-12-09 16:17
 **/
public class CollectionTest {

    @Test
    public void mapTest() {
        var buf = UnpooledByteBufAllocator.DEFAULT.heapBuffer(100);
        Map<Integer, int[]> map = new HashMap<>();
        map.put(1, new int[]{1, 2, 3});
        map.put(2, new int[]{4, 5, 6});
        Rookie rookie = new Rookie();
        rookie.serializer(buf, map);
        var result = rookie.deserializer(buf, Map.class);
        System.out.println(result);
    }

    @Test
    public void mapListTest() {
        var buf = UnpooledByteBufAllocator.DEFAULT.heapBuffer(100);
        Map<Integer, List<Integer>> map = new HashMap<>();
        map.put(1, List.of(1, 2, 3));
        map.put(2, List.of(4, 5, 6));
        Rookie rookie = new Rookie();
        rookie.serializer(buf, map);
        var result = rookie.deserializer(buf, Map.class);
        System.out.println(result);
    }

    @Test
    public void listListTest() {
        var buf = UnpooledByteBufAllocator.DEFAULT.heapBuffer(100);
        List<List<Integer>> list = new ArrayList<>();
        list.add(List.of(1, 2, 3));
        list.add(List.of(4, 5, 6));
        Rookie rookie = new Rookie();
        rookie.serializer(buf, list);
        var result = rookie.deserializer(buf, List.class);
        System.out.println(result);
    }
}
