package com.lzh.game.start;


import com.google.common.collect.Sets;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import org.junit.Test;
import org.springframework.cglib.beans.BeanGenerator;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.stream.Collectors;


@Slf4j
public class AppTest {

    private static final String[] arr = {"a","b","v","d"};
    private Random random = new Random(arr.length);

    @Test
    public void ts() {
        LocalDate date = LocalDate.parse("2019-10-22");
        log.info("{}", date.plusDays(1));
        LocalDate date1 = LocalDate.now();
        System.out.println(date1.plusMonths(1)
                .format(DateTimeFormatter.BASIC_ISO_DATE));
        ;
    }

    public byte s(byte s) {
        return s |= 1;
    }
    @Data
    static class Inner {
        private int id;
    }
    class TestMethod implements MethodInterceptor {

        @Override
        public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
//            System.out.println(o);
            System.out.println(method.getName());
            //System.out.println(methodProxy.getSuperName());
            return methodProxy.invokeSuper(o, objects);
        }
    }



    @Data
    class RandomData {
        private String time;
        private int value;
        private Inner inner;
    }

    @Data
    class Record {
        private String begin;
        private String end;
        private int max;
        private int min;
    }

    private  <T> T newProxy(Class<T> interfaceType, Object handler) {

        if (handler instanceof InvocationHandler) {
            Object obj = Proxy.newProxyInstance(interfaceType.getClassLoader(), new Class[]{ interfaceType }, (InvocationHandler)handler);
            return interfaceType.cast(obj);
        }
        return null;
    }

    @Test
    public void abstractData() throws NoSuchFieldException {
        BeanGenerator generator = new BeanGenerator();
        generator.setSuperclass(User.class);
        generator.addProperty("$shareProxy", Integer.class);
        User user = (User) generator.create();
        Field field = user.getClass().getField("$shareProxy");
        ReflectionUtils.makeAccessible(field);
        System.out.println(ReflectionUtils.getField(field, user));
    }

    @Test
    public void common() {
        SortedSet<Integer> set = new ConcurrentSkipListSet<>();
        set.add(5);
        set.add(3);
        log.info("{}", set);
    }
}
