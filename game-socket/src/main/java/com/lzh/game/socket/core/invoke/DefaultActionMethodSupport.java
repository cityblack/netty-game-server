package com.lzh.game.socket.core.invoke;

import com.lzh.game.common.bean.HandlerMethod;
import com.lzh.game.socket.annotation.Action;
import com.lzh.game.socket.annotation.RequestMapping;
import com.lzh.game.socket.annotation.ResponseMapping;
import com.lzh.game.socket.core.invoke.cmd.CmdMappingManage;
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
public class DefaultActionMethodSupport implements ActionMethodSupport, ApplicationContextAware {

    private final Map<Integer, RequestMethodMapping> protocolMap = new HashMap<>();
    private CmdMappingManage cmdMappingManage;
    private InnerParamDataBindHandler innerParamDataBindHandler;

    @Override
    public RequestMethodMapping getActionHandler(int cmd) {
        return protocolMap.get(cmd);
    }

    @Override
    public boolean containMapping(int cmd) {
        return protocolMap.containsKey(cmd);
    }

    @Override
    public void registerCmd(int cmd, RequestMethodMapping methodMapping) {

        if (protocolMap.containsKey(methodMapping.getValue())) {
            throw new IllegalArgumentException("Repeated registration " + methodMapping.getValue() + " proto.");
        }

        protocolMap.put(methodMapping.getValue(), methodMapping);

        if (log.isInfoEnabled()) {
            log.info("Request [{}] into {}.{}", methodMapping.getValue(), methodMapping.getHandlerMethod().getBean().getClass().getName(), methodMapping.getHandlerMethod().getMethod().getName());
            if (methodMapping.getResponse() != 0) {
                log.info("[{}] protocol response [{}]", methodMapping.getValue(), methodMapping.getResponse());
            }
        }
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
                        HandlerMethod method = new HandlerMethod(v, e);
                        RequestMapping mapping = method.getMethodAnnotation(RequestMapping.class);

                        RequestMethodMapping methodMapping = parseTargetMethod(mapping, method);

                        registerCmd(methodMapping.getValue(), methodMapping);
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
}
