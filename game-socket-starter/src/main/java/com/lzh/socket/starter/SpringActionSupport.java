package com.lzh.socket.starter;

import com.lzh.game.common.bean.EnhanceHandlerMethod;
import com.lzh.game.common.bean.HandlerMethod;
import com.lzh.game.socket.annotation.RequestMapping;
import com.lzh.game.socket.annotation.ResponseMapping;
import com.lzh.game.socket.core.invoke.DefaultActionMethodSupport;
import com.lzh.game.socket.core.invoke.InnerParamBindHandler;
import com.lzh.game.socket.core.invoke.RequestActionSupport;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.ReflectionUtils;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * @Action parse
 */
@Slf4j
public class SpringActionSupport implements RequestActionSupport<EnhanceHandlerMethod>, ApplicationContextAware {

    private RequestActionSupport<EnhanceHandlerMethod> support;

    private InnerParamBindHandler innerParamBindHandler;

    public SpringActionSupport(RequestActionSupport<EnhanceHandlerMethod> support, InnerParamBindHandler innerParamBindHandler) {
        this.support = support;
        this.innerParamBindHandler = innerParamBindHandler;
    }

    @Override
    public EnhanceHandlerMethod getActionHandler(int cmd) {
        return support.getActionHandler(cmd);
    }

    @Override
    public boolean containMapping(int cmd) {
        return support.containMapping(cmd);
    }

    @Override
    public void register(int cmd, EnhanceHandlerMethod methodMapping) {
        support.register(cmd, methodMapping);
    }

    @Override
    public int getRequestRelation(int requestCmd) {
        return support.getRequestRelation(requestCmd);
    }

    @Override
    public void register(int requestCmd, EnhanceHandlerMethod method, int responseCmd) {
        support.register(requestCmd, method, responseCmd);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.parseActionHandler(applicationContext);
    }

    private void parseActionHandler(ApplicationContext applicationContext) {
        Map<String, Object> beanMap = applicationContext.getBeansWithAnnotation(Action.class);
        if (!beanMap.isEmpty()) {
            synchronized (DefaultActionMethodSupport.class) {
                beanMap.forEach((k,v) -> {
                    Class<?> clazz = v.getClass();
                    if (clazz.isInterface()) {
                        throw new IllegalArgumentException("@Action can't use to interface.");
                    }
                    ReflectionUtils.doWithMethods(clazz, e -> {
                        EnhanceHandlerMethod method = new EnhanceHandlerMethod(v, e);
                        RequestMapping mapping = method.getMethodAnnotation(RequestMapping.class);

                        RequestMethodMapping methodMapping = parseTargetMethod(mapping, method);
                        if (containMapping(methodMapping.value)) {
                            throw new IllegalArgumentException("The cmd not unique." + methodMapping.value);
                        }
                        if (methodMapping.getResponse() != 0) {
                            register(methodMapping.getValue(), method, methodMapping.getResponse());
                        } else {
                            register(methodMapping.getValue(), method);
                        }
                    }, m -> m.isAnnotationPresent(RequestMapping.class));
                });
            }
        } else {
            if (log.isWarnEnabled()) {
                log.warn("No action defined!");
            }
        }
    }

    private RequestMethodMapping parseTargetMethod(RequestMapping mapping, HandlerMethod method) {
        int cmd = mapping.value();

        long protoParam = Stream.of(method.getMethodParameters())
                .filter(e -> !innerParamBindHandler.isInnerParam(e))
                .count();
        // Just allow one mapping proto class
        if (protoParam > 1) {
            throw new IllegalArgumentException(method.getBean().getClass().getSimpleName() + " " + method.getMethod().getName() + " has multi map protocol class.");
        }
        RequestMethodMapping methodMapping = buildMapping(method, cmd);

        if (!method.isVoid()) {
            ResponseMapping responseMapping = method.getMethodAnnotation(ResponseMapping.class);
            if (Objects.isNull(responseMapping)) {
                throw new IllegalArgumentException(cmd + " protocol return value is not null. so that must use @Response to map the method ");
            }
            int response = responseMapping.value();
            methodMapping.setResponse(response);
        }

        return methodMapping;
    }
    private RequestMethodMapping buildMapping(HandlerMethod method, int request) {
        RequestMethodMapping methodMapping = new RequestMethodMapping();
        methodMapping.setValue(request);
        methodMapping.setHandlerMethod(method);
        return methodMapping;
    }

    @Data
    public class RequestMethodMapping {
        /**
         * cmd id
         */
        private int value;

        private int response;

        private HandlerMethod handlerMethod;

        @Override
        public boolean equals(Object obj) {

            if (this == obj) {
                return true;
            }
            if (!(obj instanceof RequestMethodMapping)) {
                return false;
            }
            if (((RequestMethodMapping) obj).getValue() == this.getValue()) {
                return true;
            }
            return false;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(this.value);
        }
    }
}
