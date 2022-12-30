package com.easy.marketgo.biz.service;


import org.springframework.scheduling.support.CronExpression;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 12/26/22 10:21 AM
 * Describe:
 */
public class CronExpressionResolver {

    public static CronExpressionResolver getInstance(String cron) {
        return new CronExpressionResolver(cron);
    }

    private CronExpressionResolver(String cron) {
        this.cron = cron;
    }

    private String cron;


    /**
     * 获得下一个符合Cron表达式的时间
     *
     * @param time time
     * @return next time
     */
    public Date nextDateTime(Date time) {
        CronExpression cronExpression = CronExpression.parse(cron);
        // currentTime为计算下次时间点的开始时间
        ZoneId zoneId = ZoneId.systemDefault();
        LocalDateTime localDateTime = time.toInstant().atZone(zoneId).toLocalDateTime();
        return Date.from(cronExpression.next(localDateTime).atZone(zoneId).toInstant());
    }

    /**
     * 获得下一个符合Cron表达式的时间
     *
     * @param time time
     * @return next time
     */
    public Date nextDateTime(long time) {
        CronExpression cronExpression = CronExpression.parse(cron);
        // currentTime为计算下次时间点的开始时间
        ZoneId zoneId = ZoneId.systemDefault();
        LocalDateTime localDateTime = new Date(time).toInstant().atZone(zoneId).toLocalDateTime();
        return Date.from(cronExpression.next(localDateTime).atZone(zoneId).toInstant());
    }

    /**
     * 获得下一个符合Cron表达式的时间
     *
     * @param time time
     * @return next time
     */
    public long nextLongTime(Date time) {
        CronExpression cronExpression = CronExpression.parse(cron);
        // currentTime为计算下次时间点的开始时间
        ZoneId zoneId = ZoneId.systemDefault();
        LocalDateTime localDateTime = time.toInstant().atZone(zoneId).toLocalDateTime();
        return Date.from(cronExpression.next(localDateTime).atZone(zoneId).toInstant()).getTime();
    }

    /**
     * 获得下一个符合Cron表达式的时间
     *
     * @param time time
     * @return next time
     */
    public long nextLongTime(long time) {
        CronExpression cronExpression = CronExpression.parse(cron);
        // currentTime为计算下次时间点的开始时间
        ZoneId zoneId = ZoneId.systemDefault();
        LocalDateTime localDateTime = new Date(time).toInstant().atZone(zoneId).toLocalDateTime();
        return cronExpression.next(localDateTime) == null ? 0 :
                Date.from(cronExpression.next(localDateTime).atZone(zoneId).toInstant()).getTime();
    }

    /**
     * 生成Cron表达式
     *
     * @param num  数值
     * @param unit 单位：s-秒，m-分，h-小时，d-天，mon-月
     * @return
     */
    public static String timeToCron(int num, String unit) {
        switch (unit) {
            case "s":
                return "0/" + num + " * * * * ?";
            case "m":
                return "0 0/" + num + " * * * ?";
            case "h":
                return "0 0 0/" + num + " * * ?";
            case "d":
                return "0 0 0 1/" + num + " * ?";
            case "mon":
                return "0 0 0 0 1/" + num + " ?";
            default:
                return "";
        }
    }
}
