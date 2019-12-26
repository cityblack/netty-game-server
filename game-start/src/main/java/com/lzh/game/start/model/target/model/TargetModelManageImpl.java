package com.lzh.game.start.model.target.model;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Stream;

@Component
public class TargetModelManageImpl implements TargetModelManage, ApplicationContextAware {

    private Map<TargetModelSign, TargetModelStrategy> mapping = new HashMap<>();

    @Override
    public TargetModelStrategy getTargetModel(TargetModelSign sign) {
        if (!mapping.containsKey(sign)) {
            throw new IllegalArgumentException("并未注册" + sign + "标识对应的handler");
        }
        return mapping.get(sign);
    }

    @Override
    public void foreach(Consumer<TargetModelStrategy> consumer) {
        mapping.values().stream().forEach(consumer);
    }

    @Override
    public Stream<TargetModelStrategy> stream() {
        return mapping.values().stream();
    }

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        Map<String, TargetModelStrategy> beans = context.getBeansOfType(TargetModelStrategy.class);
        beans.forEach((k, targetModelStrategy) -> {
            TargetModelSign sign = targetModelStrategy.modelSign();
            if (Objects.isNull(sign)) {
                throw new IllegalArgumentException("目标模块对应的标识为空。 目标模块:" + targetModelStrategy.getClass().getSimpleName());
            }
            if (mapping.containsKey(sign)) {
                throw new IllegalArgumentException("重复注册目标模块标识. TargetModelSign:" + sign + "");
            }
            mapping.put(sign, targetModelStrategy);
        });
    }
}
