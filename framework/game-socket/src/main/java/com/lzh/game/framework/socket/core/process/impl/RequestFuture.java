package com.lzh.game.framework.socket.core.process.impl;

import com.lzh.game.framework.socket.core.NameThreadFactory;
import com.lzh.game.framework.socket.core.protocol.Request;
import com.lzh.game.framework.socket.core.protocol.Response;
import io.netty.util.HashedWheelTimer;
import io.netty.util.Timeout;
import io.netty.util.Timer;
import io.netty.util.TimerTask;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.*;

@Slf4j
public class RequestFuture extends CompletableFuture<Response> {

    private static final Map<Integer, RequestFuture> FUTURES = new ConcurrentHashMap<>();

    private static final Timer TIME_OUT_TIMER = new HashedWheelTimer(new NameThreadFactory("remote-future-timeout", true), 30, TimeUnit.MILLISECONDS);

    private final int id;

    private final long timeout;

    private final long start = System.currentTimeMillis();

//    private volatile long sent;

    private Timeout timeoutTask;

    private final ExecutorService service;

    private RequestFuture(Request request, long timeout, ExecutorService service) {
        this.id = request.getRequestId();
        this.timeout = timeout;
        this.service = service;
        FUTURES.put(id, this);
    }

    /**
     * New a request future
     *
     * @param request
     * @param timeout
     * @param executorService
     * @return
     */
    public static RequestFuture newFuture(Request request, long timeout, ExecutorService executorService) {
        final RequestFuture future = new RequestFuture(request, timeout, executorService);
        timeoutTask(future);
        return future;
    }

    private static void timeoutTask(RequestFuture future) {
        TimeoutCheckTask task = new TimeoutCheckTask(future.id);
        future.timeoutTask = TIME_OUT_TIMER.newTimeout(task, future.timeout, TimeUnit.MILLISECONDS);
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        Response response = new Response();
        this.doReceived(response);
        return true;
    }

    public static void received(Response response, boolean timeout) {
        RequestFuture future = FUTURES.remove(response.getRequestId());
        if (Objects.nonNull(future)) {
            Timeout t = future.timeoutTask;
            if (timeout) {
                t.cancel();
            }
            future.doReceived(response);
        } else {
            log.warn("Could not find request future: {}", response.getRequestId());
        }
    }

    private void doReceived(Response response) {
        if (Objects.isNull(response)) {
            throw new IllegalArgumentException("Response is null");
        }
        if (response.getStatus() == 0) {
            this.complete(response);
        } else {
            Throwable error = Objects.isNull(response.getError()) ? new IllegalArgumentException()
                    : response.getError();
            this.completeExceptionally(error);
        }
    }

    public static RequestFuture getFuture(int requestId) {
        return FUTURES.get(requestId);
    }

    private static class TimeoutCheckTask implements TimerTask {

        private final int requestId;

        public TimeoutCheckTask(int requestId) {
            this.requestId = requestId;
        }

        @Override
        public void run(Timeout timeout) throws Exception {
            RequestFuture future = RequestFuture.getFuture(requestId);
            if (Objects.isNull(future) || future.isDone() || future.isCancelled()) {
                return;
            }
            if (Objects.nonNull(future.service)) {
                future.service.execute(() -> notifyTimeout(future));
            } else {
                notifyTimeout(future);
            }
        }

        private void notifyTimeout(RequestFuture future) {
            Response response = new Response();
            response.setError(new TimeoutException());
            response.setRequestId(future.id);
            RequestFuture.received(response, true);
        }
    }
}
