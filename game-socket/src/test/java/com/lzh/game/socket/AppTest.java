package com.lzh.game.socket;

import com.lzh.game.common.bean.EnhanceHandlerMethod;
import com.lzh.game.socket.core.session.GameSessionManage;
import com.lzh.game.socket.core.session.Session;
import com.lzh.game.socket.core.session.SessionFactory;
import com.lzh.game.socket.core.session.SessionManage;
import com.lzh.game.socket.core.MessageHandlerImpl;
import com.lzh.game.socket.core.RequestProcessPool;
import com.lzh.game.socket.core.RequestHandler;
import com.lzh.game.socket.core.ServerExchange;
import com.lzh.game.socket.core.bootstrap.TcpCommonServer;
import com.lzh.game.socket.core.filter.FilterHandler;
import com.lzh.game.socket.core.invoke.*;
import com.lzh.game.socket.core.session.ServerGameSession;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.ArrayList;

public class AppTest {

    public class ServerDemo {

        public void hello(String hello) {
            System.out.println(hello);
        }
    }

    @Test
    public void startServer() throws NoSuchMethodException {
        GameServerSocketProperties properties = new GameServerSocketProperties();
        properties.setPort(8081);
        properties.setOpenGm(true);
        SessionManage<Session> manage = createSessionManage();
        MessageHandler handler = createMessageHandler();
        TcpCommonServer server = new TcpCommonServer(properties, manage, handler);
        server.start();
    }

    private SessionManage<Session> createSessionManage() {
        SessionMemoryCacheManage<String, Session> cacheManage = new GameSessionMemoryCacheManage<>();
        SessionFactory<Session> factory = ServerGameSession::of;
        SessionManage<Session> sessionManage = new GameSessionManage<>(cacheManage, factory);
        return sessionManage;
    }

    private MessageHandler createMessageHandler() throws NoSuchMethodException {
        RequestActionSupport<EnhanceHandlerMethod> support = new DefaultActionMethodSupport();
        ServerDemo demo = new ServerDemo();
        Method method = demo.getClass().getMethod("hello");
        EnhanceHandlerMethod enhanceMethod = new EnhanceHandlerMethod(demo, method);
        support.register(10086, enhanceMethod);

        InvokeMethodArgumentValues values = new InvokeMethodArgumentValuesImpl();
        RequestHandler handler = new ActionRequestHandler(support, values);
        RequestHandler filter = new FilterHandler(new ArrayList<>(), handler);

        MessageHandler messageHandler = new MessageHandlerImpl(filter, pool());

        return messageHandler;
    }

    private RequestProcessPool pool() {

        return new RequestProcessPool() {
            @Override
            public void submit(ServerExchange exchange, Runnable runnable) {
                runnable.run();
            }

            @Override
            public void submit(Session session, Runnable runnable) {
                runnable.run();
            }
        };
    }


}
