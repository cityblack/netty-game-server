package com.lzh.netty.start.controller;

import com.lzh.netty.socket.annotation.Action;
import com.lzh.netty.socket.annotation.RequestMapping;
import com.lzh.netty.socket.annotation.ResponseMapping;
import com.lzh.netty.socket.exception.RequestException;
import com.lzh.netty.socket.protocol.session.Session;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Action
public class HelloWorldController {

    @RequestMapping(10086)
    @ResponseMapping(110)
    public Map<String,Object> hello(Session session, String id, Integer[] value, List<String> list) {
        System.out.println(id);
        System.out.println(list);
        System.out.println(value);
        Map<String,Object> map = new HashMap<>();
        map.put("msg",123);
        return map;
    }

    @RequestMapping(100)
    public void time() throws IOException {
    }
}
