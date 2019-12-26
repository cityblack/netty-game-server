package com.lzh.game.start.model.player.packet;

import lombok.Data;
import lombok.Getter;

/**
 * 注册角色
 */
@Data
public class RegisterRequest {

    private String account;

    private String name;

    private int role;
}
