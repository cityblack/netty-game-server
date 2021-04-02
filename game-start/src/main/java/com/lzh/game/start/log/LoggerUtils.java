package com.lzh.game.start.log;

import com.lzh.game.common.serialization.JsonUtils;
import com.lzh.game.start.model.player.Player;
import com.lzh.game.framework.log.Log4jLoggerFactoryAdapter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class LoggerUtils {

    private static ILoggerFactory loggerFactory = new Log4jLoggerFactoryAdapter();

    private final static ExecutorService service = Executors.newFixedThreadPool(2);
    // Default param size
    private final static int DEFAULT_PARAM_SIZE = 2 << 2;

    /**
     * Use async function to log
     * @param logFile
     * @param build
     */
    private static void log(LogFile logFile, LogBuild build) {

        CompletableFuture
                .runAsync(() -> getLogger(logFile).info(toContent(build)), service)
                .exceptionally(e -> {
                    log.error("日志记录异常:", e);
                    return null;
                });
    }

    private static void log(LogFile logFile, Player player, LogBuild logBuild) {
        Map<String, Object> param = logBuild.param;
        param.put("playerId", player.getKey());
//        param.put("playerName", player.getPlayerEnt().getName());
        param.put("playerAccount", player.getPlayerEnt().getAccount());

        log(logFile, logBuild);
    }

    private static Logger getLogger(LogFile logFile) {
        return loggerFactory.getLogger(logFile.name().toLowerCase());
    }

    public static LogBuild of(LogFile logFile, LogReason logReason) {
        return LogBuild.of(logFile, logReason);
    }

    public static class LogBuild {

        private LogFile logFile;

        private LogReason logReason;

        private Map<String, Object> param;

        private LogBuild() {}

        public static LogBuild of(LogFile logFile, LogReason logReason) {
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

        public void log() {
            LoggerUtils.log(logFile, this);
        }

        public void log(Player player) {
            LoggerUtils.log(logFile, player, this);
        }
    }

    private static String toContent(LogBuild build) {
        LogReason logReason = build.logReason;
        Map<String, Object> map = build.param;
        map.put("logReason", logReason.getId());
        return JsonUtils.toJson(map);
    }

    public static void close() {
        log.debug("closing log..");
        service.shutdown();
    }
}
