package com.lzh.game.framework.logs.invoke.serializer;

import com.alibaba.fastjson2.JSON;

import java.util.Map;

/**
 * @author zehong.l
 * @since 2024-07-16 14:38
 **/
public class FastJson2ContentSerializer implements LogContentSerializer {
    @Override
    public String serializer(Map<String, Object> content) {
        return JSON.toJSONString(content);
    }
}
