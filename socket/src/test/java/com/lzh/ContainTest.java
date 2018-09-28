package com.lzh;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import reactor.core.publisher.Flux;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
public class ContainTest {
    private List<Integer> list = new ArrayList<>();
    private static AtomicLong num = new AtomicLong(0L);
    private static long single = 0L;
    @Test
    public void contain() throws ExecutionException, InterruptedException {
        long time = System.currentTimeMillis();
        increase();
        System.out.println(System.currentTimeMillis() - time);
        System.out.println(single);
    }
    private static void increase() {
        int count = 500000000;
        for (int i = 0; i < count; i++) {
            //num.getAndAdd(i);
            single += i;
        }
    }
    class Student implements Observer {
        private int age;
        private String name;
        public Student(String name, int age) {
            this.name = name;
            this.age = age;
        }
        @Override
        public void update(Observable o, Object arg) {
            System.out.println(o);
        }
    }

    class Teacher extends Observable {

        private String info;

        public void issueWork(String info) {
            this.info = info;
            System.out.println("布置的作业？");
            setChanged();
            notifyObservers();
        }
    }

    @Test
    public void test() {
        Subscriber<Integer> subscriber = new Subscriber<Integer>() {

            @Override
            public void onSubscribe(Subscription subscription) {
                // 保存订阅关系, 需要用它来给发布者响应
                this.subscription = subscription;

                // 请求一个数据
                this.subscription.request(1);
            }

            @Override
            public void onNext(Integer integer) {
                // 接受到一个数据, 处理
                System.out.println("接受到数据: " + integer);

                try {
                    TimeUnit.SECONDS.sleep(3);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                // 处理完调用request再请求一个数据
                this.subscription.request(1);
            }

            @Override
            public void onError(Throwable throwable) {
                // 出现了异常(例如处理数据的时候产生了异常)
                throwable.printStackTrace();

                // 我们可以告诉发布者, 后面不接受数据了
                this.subscription.cancel();
            }

            @Override
            public void onComplete() {
                System.out.println("处理完了!");
            }

            private Subscription subscription;

        };
        // 这里就是jdk8的stream
        Flux.fromArray(new String[]{"1","2","3"}).map(Integer::parseInt)
                // 最终操作
                // 这里就是jdk9的reactive stream
                .subscribe(subscriber);
    }
}
