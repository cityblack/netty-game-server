package com.lzh.game.start.model.target.handler;

import com.lzh.game.start.model.target.TargetType;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Component
public class TargetHandlerManageImpl implements TargetHandlerManage, ApplicationContextAware {

    private Map<TargetType, AbstractTargetHandler> map = new HashMap<>();

    @Override
    public AbstractTargetHandler handler(TargetType targetType) {
        if (!map.containsKey(targetType)) {
            throw new IllegalArgumentException("找不到对应的AbstractTargetHandler");
        }
        return map.get(targetType);
    }

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        Map<String, AbstractTargetHandler> handlerMap = context.getBeansOfType(AbstractTargetHandler.class);
        parse(handlerMap);
    }

    private void parse(Map<String, AbstractTargetHandler> handlerMap) {
        handlerMap.forEach((k, handler) -> {
            TargetType type = handler.type();
            if (Objects.isNull(type)) {
                throw new IllegalArgumentException("TargetHandler对应的标识为空。 目标模块:" + handler.getClass().getSimpleName());
            }
            if (map.containsKey(type)) {
                throw new IllegalArgumentException("TargetHandler重复注册目标模块标识. handler:" + handler.getClass().getSimpleName());
            }
            map.put(type, handler);
        });
    }
}
