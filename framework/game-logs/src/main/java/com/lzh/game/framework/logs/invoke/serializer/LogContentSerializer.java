package com.lzh.game.framework.logs.invoke.serializer;

import java.util.Map;

/**
 * @author zehong.l
 * @since 2024-07-10 11:39
 **/
@FunctionalInterface
public interface LogContentSerializer {

    String serializer(Map<String, Object> content);
}
