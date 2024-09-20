package com.lzh.game.framework.rookie.model;

import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author zehong.l
 * @since 2024-09-20 16:13
 **/
@Data
public class OT {
    protected int id;
    protected short s;
    protected double tail;
    protected float price;
    protected long ip;
    protected String desc;
    protected OtInner inner;
    protected AbstractItem item;

    protected List<AbstractItem> list;
    protected AbstractItem[] arr;
    protected Set<AbstractItem> set;
    private List<Integer> ints;

    private Map<Long, Integer> map;

    public static OT createOT() {
        var ot = new OT();
        ot.id = -1;
        ot.s = Short.MAX_VALUE;
        ot.tail = 175.1D;
        ot.price = -1008f;
        ot.ip = System.currentTimeMillis();
        ot.desc = "GangZhou";
        ot.inner = new OtInner();
        ot.item = new Item();
        ot.list = Stream.of(new Item()).collect(Collectors.toList());
        ot.arr = new AbstractItem[]{new Item()};
        ot.set = Stream.of(new Item()).collect(Collectors.toSet());
        ot.ints = Stream.of(1009, 1010, 1022).collect(Collectors.toList());
        var map = new HashMap<Long, Integer>();
        map.put(Long.MIN_VALUE, 100);
        map.put(Long.MAX_VALUE, 100);
        ot.map = map;
        return ot;
    }
}
