package com.lzh.game.start.model.game;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * 对象查询工厂
 */
@Component
public class GameObjectManageFactoryImpl implements GameObjectManageFactory, ApplicationContextAware {

    // 映射关系
    private Map<GameObjectType, GameObjectManage> mapping;

    @Override
    public GameObjectManage getVisibleManage(GameObjectType objectType) {
        return mapping.get(objectType);
    }

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        Map<String, GameObjectManage> objectManage = context.getBeansOfType(GameObjectManage.class);
        mapping = objectManage.values().stream().collect(Collectors.toMap(e -> e.type(), e -> e));
    }
}
