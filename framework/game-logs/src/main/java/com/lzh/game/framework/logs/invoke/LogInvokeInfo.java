package com.lzh.game.framework.logs.invoke;

import com.lzh.game.framework.logs.desc.LogDescDefined;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author zehong.l
 * @since 2024-07-09 18:33
 **/
@Data
@Accessors(chain = true)
public class LogInvokeInfo {

    private Class<?> parentClass;

    private LogDescDefined descDefined;

    private String[] paramNames;

    private Class<?>[] paramTypes;

    private int paramLogReasonIndex;


}
