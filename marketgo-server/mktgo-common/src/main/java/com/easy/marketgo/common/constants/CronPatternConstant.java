package com.easy.marketgo.common.constants;

import lombok.experimental.UtilityClass;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 12/25/22 11:52 PM
 * Describe:
 */
@UtilityClass
public class CronPatternConstant {
    /**
     * 执行单次cron表达式模板
     * eg: 59 59 23 1 12 ? 2022 (2022-12-01 23:59:59执行一次)
     */
    public String ONCE_CRON_PATTERN = "%s %s %s %s %s ? %s";
    /**
     * 每天执行cron表达式模板
     * eg: 59 59 23 * * ? (每日23:59:59执行)
     */
    public String DAILY_CRON_PATTERN = "%s %s %s * * ?";
    /**
     * 每周执行cron表达式模板
     * eg: 59 59 23 ? * Fri (每周五23:59:59执行)
     */
    public String WEEKLY_CRON_PATTERN = "%s %s %s ? * %s";
    /**
     * 每月执行cron表达式模板
     * eg: 59 59 23 8 * ? (每月8号23:59:59执行)
     */
    public String MONTHLY_CRON_PATTERN = "%s %s %s %s * ?";
}
