package com.lzh.game.framework.socket.core.protocol.message;

import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * @author zehong.l
 * @since 2024-08-22 10:44
 **/
@Slf4j
public class MessageLoadHandler {

    public List<Class<?>> load(String[] packageNames) {
        log.info("Loading message. scan path: {}", packageNames);
        if (Objects.isNull(packageNames) || packageNames.length == 0) {
            return Collections.emptyList();
        }
        var list = new LinkedList<Class<?>>();
        loadDefined(list, packageNames);
        return list;
    }

    private void loadDefined(List<Class<?>> list, String[] packageNames) {
        if (packageNames.length == 0) {
            return;
        }
        Reflections reflections = null;
        for (String packageName : packageNames) {
            var newReflections = new Reflections(packageName);
            reflections = Objects.isNull(reflections) ?
                    newReflections : reflections.merge(newReflections);
        }
        var pros = reflections.getTypesAnnotatedWith(Protocol.class);
        for (Class<?> clz : pros) {
            list.add(clz);
        }
    }
}
