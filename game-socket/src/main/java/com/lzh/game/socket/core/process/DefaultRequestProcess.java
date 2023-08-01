package com.lzh.game.socket.core.process;

import com.lzh.game.common.bean.EnhanceHandlerMethod;
import com.lzh.game.socket.ActionMethodSupport;
import com.lzh.game.socket.GameRequest;
import com.lzh.game.socket.core.*;
import com.lzh.game.socket.core.invoke.ConvertManager;
import com.lzh.game.socket.core.invoke.ParamConvert;
import com.lzh.game.socket.core.session.Session;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Parse target Object data earlier
 */
@Slf4j
public class DefaultRequestProcess implements Process<GameRequest> {

    private RequestHandle requestHandle;

//    private RequestProcessPool pool;

//    private ConvertManager convertManager;

//    private ActionMethodSupport<EnhanceHandlerMethod> support;
//
//    private Map<Integer, ParamConvert<?>> targetObjectConvert = new ConcurrentHashMap<>();

    private static final ParamConvert<Void> NONE_CONVERT = (e) -> null;

    public DefaultRequestProcess(RequestHandle requestHandle, ConvertManager convertManager, ActionMethodSupport<EnhanceHandlerMethod> support) {
        this(requestHandle, new DefaultPool(), convertManager, support);
    }

    public DefaultRequestProcess(RequestHandle requestHandle, RequestProcessPool pool
            , ConvertManager convertManager, ActionMethodSupport<EnhanceHandlerMethod> support) {
        this.requestHandle = requestHandle;
//        this.pool = pool;
//        this.convertManager = convertManager;
//        this.support = support;
    }

    @Override
    public void process(RemoteContext context, GameRequest request) {

//        int cmd = request.cmd();
//        if (!support.containMapping(cmd)) {
//            log.warn("Not find define cmd:{}", cmd);
//            return;
//        }
//        Object value = getConvert(request.cmd()).convert(request);
//        request.setData(value);
//        pool.submit(context, new ProcessTask(context));
    }

    private ParamConvert<?> getConvert(int cmd) {
//        return this.targetObjectConvert.computeIfAbsent(cmd, (i) -> {
//            EnhanceHandlerMethod method = support.getActionHandler(i);
//            Class<?>[] types = method.getParamsType();
//            for (Class<?> type : types) {
//                if (convertManager.isInnerConvert(type)) {
//                    continue;
//                }
//                ParamConvert<?> convert = convertManager.getConvert(type);
//                if (Objects.isNull(convert)) {
//                    throw new IllegalStateException(method.getMethod().getName() + " param type not has convert");
//                }
//                return convert;
//            }
//            return NONE_CONVERT;
//        });
        return null;
    }

    private class ProcessTask implements Runnable {

        private RemoteContext context;

        public ProcessTask(RemoteContext context) {
            this.context = context;
        }

        @Override
        public void run() {
            requestHandle.handle(context);
        }
    }


    private static class DefaultPool implements RequestProcessPool {

        private ExecutorService service = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        @Override
        public void submit(RemoteContext exchange, Runnable runnable) {
            service.submit(runnable);
        }

        @Override
        public void submit(Session session, Runnable runnable) {
            service.submit(runnable);
        }
    }
}
