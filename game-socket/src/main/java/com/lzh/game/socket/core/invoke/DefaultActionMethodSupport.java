package com.lzh.game.socket.core.invoke;

import com.lzh.game.common.bean.EnhanceHandlerMethod;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class DefaultActionMethodSupport implements RequestActionSupport<EnhanceHandlerMethod> {

    private final Map<Integer, EnhanceHandlerMethod> protocolMap = new HashMap<>();
    private final Map<Integer, Integer> requestAndResponseMapping = new HashMap<>();

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

        if (log.isInfoEnabled()) {
            log.info("Request [{}] into {}.{}", cmd, methodMapping.getBean().getClass().getName(), methodMapping.getMethod().getName());
        }
    }

    @Override
    public int getRequestRelation(int requestCmd) {
        return this.requestAndResponseMapping.getOrDefault(requestCmd, 0);
    }

    @Override
    public void register(int requestCmd, EnhanceHandlerMethod method, int responseCmd) {
        if (requestAndResponseMapping.containsKey(requestCmd)) {
            throw new IllegalArgumentException("Repeated request registration: " + requestCmd);
        }
        this.requestAndResponseMapping.put(requestCmd, responseCmd);
        this.protocolMap.put(requestCmd, method);
    }

}
