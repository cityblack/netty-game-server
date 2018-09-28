package com.lzh.netty.socket.dispatcher.filter;

import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
public class DefaultFilterConfig implements FilterConfig {

    private final List<Filter> list = new LinkedList<>();
    private final Set<Integer> anonProtoCol = new HashSet<>();

    @Override
    public FilterConfig addFilter(Filter filter) {
        Objects.requireNonNull(filter);
        if (log.isDebugEnabled()) {
            log.debug("Resister filter {}", filter.getClass().getName());
        }
        list.add(filter);
        return this;
    }

    @Override
    public FilterConfig addAnonProtocol(int protocol) {
        anonProtoCol.add(protocol);
        return this;
    }

    @Override
    public FilterConfig addAnonProtocol(int begin, int end) {

        for (int i = begin; i <= end; i++) {
            anonProtoCol.add(i);
        }

        return this;
    }

    @Override
    public List<Filter> getFilters() {
        return list;
    }

    @Override
    public Set<Integer> getAnons() {
        return anonProtoCol;
    }

}
