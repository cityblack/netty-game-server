package com.lzh.game.framework.scheduler.impl;

import com.lzh.game.framework.scheduler.SchedulerJob;

public class SchedulerJobEntity implements SchedulerJob {

    private long jobId;

    private String name;

    private String group;

    private String desc;

    @Override
    public long jobId() {
        return jobId;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public String group() {
        return group;
    }

    @Override
    public String desc() {
        return desc;
    }


    @Override
    public int status() {
        return 0;
    }

    public void setJobId(long jobId) {
        this.jobId = jobId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
