package com.lzh.game.start.model.i18n;

public interface I18n {
    //系统错误
    int SYS_ERROR = 100;
    // 非法参数
    int ILLEGAL = 101;
    // 登陆错误
    int LOGIN_ERROR = 110;
    // 随意填写的消息 {}
    int FREE_MSG = 111;
    //========= 物品 ======
    // 背包空间不足
    int NOT_ENOUGH_GRID = 1000;
    // 物品不足
    int NOT_ENOUGH_ITEMS = 1001;

    // === MoveRequest 1100==
    // 尚有步数 不可以投骰子
    int HAS_DICE_MOVE_COUNT = 1100;
    // 骰子不足
    int NOT_ENOUGH_DICE = 1101;
    // ===== 资金 1200 ===
    // 资金不足
    int NOT_ENOUGH_GOLD = 1200;
    // 钻石不足
    int NOT_ENOUGH_DIAMOND = 1201;
    // 积分不足
    int NOT_ENOUGH_INTEGRAL = 1202;

}
