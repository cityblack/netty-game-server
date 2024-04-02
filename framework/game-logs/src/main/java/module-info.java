open module game.logs {
    requires static lombok;

    requires org.apache.logging.log4j;
    requires org.apache.logging.log4j.core;
    requires org.apache.logging.log4j.slf4j2.impl;
    requires spring.beans;
    requires spring.core;
    requires spring.boot;
    requires spring.context;
    requires javassist;
    requires game.common;

    exports com.lzh.game.framework.logs;
}