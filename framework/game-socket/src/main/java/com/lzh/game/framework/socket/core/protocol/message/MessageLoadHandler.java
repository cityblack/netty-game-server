package com.lzh.game.framework.socket.core.protocol.message;

import org.reflections.Reflections;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * @author zehong.l
 * @since 2024-08-22 10:44
 **/
public class MessageLoadHandler {

    public List<MessageDefine> load(String[] packageNames) {
        var list = new LinkedList<MessageDefine>();
        loadDefined(list, packageNames);
        return list;
    }

    private void loadDefined(List<MessageDefine> list, String[] packageNames) {
        if (packageNames.length == 0) {
            return;
        }
        Reflections reflections = null;
        for (String packageName : packageNames) {
            var newReflections = new Reflections(packageName);
            reflections = Objects.isNull(reflections) ?
                    newReflections : reflections.merge(newReflections);
        }

        for (Class<?> clz : reflections.getTypesAnnotatedWith(Protocol.class)) {
            list.add(MessageManager.classToDefine(clz));
        }
    }
}
