package com.lzh.game.framework.logs.desc;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zehong.l
 * @since 2024-07-10 10:58
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LogDescDefined {

    private String logFile;

    private int logReason;
}
