package com.lzh.game.framework.logs;

import com.lzh.game.framework.logs.anno.LogFacade;
import com.lzh.game.framework.logs.desc.LogDescHandler;
import com.lzh.game.framework.logs.invoke.LogInvoke;
import com.lzh.game.framework.logs.invoke.LogMethodHandler;
import com.lzh.game.framework.utils.ClassScannerUtils;
import javassist.util.proxy.Proxy;
import javassist.util.proxy.ProxyFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author zehong.l
 * @since 2023-05-31 15:42
 **/
@Slf4j
@Component
public class LogHandler implements ApplicationContextAware {

    private static final Map<Class<?>, Object> INVOKE_BEAN = new HashMap<>();

    private LogInvoke logInvoke;

    private LogDescHandler descHandler;

    private ApplicationContext context;

    public void init(String[] scanPath) {
        log.info("{}. Loading log config..", String.join(",", scanPath));
        long startTime = System.currentTimeMillis();
        var list = ClassScannerUtils.scanPackage(scanPath, e -> e.isAnnotationPresent(LogFacade.class));
        for (Class<?> clazz : list) {
            registerLog(clazz);
        }
        log.info("Loaded log config. time:{}", System.currentTimeMillis() - startTime);
    }

    public static <T> T getLog(Class<T> clazz) {
        Object invoke = INVOKE_BEAN.get(clazz);
        if (Objects.isNull(invoke)) {
            throw new RuntimeException("No register the " + clazz.getSimpleName() + " log.");
        }
        return (T) invoke;
    }

    public void registerLog(Class<?> logClass) {
        if (!logClass.isInterface()) {
            throw new RuntimeException("Log handler is not interface");
        }
        try {
            Object object = buildObject(logClass);
            INVOKE_BEAN.put(logClass, object);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Object buildObject(Class<?> clazz) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        ProxyFactory factory = new ProxyFactory();
        factory.setInterfaces(new Class[]{clazz});
        Class<?> clz = factory.createClass();
        var name = clazz.getName();
        var bean = clz.getConstructor().newInstance();
        ((Proxy) bean).setHandler(new LogMethodHandler(logInvoke, clazz, descHandler));
        return bean;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        getInstance().context = applicationContext;
        getInstance().descHandler = applicationContext.getBean(LogDescHandler.class);
        getInstance().logInvoke = applicationContext.getBean(LogInvoke.class);
        getInstance().init(applicationContext.getBean(LogScanPackages.class)
                .packageNames());
//        registerToSpring(applicationContext);
    }

    private void registerToSpring(ApplicationContext context) {
        if (context instanceof GenericApplicationContext generic) {
            for (Map.Entry<Class<?>, Object> entry : INVOKE_BEAN.entrySet()) {
                var name = entry.getValue().getClass().getName();
                var beanDefinition = new GenericBeanDefinition();
                beanDefinition.setBeanClass(entry.getKey());
                generic.getBeanFactory().registerSingleton(name, beanDefinition);
            }
        }
    }

    public void setLogInvoke(LogInvoke logInvoke) {
        this.logInvoke = logInvoke;
    }

    public void setDescHandler(LogDescHandler descHandler) {
        this.descHandler = descHandler;
    }

    public static LogHandler getInstance() {
        return Instance.INSTANCE;
    }

    public static class Instance {
        public static final LogHandler INSTANCE = new LogHandler();
    }
}
