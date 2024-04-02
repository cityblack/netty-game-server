open module game.common {

    requires spring.boot;
    requires spring.beans;
    requires spring.core;
    requires spring.context;
    requires eventbus;
    requires javassist;
    requires lombok;
    requires jjwt;

    requires com.fasterxml.jackson.annotation;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;

    requires protostuff.core;
    requires protostuff.api;
    requires protostuff.runtime;
    requires protostuff.collectionschema;

    exports com.lzh.game.common;
    exports com.lzh.game.common.event;
    exports com.lzh.game.common.getter;
    exports com.lzh.game.common.serialization;
    exports com.lzh.game.common.server;
    exports com.lzh.game.common.util;
}