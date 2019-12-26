package com.lzh.game.start.model.target.handler;

import com.lzh.game.start.model.target.TargetType;

public interface TargetHandlerManage {

    AbstractTargetHandler handler(TargetType targetType);
}
