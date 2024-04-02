package com.lzh.game.framework.logs;

import com.lzh.game.common.serialization.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Slf4j
public class LoggerUtils {

    private static ILoggerFactory loggerFactory = new Log4jLoggerFactoryAdapter();

//    private final static ExecutorService service = Executors.newFixedThreadPool(2);
    // Default param size
    private final static int DEFAULT_PARAM_SIZE = 6;

    /**
     * Use async function to log
     * @param logFile
     * @param build
     */
    private static void log(String logFile, LogBuild build) {

        CompletableFuture
                .runAsync(() -> getLogger(logFile).info(toContent(build)))
                .exceptionally(e -> {
                    log.error("日志记录异常:", e);
                    return null;
                });
    }

    private static Logger getLogger(String logFile) {
        return loggerFactory.getLogger(logFile);
    }

    public static LogBuild of(String logFile, int logReason) {
        return LogBuild.of(logFile, logReason);
    }

    public static class LogBuild {

        private String logFile;

        private int logReason;

        private Map<String, Object> param;

        private LogBuild() {}

        public static LogBuild of(String logFile, int logReason) {
            LogBuild build = new LogBuild();
            build.logFile = logFile;
            build.logReason = logReason;
            build.param = new HashMap<>(DEFAULT_PARAM_SIZE);
            return build;
        }

        public LogBuild addParam(String key, Object value) {
            this.param.put(key, value);
            return this;
        }

        public LogBuild addParam(Map<String, Object> param) {
            this.param.putAll(param);
            return this;
        }

        public LogBuild addParam(String[] params, Object[] values) {
            if (params.length != values.length) {
                throw new RuntimeException("Log method param len is not equals");
            }
            for (int i = 0; i < params.length; i++) {
                String param = params[i];
                Object value = values[i];
                this.param.put(param, value);
            }
            return this;
        }

        public void log() {
            LoggerUtils.log(logFile, this);
        }

//        public void log(Serializable user) {
//            addParam("optionId", user).log();
//        }
    }

    private static String toContent(LogBuild build) {
        Map<String, Object> map = build.param;
        map.put("logReason", build.logReason);
        return JsonUtils.toJson(map);
    }

    public static void close() {
        log.debug("closing log..");
//        service.shutdown();
    }
}
