package com.lzh.game.start.model.target;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@ToString
public class Target {
    // 关联对应的id
    @Getter
    private int id;
    // 区分一条同时有多个Target
    private int key;
    // 目标值
    @Getter
    private long targetValue;
    // 当前值
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
