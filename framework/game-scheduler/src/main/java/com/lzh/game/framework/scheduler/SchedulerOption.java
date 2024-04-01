package com.lzh.game.framework.scheduler;

public interface SchedulerOption {
    /**
     * 延迟执行
     * @param task
     * @param param -- 携带参数
     * @param delay -- 延迟时间 毫秒
     * @return
     */
    SchedulerJob schedule(SchedulerTask task, SchedulerParam param, long delay);

    /**
     * 按周期执行
     * @param task
     * @param param
     * @param rateTime
     * @return
     */
    SchedulerJob scheduleAtFixedRate(SchedulerTask task, SchedulerParam param, long rateTime);

    /**
     * 按周期执行
     * @param task
     * @param param -- 携带参数
     * @param delay -- 延迟时间 毫秒
     * @param rateTime -- 周期时间 毫秒
     * @return
     */
    SchedulerJob scheduleAtFixedRate(SchedulerTask task, SchedulerParam param, long delay, long rateTime);


    /**
     * cron表达式
     * @param task
     * @param param -- 携带参数
     * @param cron
     * @return
     */
    SchedulerJob scheduleWithCron(SchedulerTask task, SchedulerParam param, String cron);

    /**
     * 删除任务
     * @param job
     */
    void removeJob(SchedulerJob job);
}
