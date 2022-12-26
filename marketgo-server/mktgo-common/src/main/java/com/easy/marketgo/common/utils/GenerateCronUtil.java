package com.easy.marketgo.common.utils;

import com.easy.marketgo.common.constants.CronPatternConstant;
import com.easy.marketgo.common.enums.cron.PeriodEnum;
import com.easy.marketgo.common.enums.cron.WeekEnum;
import lombok.extern.slf4j.Slf4j;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 12/25/22 11:53 PM
 * Describe:
 */
@Slf4j
public enum GenerateCronUtil {
    /**
     * 单例
     */
    INSTANCE;

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private final SimpleDateFormat weekFormat = new SimpleDateFormat("E");

    /**
     * 根据执行周期和初次执行时间，生成cron表达式
     *
     * @param period    执行周期
     * @param beginTime 初次执行时间
     * @return cron表达式
     */
    public String generateCronByPeriodAndTime(PeriodEnum period, String beginTime) {
        Date parsedDate;
        try {
            parsedDate = dateFormat.parse(beginTime);
        } catch (ParseException e) {
            log.error("parse time error. [time]: {}", beginTime);
            return "";
        }
        String[] dateAndTime = beginTime.split(" ");
        String date = dateAndTime[0];
        String time = dateAndTime[1];
        String[] splitDate = date.split("-");
        String year = splitDate[0];
        String month = splitDate[1];
        String day = splitDate[2];
        String[] splitTime = time.split(":");
        String hour = splitTime[0];
        String minute = splitTime[1];
        String second = splitTime[2];
        String cron = "";
        switch (period) {
            case ONCE:
                cron = String.format(CronPatternConstant.ONCE_CRON_PATTERN, second, minute, hour, day, month, year);
                break;
            case DAILY:
                cron = String.format(CronPatternConstant.DAILY_CRON_PATTERN, second, minute, hour);
                break;
            case WEEKLY:
                String week = weekFormat.format(parsedDate);
                String weekCode = WeekEnum.nameOf(week).getCode();
                cron = String.format(CronPatternConstant.WEEKLY_CRON_PATTERN, second, minute, hour, weekCode);
                break;
            case MONTHLY:
                cron = String.format(CronPatternConstant.MONTHLY_CRON_PATTERN, second, minute, hour, day);
                break;
            default:
                break;
        }
        return cron;
    }

    /**
     * 根据执行周期和初次执行时间，生成cron表达式
     *
     * @param beginDate 初次执行日期
     * @param beginTime 初次执行时间
     * @return cron表达式
     */
    public String generateDailyCronByPeriodAndTime(String beginDate, String beginTime) {
        String[] splitDate = beginDate.split("-");
        String year = splitDate[0];
        String month = splitDate[1];
        String day = splitDate[2];
        String[] splitTime = beginTime.split(":");
        String hour = splitTime[0];
        String minute = splitTime[1];
        String second = splitTime[2];
        String cron = cron = String.format(CronPatternConstant.DAILY_CRON_PATTERN, second, minute, hour);
        return cron;
    }

    /**
     * 根据执行周期和初次执行时间，生成cron表达式
     *
     * @param beginDate 初次执行日期
     * @param beginTime 初次执行时间
     * @param week      星期的name
     * @return cron表达式
     */
    public String generateWeeklyCronByPeriodAndTime(String beginDate, String beginTime, String week) {

        String[] splitDate = beginDate.split("-");
        String year = splitDate[0];
        String month = splitDate[1];
        String day = splitDate[2];
        String[] splitTime = beginTime.split(":");
        String hour = splitTime[0];
        String minute = splitTime[1];
        String second = splitTime[2];

        String weekCode = WeekEnum.nameOf(week).getCode();
        String cron = String.format(CronPatternConstant.WEEKLY_CRON_PATTERN, second, minute, hour, weekCode);

        return cron;
    }

    /**
     * 根据执行周期和初次执行时间，生成cron表达式
     *
     * @param beginDate 初次执行日期
     * @param beginTime 初次执行时间
     * @param day       日期的值
     * @return cron表达式
     */
    public String generateMonthlyCronByPeriodAndTime(String beginDate, String beginTime, String day) {

        String[] splitTime = beginTime.split(":");
        String hour = splitTime[0];
        String minute = splitTime[1];
        String second = splitTime[2];

        String cron = String.format(CronPatternConstant.MONTHLY_CRON_PATTERN, second, minute, hour, day);
        return cron;
    }
}
