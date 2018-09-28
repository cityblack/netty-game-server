package com.lzh.netty.socket.dispatcher.protocol;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class DefaultProtocolManage implements ProtocolManage {

    private Set<ProtocolModel> protocolModels = new HashSet<>();

    @Override
    public void addProtocol(ProtocolModel model) {
        if (null == model) {
            return;
        }
        protocolModels.add(model);
    }

    @Override
    public boolean containProtocol(ProtocolModel model) {
        if (model == null) {
            return false;
        }
        return protocolModels.contains(model);
    }

    @Override
    public Set<ProtocolModel> getAllProtocol() {
        //return a new copy Set
        return protocolModels.stream().collect(Collectors.toSet());
    }

}
