package com.lzh.game.start.model.target;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Target system, It's used for Player Achievement
 * A target may consist of multiple target.
 * exp: [{"key":1,"type":"LevelEt","value":10},{}]
 */
@ToString
public class Target {
    // unique id
    @Getter
    private int id;

    @Getter
    private int key;
    // target value
    @Getter
    private long targetValue;
    // the target current value
    @Getter
    @Setter
    private long currentValue;

    @Getter
    private TargetType targetType;

    private Map<String, String> params = new HashMap<>(4);

    // 是否统计历史 0 -> 不统计， 1 -> 统计
    private int history;

    @JsonIgnore
    public boolean isComplete() {
        return currentValue >= targetValue;
    }

    public void addProcessValue(int value) {
        this.currentValue += value;
    }

    public void addProcessValue() {
        addProcessValue(1);
    }

    public boolean isHistory() {
        return history == 1;
    }

    public Target() {
    }

    public Target(int id, TargetDef targetDef) {
        this.id = id;
        this.key = targetDef.getKey();
        this.targetType = targetDef.getType();
        this.history = targetDef.getHistory();
        if (Objects.nonNull(targetDef.getParam())) {
            this.params.putAll(targetDef.getParam());
        }
        this.targetValue = targetDef.getValue();
    }
}
