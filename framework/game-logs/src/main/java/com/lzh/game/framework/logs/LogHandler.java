package com.lzh.game.framework.logs;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

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

    private static ApplicationContext context;

    // Reduce Spring Bean Search Process
    public static <T> T getLog(Class<T> clazz) {
        Object invoke = INVOKE_BEAN.get(clazz);
        if (Objects.isNull(invoke)) {
            synchronized (INVOKE_BEAN) {
                if (!INVOKE_BEAN.containsKey(clazz)) {
                    var bean = context.getBean(clazz);
                    registerLog(clazz, bean);
                }
                return (T) INVOKE_BEAN.get(clazz);
            }
        }
        return (T) invoke;
    }

    private static void registerLog(Class<?> logClass, Object bean) {
        INVOKE_BEAN.put(logClass, bean);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }


    public static LogHandler getInstance() {
        return Instance.INSTANCE;
    }

    public static class Instance {
        public static final LogHandler INSTANCE = new LogHandler();
    }
}
