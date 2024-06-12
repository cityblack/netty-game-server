package com.lzh.game.socket.core.invoke;

import com.lzh.game.common.bean.EnhanceHandlerMethod;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class DefaultActionInvokeSupport implements InvokeSupport {

    private final Map<Integer, MethodInvoke> protocolMap = new HashMap<>();

    @Override
    public MethodInvoke getActionHandler(int cmd) {
        return protocolMap.get(cmd);
    }

    @Override
    public boolean containMapping(int cmd) {
        return protocolMap.containsKey(cmd);
    }

    @Override
    public void register(int cmd, MethodInvoke invoke) {

        if (protocolMap.containsKey(cmd)) {
            throw new IllegalArgumentException("Repeated registration " + cmd + " proto.");
        }
        protocolMap.put(cmd, invoke);

    }

    @Override
    public List<MethodInvoke> getAllActionHandler() {
        return new ArrayList<>(protocolMap.values());
    }
}
