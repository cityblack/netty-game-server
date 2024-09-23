package com.lzh.game.framework.socket.core.bootstrap;

import com.lzh.game.framework.socket.core.GameSocketProperties;
import com.lzh.game.framework.socket.core.invoke.support.DefaultActionInvokeSupport;
import com.lzh.game.framework.socket.core.invoke.support.InvokeSupport;
import com.lzh.game.framework.socket.core.process.context.DefaultProcessorPipeline;
import com.lzh.game.framework.socket.core.process.context.ProcessorPipeline;
import com.lzh.game.framework.socket.core.protocol.message.DefaultMessageManager;
import com.lzh.game.framework.socket.core.protocol.message.MessageManager;
import com.lzh.game.framework.socket.core.session.GameSessionManage;
import com.lzh.game.framework.socket.core.session.Session;
import com.lzh.game.framework.socket.core.session.SessionManage;
import lombok.Getter;

/**
 * @author zehong.l
 * @since 2024-07-30 10:16
 **/
@Getter
public class BootstrapContext<T extends GameSocketProperties> {

    private SessionManage<Session> sessionManage;

    private MessageManager messageManager;

    private InvokeSupport invokeSupport;

    private ProcessorPipeline pipeline;

    private T properties;

    public static <T extends GameSocketProperties> BootstrapContext<T> of(T properties) {
        var context = new BootstrapContext<T>();
        context.properties = properties;
        context.sessionManage = GameSessionManage.of();
        context.messageManager = new DefaultMessageManager(properties);
        context.invokeSupport = new DefaultActionInvokeSupport();
        context.pipeline = new DefaultProcessorPipeline();
        return context;
    }


    public static <T extends GameSocketProperties> BootstrapContext<T> of(T properties, SessionManage<Session> sessionManage, MessageManager messageManager, InvokeSupport invokeSupport) {
        var context = new BootstrapContext<T>();
        context.properties = properties;
        context.sessionManage = sessionManage;
        context.messageManager = messageManager;
        context.invokeSupport = invokeSupport;
        context.pipeline = new DefaultProcessorPipeline();
        return context;
    }
}