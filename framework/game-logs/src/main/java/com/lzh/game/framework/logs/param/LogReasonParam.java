package com.lzh.game.framework.logs.param;

/**
 * @author zehong.l
 * @since 2024-07-10 11:15
 **/
@FunctionalInterface
public interface LogReasonParam extends LogParam {

    int getLogReason();

    @Override
    default Object factValue(String name) {
        return getLogReason();
    }
}
