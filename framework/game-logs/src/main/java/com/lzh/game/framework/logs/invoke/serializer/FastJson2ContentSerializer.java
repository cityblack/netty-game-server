package com.lzh.game.framework.logs.invoke.serializer;

import com.lzh.game.framework.utils.JsonUtils;

import java.util.Map;

/**
 * @author zehong.l
 * @since 2024-07-16 14:38
 **/
public class FastJson2ContentSerializer implements LogContentSerializer {
    @Override
    public String serializer(Map<String, Object> content) {
        return JsonUtils.toJson(content);
    }
}
