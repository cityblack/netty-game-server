package com.lzh.game.framework.logs;

import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.Proxy;
import javassist.util.proxy.ProxyFactory;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author zehong.l
 * @date 2023-05-31 15:42
 **/
//@Slf4j
//@SpringBootTest(classes = {com.lzh.game.framework.logs.AppTest.class})
public class LogHandlerTest {

    @Test
    public void log() throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
//        LogHandler.getLog(LogTest.class).logTest(10086, "hello world");
        ProxyFactory factory = new ProxyFactory();
        factory.setInterfaces(new Class[]{LogTest.class});
        Class<?> proxy = factory.createClass();
        LogTest test = (LogTest) proxy.getDeclaredConstructor().newInstance();
        ((Proxy)test).setHandler(new MethodHandler() {
            @Override
            public Object invoke(Object self, Method thisMethod, Method proceed, Object[] args) throws Throwable {
                System.out.println("hello world..");
                System.out.println(thisMethod.getName());
                return null;
            }
        });
        test.logTest(1, "");
    }

}
