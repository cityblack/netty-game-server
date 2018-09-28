package com.lzh.netty.socket.dispatcher.action;

import com.lzh.netty.socket.annotation.Action;
import com.lzh.netty.socket.dispatcher.protocol.ProtocolManage;
import com.lzh.netty.socket.dispatcher.protocol.ProtocolModel;
import com.lzh.netty.socket.method.InvocableHandlerMethod;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class DefaultActionSupport implements ActionSupport,ApplicationContextAware {

    private final Map<Integer, InvocableHandlerMethod> protocolMap = new HashMap<>();
    private ProtocolManage protocolManage;

    @Override
    public InvocableHandlerMethod getActionHandler(int protocolId) {
        return protocolMap.get(protocolId);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        parseActionHandler(applicationContext);
    }

    private void checkProtocol(ProtocolModel model) {
        if (protocolManage.containProtocol(model)) {
            throw new RuntimeException("[" + model.getValue() + "] protocol is repeat. it must be unique.");
        }
    }

    private void parseActionHandler(ApplicationContext applicationContext) {
        Map<String, Object> beanMap = applicationContext.getBeansWithAnnotation(Action.class);
        if (!beanMap.isEmpty()) {
            synchronized (ActionCenterHandler.class) {
                beanMap.forEach((k,v) -> {
                    Class<?> clazz = v.getClass();

                    if (!clazz.isInterface()) {
                        Stream.of(clazz.getDeclaredMethods())
                                .forEach(m -> {
                                    InvocableHandlerMethod method = new InvocableHandlerMethod(v,m);
                                    ProtocolModel requestProto = method.getRequestProtoModel();
                                    ProtocolModel responseProto = method.getResponseProtoModel();

                                    if (requestProto != null) {
                                        checkProtocol(requestProto);
                                        protocolMap.put(requestProto.getValue(),method);
                                        protocolManage.addProtocol(requestProto);

                                        if (responseProto != null) {
                                            checkProtocol(responseProto);
                                            protocolManage.addProtocol(responseProto);
                                            if (log.isInfoEnabled()) {
                                                log.info("Response [{}] of {}.{}() ", responseProto.getValue()
                                                        , clazz.getName()
                                                        , m.getName());
                                            }
                                        }
                                    }
                                    if (log.isInfoEnabled()) {
                                        log.info("Request [{}] into {}.{}() ", requestProto.getValue()
                                                , clazz.getName()
                                                , m.getName());
                                    }
                                });

                    }
                });
            }
        } else {
            if (log.isWarnEnabled()) {
                log.warn("No action defined!");
            }
        }
    }

    public void setProtocolManage(ProtocolManage protocolManage) {
        this.protocolManage = protocolManage;
    }
}
