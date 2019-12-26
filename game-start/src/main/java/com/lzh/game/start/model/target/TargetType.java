package com.lzh.game.start.model.target;

public enum TargetType {
    /***
     * 等级 大于等于 {"key":x,"type":"LevelEt","value":"level"}
     */
    LEVEL_ET,

    /**
     * 达到对应的金币目标 {"key":x,"type":"GOLD","value":"x","history":"0 or 1"}
     */
    GOLD,
}
