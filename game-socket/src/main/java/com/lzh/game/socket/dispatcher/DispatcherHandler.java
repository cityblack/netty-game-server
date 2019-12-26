package com.lzh.game.socket.dispatcher;

import com.lzh.game.common.OptionListener;
import com.lzh.game.socket.dispatcher.action.ActionCenter;
import com.lzh.game.socket.dispatcher.exception.NotDefinedResponseProtocolException;
import com.lzh.game.socket.dispatcher.exception.NotFondProtocolException;
import com.lzh.game.socket.exchange.Request;
import com.lzh.game.socket.exchange.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Objects;


/**
 * 请求调度中心,该类主要完成任务线程的推送、请求结果推送
 *
 * 请求拦截、响应由{@link RequestComposeHandler} 完成
 */
@Slf4j
public class DispatcherHandler implements ApplicationContextAware {

    private ActionCenter actionCenter;
    private ExchangeProcess process;
    private RequestComposeHandler requestComposeHandler;

    public void doRequest(Request request) {
        ServerExchange exchange = new ServerExchangeWrapper(request);
        process.addRequestProcess(exchange, data -> {
            requestComposeHandler.handler(data, actionCenter, new OptionListener<ServerExchange>() {
                @Override
                public void success(ServerExchange exchange) {
                    doResult(exchange);
                }

                @Override
                public void error(Throwable throwable) {
                    doError(exchange, throwable);
                }
            });
            return data;
        });
    }

    private void doResult(ServerExchange exchange) {
        Response response = exchange.getResponse();
        if (response.cmd() != 0 && Objects.nonNull(response.data())) {
            sendMessage(exchange);
        }
    }

    private void doError(ServerExchange exchange, Throwable throwable) {
        if (throwable instanceof NotFondProtocolException) {
            if (log.isWarnEnabled()) {
                log.warn("Not define the exchange id:{}", ((NotFondProtocolException)throwable).getProtocol());
            }
        } else if (throwable instanceof NotDefinedResponseProtocolException) {
            log.error("The request {} result isn't null. but response exchange not defined", ((NotDefinedResponseProtocolException)throwable).getProtocol());
        } else {
            log.error("", throwable);
        }
    }

    public void setActionCenter(ActionCenter actionCenter) {
        this.actionCenter = actionCenter;
    }

    public ExchangeProcess getProcess() {
        return process;
    }

    public void setProcess(ExchangeProcess process) {
        this.process = process;
    }

    public void setRequestComposeHandler(RequestComposeHandler requestComposeHandler) {
        this.requestComposeHandler = requestComposeHandler;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

    }

    private void sendMessage(ServerExchange exchange) {

        if (!exchange.getSession().opened()) {
            log.error("Send message to client, but session is closed, response -> {}", exchange.getResponse());
        } else {
            Response response = exchange.getResponse();

            exchange.getSession().write(ProtoUtil.toProBufResponse(response.cmd(), response.data()));

        }
    }

}
