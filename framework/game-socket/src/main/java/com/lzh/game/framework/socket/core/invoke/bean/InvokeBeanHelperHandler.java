package com.lzh.game.framework.socket.core.invoke.bean;

import com.lzh.game.framework.socket.core.bootstrap.BootstrapContext;
import com.lzh.game.framework.socket.core.protocol.message.MessageDefine;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Stream;

/**
 * @author zehong.l
 * @since 2024-07-02 11:51
 **/
@Slf4j
public class InvokeBeanHelperHandler {

    private final List<InvokeBeanHelper> helpers = new ArrayList<>();

    public void pushBean(Object bean, BootstrapContext<?> context) {
        var stream = parseBean(bean, context);
        stream.forEach(e -> {
            if (Objects.nonNull(e.getProtocol())) {
                for (Class<?> c : e.getProtocol()) {
                    context.getMessageManager().registerMessage(c);
                }
            }
            if (Objects.nonNull(e.getDefines())) {
                for (MessageDefine define : e.getDefines()) {
                    context.getMessageManager().registerMessage(define);
                }
            }
            context.getInvokeSupport().register(e.getMsgId(), e.getHandlerMethod());
        });
    }

    public void addHelper(InvokeBeanHelper helper) {
        this.helpers.add(helper);
    }

    public Stream<InvokeModel> parseBean(Object bean, BootstrapContext<?> context) {
        Class<?> clazz = bean.getClass();
        return Stream.of(clazz.getDeclaredMethods())
                .map(method -> toModel(bean, method, context))
                .filter(Objects::nonNull);
    }

    private InvokeModel toModel(Object bean, Method method, BootstrapContext<?> context) {
        for (InvokeBeanHelper helper : helpers) {
            if (helper.match(bean, method)) {
                return helper.parseBean(bean, method, context);
            }
        }
        throw new IllegalArgumentException("parse bean error. cause of "
                + method.getName() + " not find helper");
    }

    public static InvokeBeanHelperHandler getInstance() {
        return Instance.INSTANCE;
    }

    private InvokeBeanHelperHandler() {
        addHelper(new ReceiveInvokeBeanHelper());
    }

    private static class Instance {
        private static final InvokeBeanHelperHandler INSTANCE = new InvokeBeanHelperHandler();
    }
}
