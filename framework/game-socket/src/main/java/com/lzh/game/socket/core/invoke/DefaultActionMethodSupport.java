package com.lzh.game.socket.core.invoke;

import com.lzh.game.common.bean.EnhanceHandlerMethod;
import com.lzh.game.socket.ActionMethodSupport;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class DefaultActionMethodSupport implements ActionMethodSupport<EnhanceHandlerMethod> {

    private final Map<Integer, EnhanceHandlerMethod> protocolMap = new HashMap<>();

    @Override
    public EnhanceHandlerMethod getActionHandler(int cmd) {
        return protocolMap.get(cmd);
    }

    @Override
    public boolean containMapping(int cmd) {
        return protocolMap.containsKey(cmd);
    }

    @Override
    public void register(int cmd, EnhanceHandlerMethod methodMapping) {

        if (protocolMap.containsKey(cmd)) {
            throw new IllegalArgumentException("Repeated registration " + cmd + " proto.");
        }
        protocolMap.put(cmd, methodMapping);

        if (log.isDebugEnabled()) {
            log.debug("Request [{}] into {}.{}", cmd, methodMapping.getBean().getClass().getName(), methodMapping.getMethod().getName());
        }
    }

    @Override
    public List<EnhanceHandlerMethod> getAllActionHandler() {
        return new ArrayList<>(protocolMap.values());
    }
}
