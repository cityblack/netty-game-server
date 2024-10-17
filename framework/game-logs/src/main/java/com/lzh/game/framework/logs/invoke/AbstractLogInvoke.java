package com.lzh.game.framework.logs.invoke;

import com.lzh.game.framework.logs.LogConstant;
import com.lzh.game.framework.logs.invoke.serializer.LogContentSerializer;
import com.lzh.game.framework.logs.param.LogParam;
import com.lzh.game.framework.logs.param.LogReasonParam;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.beans.factory.DisposableBean;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author zehong.l
 * @since 2024-07-10 11:02
 **/
@Slf4j
public abstract class AbstractLogInvoke implements LogInvoke, DisposableBean {

    private final ExecutorService executorService;

    public AbstractLogInvoke() {
        this(Executors.newFixedThreadPool(2));
    }

    public AbstractLogInvoke(ExecutorService executorService) {
        this.executorService = executorService;
    }

    protected abstract void doLog(String name, Map<String, Object> content);

    @Override
    public void invoke(LogInvokeInfo invokeInfo, Object[] args) {
        final var params = convertParam(invokeInfo, args);
        executorService.submit(() -> {
            Map<String, Object> content = new HashMap<>((args.length >> 1) * 3);
            content.put(LogConstant.CREATE_TIME_NAME, System.currentTimeMillis());
            var logFile = invokeInfo.getDescDefined().getLogFile();
            content.put(LogConstant.LOG_FILE_KEY, logFile);
            int reason = invokeInfo.getDescDefined().getLogReason();
            if (reason != 0) {
                content.put(LogConstant.LOG_REASON_KEY, reason);
            }
            for (var param : params) {
                content.put(param.name, param.value);
            }
            try {
                doLog(logFile, content);
            } catch (Exception e) {
                log.error("", e);
            }
        });
    }

    private List<Param> convertParam(LogInvokeInfo invokeInfo, Object[] args) {
        List<Param> params = new ArrayList<>(args.length);
        for (int i = 0; i < args.length; i++) {
            Object arg = args[i];
            var paramName = invokeInfo.getParamNames()[i];
            if (arg instanceof LogReasonParam) {
                paramName = LogConstant.LOG_REASON_KEY;
            }
            if (arg instanceof LogParam param) {
                arg = param.factValue(paramName);
            }
            if (Objects.nonNull(arg)) {
                params.add(new Param(paramName, arg));
            }
        }
        return params;
    }

    @AllArgsConstructor
    private final static class Param {
        String name;
        Object value;
    }

    @Override
    public void destroy() throws Exception {
        this.executorService.shutdown();
        if (!this.executorService.isTerminated() && !this.executorService.awaitTermination(10, TimeUnit.SECONDS)) {
            log.info("Waiting log write to file.");
            for (Runnable runnable : this.executorService.shutdownNow()) {
                runnable.run();
            }
        }
        log.info("Closed log thead pool.");
    }
}
