package com.lzh.game.framework.logs.invoke;

import com.lzh.game.framework.logs.LogConstant;
import com.lzh.game.framework.logs.invoke.serializer.LogContentSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;

/**
 * @author zehong.l
 * @since 2024-07-12 18:26
 **/
public class DefaultLogInvoke extends Slf4jLogInvoke implements LogInvoke {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogConstant.LOGGER_NAME);

    public DefaultLogInvoke(LogContentSerializer serializer) {
        super(serializer);
    }

    public DefaultLogInvoke(ExecutorService executorService, LogContentSerializer serializer) {
        super(executorService, serializer);
    }

    @Override
    protected Logger getLogger(String logFile) {
        return LOGGER;
    }
}
