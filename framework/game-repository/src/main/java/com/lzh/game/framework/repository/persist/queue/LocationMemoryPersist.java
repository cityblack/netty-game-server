package com.lzh.game.framework.repository.persist.queue;

import com.lzh.game.framework.repository.persist.Element;
import com.lzh.game.framework.repository.persist.Persist;
import com.lzh.game.framework.repository.persist.consumer.PersistConsumer;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class LocationMemoryPersist implements Persist {

    private BlockingQueue<Element> queue;

    private AtomicBoolean enable;

    private PersistConsumer persistConsumer;

    private long consumeInterval;

    private Map<String, Element> mergeCache = new ConcurrentHashMap<>();

    private ScheduledExecutorService consumeTimer;

    private Class<?> type;

    public LocationMemoryPersist(Class<?> type, PersistConsumer consumer, long consumeInterval) {
        this.persistConsumer = consumer;
        this.type = type;
        this.queue = new LinkedBlockingQueue<>();
        this.enable = new AtomicBoolean(true);
        this.consumeTimer = Executors.newScheduledThreadPool(1);
        this.consumeInterval = consumeInterval;
        this.startTimer();
    }

    @Override
    public void put(Element element) {
        if (Objects.isNull(element)) {
            return;
        }
        if (enable.get()) {
            replaceElement(element);
            queue.offer(element);
        } else {
            log.error("Persist queue is stopping. not allow add new element.");
        }
    }

    @Override
    public void shutDown() {
        log.info("Close Persist entity queue. {} scheduler size: {}", type, this.queue.size());
        this.consumeQueue(queue);
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
            this.mergeCache.remove(getMergeKey(element));
            persistConsumer.onConsumer(element);
        } catch (Exception e) {
            log.error("Consume element [{}-{}] error", element.getClazz(), element.getId());
        }
    }

    private String getMergeKey(Element element) {

        String className = element.getClazz().getSimpleName();
        String typeName = element.getEventType().name();
        String key = element.getId().toString();

        return String.join(":", className, typeName, key);
    }

    private void replaceElement(Element element) {

        String mergeKey = getMergeKey(element);
        Element oldElement = mergeCache.get(mergeKey);

        if (Objects.nonNull(oldElement)) {
            replaceStrategy(oldElement, element);
            mergeCache.put(mergeKey, element);
        }
    }

    private void replaceStrategy(final Element oldElement, Element newElement) {

        newElement.eventBack(new Element.EventTypeBack() {

            @Override
            public void onSave(Element element) {
                queue.remove(oldElement);
            }

            @Override
            public void onUpdate(Element element) {
                queue.remove(oldElement);
            }

            @Override
            public void onDeleter(Element element) {
                queue.remove(oldElement);
            }
        });
    }

    protected void startTimer() {

        if (!this.enable.get()) {
            throw new RuntimeException("Persist queue not start.");
        }
        log.info("Open Persist timer.");
        consumeTimer.scheduleAtFixedRate(() -> {
            if (enable.get()) {
                consumeQueue(queue);
            }
        }, consumeInterval, consumeInterval, TimeUnit.MILLISECONDS);
    }

    private void consumeQueue(BlockingQueue<Element> queue) {

        int size = queue.size();

        for (int i = 0; i < size; i++) {
            Element element = queue.poll();
            if (Objects.nonNull(element)) {
                consumer(element);
            }
        }
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

    public void setMergeCache(Map<String, Element> mergeCache) {
        this.mergeCache = mergeCache;
    }

}
