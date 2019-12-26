package com.lzh.game.framework.log;

import lombok.Data;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.appender.RollingFileAppender;
import org.apache.logging.log4j.core.appender.rolling.TimeBasedTriggeringPolicy;
import org.apache.logging.log4j.core.appender.rolling.TriggeringPolicy;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.apache.logging.log4j.spi.AbstractLoggerAdapter;
import org.apache.logging.log4j.spi.ExtendedLogger;
import org.apache.logging.log4j.spi.LoggerContext;
import org.apache.logging.log4j.util.StackLocatorUtil;
import org.apache.logging.slf4j.Log4jLogger;
import org.apache.logging.slf4j.Log4jLoggerFactory;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Properties;
import java.util.function.Consumer;

public class Log4jLoggerFactoryAdapter extends AbstractLoggerAdapter<Logger> implements ILoggerFactory {

    private static final String FQCN = Log4jLoggerFactory.class.getName();
    private static final String PACKAGE = "org.slf4j";

    private static LogConfig config;

    static {
       config = loadConf();
    }

    @Override
    protected Logger newLogger(String name, LoggerContext context) {
        final String key = Logger.ROOT_LOGGER_NAME.equals(name) ? LogManager.ROOT_LOGGER_NAME : name;
        ExtendedLogger logger = context.getLogger(key);
        setLogger(logger, name, config);
        return new Log4jLogger(logger, name);
    }

    @Override
    protected LoggerContext getContext() {
        final Class<?> anchor = StackLocatorUtil.getCallerClass(FQCN, PACKAGE);
        return anchor == null ? LogManager.getContext() : getContext(StackLocatorUtil.getCallerClass(anchor));
    }

    private void setLogger(ExtendedLogger logger, String name, LogConfig config) {
        org.apache.logging.log4j.core.Logger log = (org.apache.logging.log4j.core.Logger)logger;
        PatternLayout layout = PatternLayout.newBuilder()
                .withPattern(config.pattern)
                .build();
        TriggeringPolicy policy = TimeBasedTriggeringPolicy.newBuilder()
                .withModulate(true)
                .withInterval(config.interval)
                .build();

        org.apache.logging.log4j.core.LoggerContext context = org.apache.logging.log4j.core.LoggerContext.getContext(false);
        Appender appender = RollingFileAppender.newBuilder()
                .withName(config.name)
                .setConfiguration(context.getConfiguration())
                .withLayout(layout)
//                .withFileName(config.path + name + ".log")
                .withFilePattern(config.path + name + config.fileSuffix)
                .withPolicy(policy)
                .build();
        appender.start();

        log.setAdditive(false);
        log.setLevel(Level.INFO);
        log.addAppender(appender);

//        new Log4j
    }

    @Data
    private static class LogConfig {
        private String path = "log/";
        private String name = "dynamicAppend";
        private String pattern = "%d{yyyy-MM-dd HH:mm:ss:SSS} - %m%n";
        private String fileSuffix = ".log.%d{yyyy-MM-dd}";
        private int interval = 1;
    }

    private static LogConfig loadConf() {
        String file = "logs.properties";

        try {
            InputStream inputStream = Log4jLoggerFactoryAdapter.class.getClassLoader().getResourceAsStream(file);
            Properties properties = new Properties();
            properties.load(inputStream);
            LogConfig config = new LogConfig();

            setConfig(properties,"path", config::setPath);
            setConfig(properties, "name", config::setName);
            setConfig(properties, "pattern", config::setPattern);
            setConfig(properties, "fileSuffix", config::setFileSuffix);
            setConfig(properties, "interval", e -> config.setInterval(Integer.valueOf(e)));
            return config;
        } catch (IOException e) {
            return new LogConfig();
        }
    }

    private static void setConfig(Properties properties, String key, Consumer<String> consumer) {
        String value = properties.getProperty(key);
        if (Objects.nonNull(value)) {
            consumer.accept(value);
        }
    }
}
