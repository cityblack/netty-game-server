//package com.lzh.game.start.filter;
//
//import com.lzh.game.framework.socket.core.bootstrap.server.GameServerSocketProperties;
//import com.lzh.game.framework.socket.core.protocol.Request;
//import com.lzh.game.framework.socket.core.filter.Filter;
//import com.lzh.game.framework.socket.core.filter.FilterChain;
//import com.lzh.game.start.cmd.impl.CmdMessage;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//
//@Slf4j
//public class GmFilter implements Filter {
//
//    @Autowired
//    private GameServerSocketProperties properties;
//
////    private static final int DEFAULT_GM_PROTOCOL = CmdMessage.CM_GM;
//
//    @Override
//    public void doFilter(Request request, FilterChain chain) {
//
//        if (DEFAULT_GM_PROTOCOL == request.getMsgId()) {
//            if (!properties.isOpenGm()) {
//                if (log.isInfoEnabled()) {
//                    log.info("Gm server is not open, please check the request is valid [{}]"
//                            , request.getSession().getRemoteAddress());
//                }
//                return;
//            }
//        }
//
//        chain.filter(context);
//    }
//}
