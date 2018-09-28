package com.lzh.netty.start.filter;

import com.lzh.netty.socket.dispatcher.filter.Filter;
import com.lzh.netty.socket.dispatcher.filter.FilterConfig;
import com.lzh.netty.socket.dispatcher.filter.FilterConfigManage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfiguration {

    @Bean
    public FilterConfigManage filterConfigManage(FilterConfig filterConfig) {
        //filterConfig.addAnonProtocol(10086);
        filterConfig.addFilter(new IPFilter());
        filterConfig.addFilter(protocolVersionFilter());
        return FilterConfigManage.build.create(filterConfig);
    }

    @Bean
    public Filter protocolVersionFilter() {
        return new ProtocolVersionFilter();
    }

}
