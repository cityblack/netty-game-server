package com.lzh.game.socket.dispatcher.filter;

import java.util.List;
import java.util.Set;

public interface FilterConfig {
    /**
     * Add new filter
     * According to the interception successively adding order
     * @param filter
     */
    FilterConfig addFilter(Filter filter);

    /**
     * Allow anyone to request this exchange
     * @param protocol
     */
    FilterConfig addAnonProtocol(int protocol);

    /**
     * Allow anyone to request this exchange
     * @param begin exchange id value
     * @param end exchange id value
     */
    FilterConfig addAnonProtocol(int begin, int end);

    List<Filter> getFilters();

    Set<Integer> getAnons();

}
