package com.lzh.game.framework.logs;

import com.lzh.game.framework.logs.desc.LogDescHandler;
import com.lzh.game.framework.logs.invoke.LogInvoke;
import com.lzh.game.framework.logs.invoke.LogMethodHandler;
import javassist.util.proxy.Proxy;
import javassist.util.proxy.ProxyFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.FactoryBean;

/**
 * @author zehong.l
 * @since 2024-07-16 18:19
 **/
public class JavassistLogFactory<T> implements FactoryBean<T>, BeanFactoryAware {

    private final Class<T> interfaceType;

    private BeanFactory beanFactory;

    public JavassistLogFactory(Class<T> interfaceType) {
        this.interfaceType = interfaceType;
    }

    @Override
    public T getObject() throws Exception {
        return createBean();
    }

    @Override
    public Class<?> getObjectType() {
        return interfaceType;
    }

    private T createBean() throws Exception {
        ProxyFactory factory = new ProxyFactory();
        factory.setInterfaces(new Class[]{interfaceType});
        Class<?> clz = factory.createClass();

        var bean = clz.getConstructor().newInstance();
        ((Proxy) bean).setHandler(new LogMethodHandler(beanFactory.getBean(LogInvoke.class)
                , interfaceType, beanFactory.getBean(LogDescHandler.class)));
        return (T) bean;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }
}
