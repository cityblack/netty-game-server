package com.lzh.game.start.model.function.service;

import com.lzh.game.start.model.player.Player;
import org.springframework.stereotype.Component;

@Component
public class FunctionServiceImpl implements FunctionService {

    @Override
    public boolean isOpen(Player player, int functionId) {
        return false;
    }
}
