package com.lzh.game.framework.socket.core.bootstrap;

import com.lzh.game.framework.socket.core.invoke.InvokeBeanHelper;
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
import lombok.Setter;

/**
 * @author zehong.l
 * @since 2024-07-30 10:16
 **/
@Getter
@Setter
public class BootstrapContext {

    private SessionManage<Session> sessionManage;

    private MessageManager messageManager;

    private InvokeSupport invokeSupport;

    private InvokeBeanHelper beanHelper;

    private ProcessorPipeline pipeline;

    public static BootstrapContext of() {
        var context = new BootstrapContext();
        context.sessionManage = GameSessionManage.of();
        context.messageManager = new DefaultMessageManager();
        context.invokeSupport = new DefaultActionInvokeSupport();
        context.beanHelper = new InvokeBeanHelper(context);
        context.pipeline = new DefaultProcessorPipeline();
        return context;
    }

    public static BootstrapContext of(SessionManage<Session> sessionManage) {
        return of(sessionManage, new DefaultMessageManager(), new DefaultActionInvokeSupport());
    }

    public static BootstrapContext of(SessionManage<Session> sessionManage, MessageManager messageManager, InvokeSupport invokeSupport) {
        var context = new BootstrapContext();
        context.sessionManage = sessionManage;
        context.messageManager = messageManager;
        context.invokeSupport = invokeSupport;
        context.beanHelper = new InvokeBeanHelper(context);
        context.pipeline = new DefaultProcessorPipeline();
        return context;
    }
}
