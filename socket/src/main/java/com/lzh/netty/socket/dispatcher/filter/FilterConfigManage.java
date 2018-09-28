package com.lzh.netty.socket.dispatcher.filter;

public interface FilterConfigManage {

    FilterConfig getConfig();

    class build {

        public static FilterConfigManage create(FilterConfig config) {

            return () -> config;
        }
    }
}
