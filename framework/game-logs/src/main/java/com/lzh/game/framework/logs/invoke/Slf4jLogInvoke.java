package com.lzh.game.framework.logs.invoke;

import com.lzh.game.framework.logs.invoke.serializer.LogContentSerializer;
import org.slf4j.Logger;

import java.util.Map;
import java.util.concurrent.ExecutorService;

/**
 * @author zehong.l
 * @since 2024-10-17 14:54
 **/
public abstract class Slf4jLogInvoke extends AbstractLogInvoke {

    private final LogContentSerializer serializer;

    public Slf4jLogInvoke(LogContentSerializer serializer) {
        super();
        this.serializer = serializer;
    }

    public Slf4jLogInvoke(ExecutorService executorService, LogContentSerializer serializer) {
        super(executorService);
        this.serializer = serializer;
    }

    protected abstract Logger getLogger(String logFile);

    @Override
    protected void doLog(String name, Map<String, Object> content) {
        getLogger(name).info(serializer.serializer(content));
    }
}
