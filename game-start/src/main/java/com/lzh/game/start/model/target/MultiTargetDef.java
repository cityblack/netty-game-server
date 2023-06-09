package com.lzh.game.start.model.target;

import lombok.Getter;
import lombok.Setter;

/**
 * {"useOr":false,"targets":[{"key":1,"type":"xx","value":100}]}
 * @author zehong.l
 * @date 2023-06-09 16:54
 **/
@Getter
@Setter
public class MultiTargetDef {

    private boolean useOr;

    private TargetDef[] targets;
}
