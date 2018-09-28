package com.lzh.netty.socket.dispatcher;

import com.lzh.netty.socket.dispatcher.action.ActionCenter;
import com.lzh.netty.socket.exception.SessionCloseException;
import com.lzh.netty.socket.protocol.CodeState;
import com.lzh.netty.socket.protocol.Request;
import com.lzh.netty.socket.protocol.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


/**
 * 请求调度中心,该类主要完成任务线程的推送、请求结果推送
 *
 * 请求拦截、响应由{@link RequestComposeHandler} 完成
 */
@Slf4j
public class DispatcherHandler implements ApplicationContextAware {

    private ActionCenter actionCenter;
    private TaskFactory factory;
    private int responseTime;
    private RequestComposeHandler requestComposeHandler;

    public void doRequest(Request request) {
        if (log.isInfoEnabled()) {
            log.info("{} request -> {}", request.getSession().getId(), request.getProtocolId());
        }
        ServerExchange exchange = new DefaultGameServerExchange(request);
        CompletableFuture<ServerExchange> future = factory.addRequestTask(exchange, data -> {
            requestComposeHandler.handler(data,actionCenter);
            return data;
        });
        factory.addTask(() -> {
            try {
                ServerExchange resultExchange = future.get(getResponseTime(), TimeUnit.SECONDS);

                Response response = resultExchange.getResponse();
                if (null != response) {
                    //code equals 200 means the request is successful,
                    if (response.codeState() == CodeState.OK.getCode() && null != response.data()) {

                        if (response.protocolId() == -1) {
                            if (log.isInfoEnabled()) {
                                log.info("Send {} to client fail due to not defined response protocol id", response);
                            }
                            return;
                        }
                        sendMessage(resultExchange);
                    } else if (response.codeState() != CodeState.OK.getCode()) { //error or i18n means the code is not 200
                        sendMessage(resultExchange);
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
                log.error("client task response time out, {}", request.getProtocolId());
            }
        });

    }

    public int getResponseTime() {

        return responseTime;
    }

    public void setActionCenter(ActionCenter actionCenter) {
        this.actionCenter = actionCenter;
    }

    public void setResponseTime(int responseTime) {
        this.responseTime = responseTime;
    }

    public void setFactory(TaskFactory factory) {
        this.factory = factory;
    }

    public void setRequestComposeHandler(RequestComposeHandler requestComposeHandler) {
        this.requestComposeHandler = requestComposeHandler;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

    }

    private void sendMessage(ServerExchange exchange) throws SessionCloseException {

        if (!exchange.getSession().opened()) {
            log.error("Send message to client, but session is closed, response -> {}", exchange.getResponse());
            throw new SessionCloseException("Send message to client, but session is closed");
        } else {
            exchange.getSession().write(exchange.getResponse().toProtoResponse());
            if (log.isDebugEnabled()) {
                log.debug("Send message to {}", exchange.getRequest().getSession().getRemoteAddress());
            }
        }
    }
}
