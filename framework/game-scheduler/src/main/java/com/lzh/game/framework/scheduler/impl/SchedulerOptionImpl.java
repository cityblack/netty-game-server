package com.lzh.game.framework.scheduler.impl;

import com.lzh.game.common.util.IdGenerator;
import com.lzh.game.framework.scheduler.SchedulerJob;
import com.lzh.game.framework.scheduler.SchedulerOption;
import com.lzh.game.framework.scheduler.SchedulerParam;
import com.lzh.game.framework.scheduler.SchedulerTask;
import org.quartz.*;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.DateBuilder.futureDate;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;

public class SchedulerOptionImpl implements SchedulerOption {

    private static final String DEFAULT_GROUP = "task_group";

    private Scheduler scheduler;

    public SchedulerOptionImpl(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    @Override
    public SchedulerJob schedule(SchedulerTask task, SchedulerParam param, long delay) {

        JobDetail detail = createJob(task, param);
        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(param.getName(), DEFAULT_GROUP)
                .startAt(futureDate((int) delay, DateBuilder.IntervalUnit.MILLISECOND))
                .build();
        addJob(detail, trigger);

        return buildJobInfo(detail);
    }

    @Override
    public SchedulerJob scheduleAtFixedRate(SchedulerTask task, SchedulerParam param, long rateTime) {
        JobDetail detail = createJob(task, param);
        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(param.getName(), DEFAULT_GROUP)
                .startNow()
                .withSchedule(simpleSchedule()
                        .withIntervalInMilliseconds(rateTime)
                        .repeatForever())
                .build();
        addJob(detail, trigger);
        return buildJobInfo(detail);
    }

    @Override
    public SchedulerJob scheduleAtFixedRate(SchedulerTask task, SchedulerParam param, long delay, long rateTime) {
        JobDetail detail = createJob(task, param);
        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(param.getName(), DEFAULT_GROUP)
                .startAt(futureDate((int) delay, DateBuilder.IntervalUnit.MILLISECOND))
                .withSchedule(simpleSchedule()
                        .withIntervalInMilliseconds(rateTime)
                        .repeatForever())
                .build();
        addJob(detail, trigger);
        return buildJobInfo(detail);
    }

    @Override
    public SchedulerJob scheduleWithCron(SchedulerTask task, SchedulerParam param, String cron) {
        JobDetail detail = createJob(task, param);
        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(param.getName(), DEFAULT_GROUP)
                .withSchedule(cronSchedule(cron))
                .build();
        addJob(detail, trigger);
        return buildJobInfo(detail);
    }

    @Override
    public void removeJob(SchedulerJob job) {
        JobKey key = getKey(job);

        try {
            scheduler.pauseJob(key);
            scheduler.deleteJob(key);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    private JobKey getKey(SchedulerJob job) {
        return JobKey.jobKey(job.name(), job.group());
    }

    private JobDetail createJob(SchedulerTask task, SchedulerParam param) {
        Class<? extends SchedulerTask> clazz = task.getClass();
        JobDetail detail = JobBuilder.newJob(clazz).withIdentity(param.getName()).build();
        if (!param.getExt().isEmpty()) {
            detail.getJobDataMap().putAll(param.getExt());
        }
        return detail;
    }

    private SchedulerJob buildJobInfo(JobDetail detail) {
        SchedulerJobEntity entity = new SchedulerJobEntity();
        entity.setName(detail.getKey().getName());
        entity.setJobId(IdGenerator.singleton().createLongId());
        entity.setGroup(detail.getKey().getGroup());
        return entity;
    }

    private void addJob(JobDetail detail, Trigger trigger) {
        try {
            scheduler.scheduleJob(detail, trigger);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }
}
