package com.lzh.socket.starter;

import com.lzh.game.socket.core.protocol.Request;
import com.lzh.game.socket.core.protocol.Response;
import com.lzh.game.socket.core.invoke.support.ErrorHandler;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Map;
import java.util.Objects;

public class SpringExceptionHandler implements ErrorHandler, ApplicationContextAware {

    private DefaultErrorHandler errorHandler;

    @Override
    public boolean resolveException(Exception ex, Request request, Response response) {
        if (Objects.isNull(errorHandler)) {
            return false;
        }
        return errorHandler.resolveException(ex, request, response);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        loadAdvice(applicationContext);
    }

    private void loadAdvice(ApplicationContext context) {
        Map<String, Object> map = context.getBeansWithAnnotation(ActionAdvice.class);
        if (map.size() > 1) {
            throw new IllegalArgumentException("@ControllerAdvice has multiple instance. " + map);
        }
        if (!map.isEmpty()) {
            Map.Entry<String, Object> entry = map.entrySet().iterator().next();
            Object bean = entry.getValue();
            Class<?> clazz = bean.getClass();
            errorHandler = new DefaultErrorHandler(new ExceptionHandlerMethodResolver(clazz), bean);
        }
    }
}
