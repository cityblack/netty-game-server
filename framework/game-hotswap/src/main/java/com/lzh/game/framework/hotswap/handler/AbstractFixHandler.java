package com.lzh.game.framework.hotswap.handler;

import com.lzh.game.framework.hotswap.handler.dao.FixDao;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author zehong.l
 * @since 2024-10-08 11:33
 **/
@Slf4j
public abstract class AbstractFixHandler implements FixHandler {

    private static volatile boolean load;

    private static final Map<String, Boolean> EXECUTE_FIX_ID = new ConcurrentHashMap<>();

    public static void executeFix(Collection<String> classNames, FixDao fixBugDao) {
        try {
            load(fixBugDao);
            var handlers = getAbleHandlers(classNames);
            if (handlers.isEmpty()) {
                log.info("Not have fix handler");
                return;
            }
            register(handlers, fixBugDao);
            long time = System.currentTimeMillis();
            for (var handler : handlers) {
                handler.fix();
            }
            log.info("Fix handler finish. use time:{}", System.currentTimeMillis() - time);
        } catch (Exception e) {
            log.error("Execute fix handler error:", e);
        }
    }

    private static void load(FixDao fixDao) {
        if (load) {
            return;
        }
        for (String id : fixDao.loadAllUsedId()) {
            EXECUTE_FIX_ID.put(id, Boolean.TRUE);
        }
        load = true;
    }

    private static void register(List<FixHandler> handlers, FixDao fixBugDao) {
        for (FixHandler handler : handlers) {
            EXECUTE_FIX_ID.put(handler.getId(), Boolean.TRUE);
            fixBugDao.saveFix(handler.getId());
        }
    }

    private static List<FixHandler> getAbleHandlers(Collection<String> classNames) throws Exception {
        List<FixHandler> list = new ArrayList<>(classNames.size());
        for (String className : classNames) {
            var clz = Class.forName(className);
            if (!FixHandler.class.isAssignableFrom(clz)) {
                continue;
            }
            FixHandler handler = (FixHandler) clz.getConstructor()
                    .newInstance();
            if (EXECUTE_FIX_ID.containsKey(handler.getId())) {
                log.warn("[{}] exist, ignore", handler.getId());
                continue;
            }
            if (handler.executed()) {
                continue;
            }
            list.add(handler);
        }
        return list;
    }

    private final AtomicBoolean executed = new AtomicBoolean(false);

    @Override
    public void fix() {
        if (!executed.compareAndSet(false, true)) {
            log.info("ignore {}. cause that was executed.", this.getClass().getName());
            return;
        }
        doFix();
    }

    @Override
    public boolean executed() {
        return executed.get();
    }

    protected abstract void doFix();
}
