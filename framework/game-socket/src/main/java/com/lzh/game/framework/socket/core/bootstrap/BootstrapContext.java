package com.lzh.game.framework.socket.core.bootstrap;

import com.lzh.game.framework.socket.core.SocketProperties;
import com.lzh.game.framework.socket.core.invoke.convert.DefaultInvokeMethodArgumentValues;
import com.lzh.game.framework.socket.core.invoke.convert.InvokeMethodArgumentValues;
import com.lzh.game.framework.socket.core.invoke.support.DefaultActionInvokeSupport;
import com.lzh.game.framework.socket.core.invoke.support.InvokeSupport;
import com.lzh.game.framework.socket.core.process.context.DefaultProcessorPipeline;
import com.lzh.game.framework.socket.core.process.context.ProcessorPipeline;
import com.lzh.game.framework.socket.core.protocol.message.DefaultMessageManager;
import com.lzh.game.framework.socket.core.protocol.message.MessageManager;
import com.lzh.game.framework.socket.core.session.impl.DefaultSessionManage;
import com.lzh.game.framework.socket.core.session.Session;
import com.lzh.game.framework.socket.core.session.SessionManage;
import lombok.Getter;

/**
 * @author zehong.l
 * @since 2024-07-30 10:16
 **/
@Getter
public class BootstrapContext<T extends SocketProperties> {

    private SessionManage<Session> sessionManage;

    private MessageManager messageManager;

    private InvokeSupport invokeSupport;

    private InvokeMethodArgumentValues values;

    private ProcessorPipeline pipeline;

    private T properties;

    public static <T extends SocketProperties> BootstrapContext<T> of(T properties) {
        return of(properties, new DefaultSessionManage<>()
                , new DefaultMessageManager(properties), new DefaultActionInvokeSupport(), new DefaultInvokeMethodArgumentValues());
    }

    public static <T extends SocketProperties> BootstrapContext<T> of(T properties, SessionManage<Session> sessionManage
            , MessageManager messageManager, InvokeSupport invokeSupport, InvokeMethodArgumentValues values) {
        var context = new BootstrapContext<T>();
        context.properties = properties;
        context.sessionManage = sessionManage;
        context.messageManager = messageManager;
        context.invokeSupport = invokeSupport;
        context.values = values;
        context.pipeline = new DefaultProcessorPipeline();
        return context;
    }

    public void shutdown() {
        sessionManage.shutdown();
    }
}
