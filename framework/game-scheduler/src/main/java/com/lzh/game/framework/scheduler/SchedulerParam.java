package com.lzh.game.framework.scheduler;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class SchedulerParam {

    private String name;

    private Map<String, Object> ext;

    public static SchedulerParam of(String name) {
        SchedulerParam param = new SchedulerParam();
        param.name = name;
        param.ext = new HashMap<>(4);
        return param;
    }

    public SchedulerParam addParam(String key, Object value) {
        this.ext.put(key, value);
        return this;
    }
}
