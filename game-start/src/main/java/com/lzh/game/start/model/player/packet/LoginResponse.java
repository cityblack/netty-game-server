package com.lzh.game.start.model.player.packet;

import com.lzh.game.start.model.player.Player;
import lombok.Getter;

public class LoginResponse {

    @Getter
    private boolean login;

    @Getter
    private int i18n;

    public static LoginResponse of(boolean login) {
        LoginResponse result = new LoginResponse();
        result.login = login;
        return result;
    }

    public LoginResponse setI18n(int i18n) {
        this.i18n = i18n;
        return this;
    }

    public static LoginResponse ofSuccess(Player player) {
        LoginResponse result = of(true);
        return result;
    }
}
