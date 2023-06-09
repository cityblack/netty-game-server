package com.lzh.game.start.model.target;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author zehong.l
 * @date 2023-06-09 15:52
 **/
@Getter
@Setter
public class MultiTarget {

    private int id;

    private Target[] targets;

    private int status;
    private boolean orCompose;

    @JsonIgnore
    public boolean hasTypeTarget(TargetType type) {
        for (Target target : targets) {
            if (target.getTargetType() == type) {
                return true;
            }
        }
        return false;
    }

    public boolean isCompleted() {
        return this.status >= TargetStatus.COMPLETED.getStatus();
    }

    public static MultiTarget of(int id, Target[] targets, boolean orCompose) {
        MultiTarget multiTarget = new MultiTarget();
        multiTarget.id = id;
        multiTarget.targets = targets;
        multiTarget.orCompose = orCompose;
        return multiTarget;
    }
}
