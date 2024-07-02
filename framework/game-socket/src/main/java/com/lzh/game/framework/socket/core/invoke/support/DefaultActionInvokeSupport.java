package com.lzh.game.framework.socket.core.invoke.support;

import com.lzh.game.framework.utils.bean.EnhanceMethodInvoke;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class DefaultActionInvokeSupport implements InvokeSupport {

    private final Map<Short, EnhanceMethodInvoke> protocolMap = new HashMap<>();

    @Override
    public EnhanceMethodInvoke getActionHandler(short msgId) {
        return protocolMap.get(msgId);
    }

    @Override
    public boolean containMapping(short msgId) {
        return protocolMap.containsKey(msgId);
    }

    @Override
    public void register(short msgId, EnhanceMethodInvoke invoke) {

        if (protocolMap.containsKey(msgId)) {
            throw new IllegalArgumentException("Repeated registration " + msgId + " proto.");
        }
        protocolMap.put(msgId, invoke);

    }

    @Override
    public List<EnhanceMethodInvoke> getAllActionHandler() {
        return new ArrayList<>(protocolMap.values());
    }
}
