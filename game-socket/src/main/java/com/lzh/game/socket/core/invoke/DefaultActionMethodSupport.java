package com.lzh.game.socket.core.invoke;

import com.lzh.game.common.bean.EnhanceHandlerMethod;
import com.lzh.game.common.bean.HandlerMethod;
import com.lzh.game.socket.annotation.Action;
import com.lzh.game.socket.annotation.RequestMapping;
import com.lzh.game.socket.annotation.ResponseMapping;
import com.lzh.game.socket.core.invoke.cmd.CmdMappingManage;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.ReflectionUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;
import static com.lzh.game.socket.core.invoke.cmd.CmdMappingManage.*;

@Slf4j
public class DefaultActionMethodSupport implements ActionMethodSupport<EnhanceHandlerMethod>, ApplicationContextAware {

    private final Map<Integer, EnhanceHandlerMethod> protocolMap = new HashMap<>();
    private final Map<Integer, Integer> requestAndResponseMapping = new HashMap<>();

    private CmdMappingManage cmdMappingManage;
    private InnerParamDataBindHandler innerParamDataBindHandler;


    @Override
    public EnhanceHandlerMethod getActionHandler(int cmd) {
        return protocolMap.get(cmd);
    }

    @Override
    public boolean containMapping(int cmd) {
        return protocolMap.containsKey(cmd);
    }

    @Override
    public void registerCmd(int cmd, EnhanceHandlerMethod methodMapping) {

        if (protocolMap.containsKey(cmd)) {
            throw new IllegalArgumentException("Repeated registration " + cmd + " proto.");
        }
        protocolMap.put(cmd, methodMapping);

        if (log.isInfoEnabled()) {
            log.info("Request [{}] into {}.{}", cmd, methodMapping.getBean().getClass().getName(), methodMapping.getMethod().getName());
        }
    }

    @Override
    public void registerRequestRelation(int requestCmd, int responseCmd) {
        if (requestAndResponseMapping.containsKey(requestCmd)) {
            throw new IllegalArgumentException("Repeated request registration: " + requestCmd);
        }
        this.requestAndResponseMapping.put(requestCmd, responseCmd);
    }

    @Override
    public int getRequestRelation(int requestCmd) {
        return this.requestAndResponseMapping.getOrDefault(requestCmd, 0);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        parseActionHandler(applicationContext);
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

                        registerCmd(methodMapping.getValue(), method);
                        if (methodMapping.getResponse() != 0) {
                            registerRequestRelation(methodMapping.getValue(), methodMapping.getResponse());
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

    public void setCmdMappingManage(CmdMappingManage cmdMappingManage) {
        this.cmdMappingManage = cmdMappingManage;
    }

    private RequestMethodMapping buildMapping(HandlerMethod method, int request) {
        RequestMethodMapping methodMapping = new RequestMethodMapping();
        methodMapping.setValue(request);
        methodMapping.setHandlerMethod(method);
        return methodMapping;
    }

    private RequestMethodMapping parseTargetMethod(RequestMapping mapping, HandlerMethod method) {
        int cmd = mapping.value();
        checkCmdExist(cmd);
        CmdModel model = cmdMappingManage.getCmd(cmd);
        checkModel(model, CmdType.REQUEST);

        long protoParam = Stream.of(method.getMethodParameters())
                .filter(e -> !innerParamDataBindHandler.isInnerParam(e))
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
            checkCmdExist(response);
            CmdModel responseModel = cmdMappingManage.getCmd(response);
            checkModel(responseModel, CmdType.RESPONSE);
            methodMapping.setResponse(responseModel.getCmd());
        }

        return methodMapping;
    }

    private void checkModel(CmdModel cmdModel, CmdType cmdType) {
        if (!cmdType.equals(cmdModel.getCmdType())) {
            throw new IllegalArgumentException(cmdModel.getCmd() + "protocol mapping type is error. Protocol class' target is " + cmdType.name() +" but in fact value is " + cmdModel.getCmdType());
        }
    }

    public void setInnerParamDataBindHandler(InnerParamDataBindHandler innerParamDataBindHandler) {
        this.innerParamDataBindHandler = innerParamDataBindHandler;
    }

    private void checkCmdExist(int cmd) {
        if (!cmdMappingManage.contain(cmd)) {
            throw new IllegalArgumentException("Not register [" + cmd + "] protocol");
        }
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

        public boolean hasResponse() {
            return this.getResponse() != 0;
        }

    }
}
