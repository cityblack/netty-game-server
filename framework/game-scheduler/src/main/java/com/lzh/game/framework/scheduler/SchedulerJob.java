package com.lzh.game.framework.scheduler;

public interface SchedulerJob {

    long jobId();

    String name();

    String group();

    String desc();

    int status();
}
