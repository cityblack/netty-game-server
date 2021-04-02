package com.lzh.game.start.model.target.handler;

import com.lzh.game.start.model.player.Player;
import com.lzh.game.start.model.target.Target;
import com.lzh.game.start.model.target.TargetType;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractTargetHandler {

    public abstract void init(Player player, Target target);

    public abstract TargetType type();


}
