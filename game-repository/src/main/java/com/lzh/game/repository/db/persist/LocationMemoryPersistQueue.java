package com.lzh.game.repository.db.persist;

import com.lzh.game.repository.db.Element;
import com.lzh.game.repository.db.Persist;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class LocationMemoryPersistQueue implements Persist, InitializingBean, DisposableBean {

    private BlockingQueue<Element> queue;

    private AtomicBoolean enable;

    private PersistConsumer persistConsumer;

    private long consumeIntervalTime;

    private Map<String, Element> mergeCache = new ConcurrentHashMap<>();

    private ScheduledExecutorService consumeTimer;

    public LocationMemoryPersistQueue(PersistConsumer consumer) {
        this.persistConsumer = consumer;
        this.queue = new LinkedBlockingQueue();
        this.enable = new AtomicBoolean(true);
        this.consumeTimer = Executors.newScheduledThreadPool(2);
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
        log.info("Close Persist entity queue. scheduler size: {}", this.queue.size());
        this.enable.getAndSet(false);
        this.consumeTimer.shutdown();
        this.consumeQueue(queue);
    }

    protected void consumer(Element element) {
        if (log.isDebugEnabled()) {
            log.debug("Consumer element from persist queue. element:{}", element);
        }
        this.mergeCache.remove(getMergeKey(element));
        persistConsumer.onConsumer(element);
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
        }, 0, consumeIntervalTime, TimeUnit.MILLISECONDS);
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

    @Override
    public void afterPropertiesSet() throws Exception {
        this.startTimer();
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

    public void setConsumeIntervalTime(long consumeIntervalTime) {
        this.consumeIntervalTime = consumeIntervalTime;
    }

    public void setMergeCache(Map<String, Element> mergeCache) {
        this.mergeCache = mergeCache;
    }

    @Override
    public void destroy() throws Exception {
        this.shutDown();
    }
}
