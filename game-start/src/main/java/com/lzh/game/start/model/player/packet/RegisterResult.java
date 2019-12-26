package com.lzh.game.start.model.player.packet;

import lombok.Data;

@Data
public class RegisterResult {

    private boolean valid;

    public static RegisterResult of(boolean valid) {
        RegisterResult result = new RegisterResult();
        result.valid = valid;
        return result;
    }
}
