package com.lzh.game.start.filter;

import com.lzh.game.framework.socket.core.filter.Filter;
import com.lzh.game.framework.socket.core.filter.FilterChain;
import com.lzh.game.framework.socket.core.protocol.Request;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;

@Slf4j
public class IPFilter implements Filter {
    private List<String> ipArr = Arrays.asList("192.168.0.1");

    @Override
    public void doFilter(Request request, FilterChain chain) {
        if (ipArr.contains(request.getSession().getRemoteAddress())) {

        } else {
            chain.filter(request, null);
        }
    }
}
