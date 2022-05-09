package com.lzh.game.socket.core;

import com.lzh.game.socket.GameResponse;
import com.lzh.game.socket.Request;
import com.lzh.game.socket.Response;
import io.netty.util.HashedWheelTimer;
import io.netty.util.Timeout;
import io.netty.util.Timer;
import io.netty.util.TimerTask;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.*;

@Slf4j
public class RequestFuture<T> extends CompletableFuture<T> {

    private static final Map<Integer, RequestFuture<?>> FUTURES = new ConcurrentHashMap<>();

    private static final Timer TIME_OUT_TIMER = new HashedWheelTimer(new NameThreadFactory("remote-future-timeout", true), 30, TimeUnit.MILLISECONDS);

    private final int id;

    private final long timeout;

    private final long start = System.currentTimeMillis();

//    private volatile long sent;

    private Timeout timeoutTask;

    private final ExecutorService service;

    private final Class<T> responseType;

    private RequestFuture(Request request, long timeout, ExecutorService service, Class<T> responseType) {
        this.id = request.remoteId();
//        this.request = request;
        this.timeout = timeout;
        this.service = service;
        this.responseType = responseType;
        FUTURES.put(id, this);
    }

    public Class<T> getResponseType() {
        return responseType;
    }

    /**
     * New a request future
     * @param request
     * @param timeout
     * @param executorService
     * @return
     */
    public static <T>RequestFuture<T> newFuture(Request request, long timeout, ExecutorService executorService, Class<T> clazz) {
        final RequestFuture<T> future = new RequestFuture(request, timeout, executorService, clazz);
        timeoutTask(future);
        return future;
    }

    private static void timeoutTask(RequestFuture future) {
        TimeoutCheckTask task = new TimeoutCheckTask(future.id);
        future.timeoutTask = TIME_OUT_TIMER.newTimeout(task, future.timeout, TimeUnit.MILLISECONDS);
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        GameResponse response = new GameResponse();
        this.doReceived(response);
        return true;
    }

    public static void received(Response response, boolean timeout) {
        try {
            RequestFuture future = FUTURES.get(response.remoteId());
            if (Objects.nonNull(future)) {
                Timeout t = future.timeoutTask;
                if (!timeout) {
                    t.cancel();
                }
                future.doReceived(response);
            } else {
                log.warn("Could not find request future: {}", response.remoteId());
            }
        } finally {
            FUTURES.remove(response.remoteId());
        }
    }

    private void doReceived(Response response) {
        if (Objects.isNull(response)) {
            throw new IllegalArgumentException("Response is null");
        }
        if (response.status() == Response.OK) {
            this.complete((T) response.data());
        } else {
            this.completeExceptionally(new IllegalArgumentException());
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
            GameResponse response = new GameResponse();
            response.setError(new TimeoutException());
            response.setRemoteId(future.id);
            RequestFuture.received(response, true);
        }
    }
}
