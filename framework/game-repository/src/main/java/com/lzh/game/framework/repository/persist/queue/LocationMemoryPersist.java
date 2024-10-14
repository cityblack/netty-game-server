package com.lzh.game.framework.repository.persist.queue;

import com.lzh.game.framework.repository.persist.Element;
import com.lzh.game.framework.repository.persist.EventType;
import com.lzh.game.framework.repository.persist.Persist;
import com.lzh.game.framework.repository.persist.consumer.PersistConsumer;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class LocationMemoryPersist implements Persist {

    private BlockingQueue<Element> queue;

    private AtomicBoolean enable;

    private PersistConsumer persistConsumer;

    private long consumeInterval;

    private final Map<Element, Boolean> deleterCache;

    private final ScheduledExecutorService consumeTimer;

    private Class<?> type;

    public LocationMemoryPersist(Class<?> type, PersistConsumer consumer, long consumeInterval) {
        this.persistConsumer = consumer;
        this.type = type;
        this.queue = new LinkedBlockingQueue<>();
        this.enable = new AtomicBoolean(true);
        this.consumeTimer = Executors.newScheduledThreadPool(1);
        this.consumeInterval = consumeInterval;
        this.deleterCache = new ConcurrentHashMap<>();
        this.startTimer();
    }

    @Override
    public void put(Element element) {
        if (Objects.isNull(element)) {
            return;
        }
        if (enable.get()) {
            if (element.getEventType() == EventType.DELETER) {
                deleterCache.put(element, Boolean.TRUE);
            } else {
                if (deleterCache.containsKey(element)) {
                    log.error("[{}-{}] is already deleted. EventType: {}", element.getClazz(), element.getId(), element.getEventType());
                    return;
                }
            }
            queue.offer(element);
        } else {
            log.error("Persist queue is not running. not allow add new element.");
        }
    }

    @Override
    public void shutDown() {
        log.debug("Close Persist entity queue. {} scheduler size: {}", type, this.queue.size());
        int size = queue.size();
        this.consumeQueue(queue);
        this.consumeTimer.shutdown();
        if (size > 0) {
            try {
                Thread.sleep(5_000);
            } catch (InterruptedException e) {
                log.error("", e);
            }
            log.debug("Wait 5 second to persist {} element.", type.getSimpleName());
        }
        var list = this.consumeTimer.shutdownNow();
        for (Runnable runnable : list) {
            runnable.run();
        }
    }

    protected void consumer(Element element) {
        if (log.isDebugEnabled()) {
            log.debug("Consumer element from persist queue. element:{}", element);
        }
        try {
            persistConsumer.onConsumer(element);
        } catch (Exception e) {
            log.error("Consume element [{}-{}] error", element.getClazz(), element.getId(), e);
        }
    }

    protected void startTimer() {

        if (!this.enable.get()) {
            throw new RuntimeException("Persist queue not start.");
        }
        log.info("Open Persist timer.");
        consumeTimer.scheduleAtFixedRate(() -> consumeQueue(queue), consumeInterval
                , consumeInterval, TimeUnit.MILLISECONDS);
    }

    private void consumeQueue(BlockingQueue<Element> queue) {
        var arr = queue.toArray(Element[]::new);
        var merge = mergeElement(arr);
        for (Element element : merge) {
            consumer(element);
        }
    }

    private Collection<Element> mergeElement(Element[] arr) {
        List<Element> result = new ArrayList<>();
        Map<Element, Integer> cache = new HashMap<>();
        for (Element element : arr) {
            if (Objects.isNull(element)) {
                continue;
            }
            if (deleterCache.containsKey(element)) {
                continue;
            }
            int index = cache.getOrDefault(element, -1);
            if (index >= 0) {
                result.set(index, element);
            } else {
                cache.put(element, result.size());
                result.add(element);
            }
        }
        return result;
    }

    public void setQueue(BlockingQueue<Element> queue) {
        this.queue = queue;
    }

    public void setEnable(AtomicBoolean enable) {
        this.enable = enable;
    }

    public void setPersistConsumer(PersistConsumer persistConsumer) {
        this.persistConsumer = persistConsumer;
    }

    public void setConsumeInterval(long consumeInterval) {
        this.consumeInterval = consumeInterval;
    }

}
