package com.easy.marketgo.common.utils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * 日期格式化工具
 *
 * @author : kevinwang
 * @version : 1.0
 * @data : 7/7/22 7:17 PM
 * Describe:
 */
public class DateFormatUtils {

    public static final String DATETIME = "yyyy-MM-dd HH:mm:ss";

    public static final String DATE = "yyyy-MM-dd";

    /**
     * 春节期间
     */
    public final static Map<Integer, String> springFestivalPeriodMap = Maps.newHashMap();

    private static String[] chineseWeekDateName = new String[]{"日", "一", "二", "三", "四", "五", "六"};

    static {
        springFestivalPeriodMap.put(2013, "2013-02-09 00:00:00|2013-02-15 00:00:00");
        springFestivalPeriodMap.put(2014, "2014-01-30 00:00:00|2014-02-05 00:00:00");
        springFestivalPeriodMap.put(2015, "2015-02-18 00:00:00|2015-02-24 00:00:00");
        springFestivalPeriodMap.put(2016, "2016-02-07 00:00:00|2016-02-13 00:00:00");
        springFestivalPeriodMap.put(2017, "2017-01-27 00:00:00|2017-02-03 00:00:00");
        springFestivalPeriodMap.put(2018, "2018-02-15 00:00:00|2018-02-22 00:00:00");
        springFestivalPeriodMap.put(2019, "2019-02-04 00:00:00|2019-02-11 00:00:00");
        springFestivalPeriodMap.put(2020, "2020-01-24 00:00:00|2020-01-31 00:00:00");
        springFestivalPeriodMap.put(2021, "2021-02-11 00:00:00|2021-02-17 00:00:00");
        springFestivalPeriodMap.put(2022, "2022-01-31 00:00:00|2022-02-06 00:00:00");
        springFestivalPeriodMap.put(2023, "2023-01-21 00:00:00|2023-01-27 00:00:00");
    }

    public static String formatDate(Date date) {

        return formatDate(date, DATE);
    }

    public static String formatDate(Date date, String pattern) {

        if (date == null) {
            return null;
        }
        return org.apache.commons.lang3.time.DateFormatUtils.format(date, pattern, Locale.CHINA);
    }

    public static String formatDateTime(Date date) {

        return formatDate(date, DATETIME);
    }

    public static String formatDate(String dateStr, String srcPattern, String desPattern) {

        Date date = parseDate(dateStr, srcPattern);
        if (date == null) {
            return null;
        }
        return formatDate(date, desPattern);
    }

    public static Date parseDate(String dateStr, String pattern) {

        return parseDate(StringUtils.trim(dateStr), new String[]{pattern});
    }

    public static Date parseDate(String dateStr, String[] patterns) {

        if (dateStr == null) {
            return null;
        }
        try {
            return DateUtils.parseDateStrictly(dateStr, patterns);
        } catch (ParseException e) {
            return null;
        }
    }

    public static Date parseDate(String dateStr) {

        return parseDate(dateStr, DATE);
    }

    public static Date parseDateTime(String dateStr) {

        return parseDate(dateStr, new String[]{
                DATETIME,
                "yyyy-MM-dd HH:mm:ss.SSS",
        });
    }

    public static Date parseDate(long timeMillis) {

        return Date.from(Instant.ofEpochMilli(timeMillis));
    }

    public static Date parseDate(long timeMillis, String parrern) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return new Date(timeMillis);
    }


    public static LocalDateTime parseLocalDateTime(String datetimeStr, String pattern) {

        if (datetimeStr == null) {
            return null;
        }
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
            return LocalDateTime.parse(datetimeStr, formatter);
        } catch (Exception e) {
            return null;
        }
    }

    public static LocalDateTime parseLocalDateTime(String datetimeStr) {
        return parseLocalDateTime(datetimeStr, DATETIME);
    }

    public static String formatLocalDateTime(LocalDateTime datetime, String pattern) {

        if (datetime == null) {
            return null;
        }
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
            return datetime.format(formatter);
        } catch (Exception e) {
            return null;
        }
    }

    public static String formatLocalDateTime(LocalDateTime datetime) {
        return formatLocalDateTime(datetime, DATETIME);
    }

    public static LocalDate parseLocalDate(String dateStr, String pattern) {

        if (dateStr == null) {
            return null;
        }
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
            return LocalDate.parse(dateStr, formatter);
        } catch (Exception e) {
            return null;
        }
    }

    public static String formatLocalDate(LocalDate date, String pattern) {

        if (date == null) {
            return null;
        }
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
            return date.format(formatter);
        } catch (Exception e) {
            return null;
        }
    }

    public static boolean test(String dateStr, String pattern) {

        return test(dateStr, new String[]{pattern});
    }

    public static boolean test(String dateStr, String[] patterns) {

        if (dateStr == null) {
            return false;
        }
        try {
            DateUtils.parseDateStrictly(dateStr, patterns);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    public static Date convertTo(LocalDateTime localDateTime) {

        ZonedDateTime zdt = localDateTime.atZone(ZoneId.systemDefault());
        return Date.from(zdt.toInstant());
    }

    public static LocalDateTime convertFrom(Date date) {

        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }


    public static void main(String[] args) {


        System.out.println(parseTimeOrOrigin("1609257600000"));

        {
            LocalDateTime beginTime = LocalDateTime.of(2016, 11, 1, 0, 0, 0);
            LocalDateTime endTime = LocalDateTime.of(2016, 12, 1, 23, 2, 59);
            List<Pair<LocalDateTime, LocalDateTime>> list = DateFormatUtils.splitTimePieces(beginTime, endTime, 1,
                    ChronoUnit.DAYS);
            list.forEach(pair -> {
                System.out.println(pair.getLeft()
                        .format(DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss")) + " - " + pair.getRight()
                        .format(DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss")));
            });
            System.out.println("list size : " + list.size());
        }

        {

            LocalDateTime beginTime = LocalDateTime.of(2016, 11, 1, 0, 0, 0);
            LocalDateTime endTime = LocalDateTime.of(2016, 12, 1, 23, 2, 59);
            List<Pair<LocalDateTime, LocalDateTime>> list = DateFormatUtils.splitTimePieces(beginTime, endTime, 6,
                    ChronoUnit.DAYS);
            list.forEach(pair -> {
                System.out.println(pair.getLeft()
                        .format(DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss")) + " - " + pair.getRight()
                        .format(DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss")));
            });
            System.out.println("list size : " + list.size());
        }

        {

            LocalDateTime beginTime = LocalDateTime.of(2016, 11, 30, 0, 0, 0);
            LocalDateTime endTime = LocalDateTime.of(2016, 12, 1, 23, 2, 59);
            List<Pair<LocalDateTime, LocalDateTime>> list = DateFormatUtils.splitTimePieces(beginTime, endTime, 6,
                    ChronoUnit.HOURS);
            list.forEach(pair -> {
                System.out.println(pair.getLeft()
                        .format(DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss")) + " - " + pair.getRight()
                        .format(DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss")));
            });
            System.out.println("list size : " + list.size());
        }

        {

            LocalDateTime beginTime = LocalDateTime.of(2016, 11, 30, 0, 0, 0);
            LocalDateTime endTime = LocalDateTime.of(2016, 12, 1, 23, 2, 59);
            List<Pair<LocalDateTime, LocalDateTime>> list = DateFormatUtils.splitTimePieces(beginTime, endTime, 1,
                    ChronoUnit.HOURS);
            list.forEach(pair -> {
                System.out.println(pair.getLeft()
                        .format(DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss")) + " - " + pair.getRight()
                        .format(DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss")));
            });
            System.out.println("list size : " + list.size());
        }

        {

            LocalDateTime beginTime = LocalDateTime.of(2016, 11, 30, 0, 0, 0);
            LocalDateTime endTime = LocalDateTime.of(2016, 11, 30, 0, 30, 59);
            List<Pair<LocalDateTime, LocalDateTime>> list = DateFormatUtils.splitTimePieces(beginTime, endTime, 6,
                    ChronoUnit.MINUTES);
            list.forEach(pair -> {
                System.out.println(pair.getLeft()
                        .format(DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss")) + " - " + pair.getRight()
                        .format(DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss")));
            });
            System.out.println("list size : " + list.size());
        }

        {

            LocalDateTime beginTime = LocalDateTime.of(2016, 11, 30, 0, 0, 0);
            LocalDateTime endTime = LocalDateTime.of(2016, 11, 30, 0, 30, 59);
            List<Pair<LocalDateTime, LocalDateTime>> list = DateFormatUtils.splitTimePieces(beginTime, endTime, 1,
                    ChronoUnit.MINUTES);
            list.forEach(pair -> {
                System.out.println(pair.getLeft()
                        .format(DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss")) + " - " + pair.getRight()
                        .format(DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss")));
            });
            System.out.println("list size : " + list.size());
        }

        {

            LocalDateTime beginTime = LocalDateTime.of(2016, 11, 30, 0, 0, 0);
            LocalDateTime endTime = LocalDateTime.of(2016, 11, 30, 0, 2, 59);
            List<Pair<LocalDateTime, LocalDateTime>> list = DateFormatUtils.splitTimePieces(beginTime, endTime, 6,
                    ChronoUnit.SECONDS);
            list.forEach(pair -> {
                System.out.println(pair.getLeft()
                        .format(DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss")) + " - " + pair.getRight()
                        .format(DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss")));
            });
            System.out.println("list size : " + list.size());
        }

        {

            LocalDateTime beginTime = LocalDateTime.of(2016, 11, 30, 0, 0, 0);
            LocalDateTime endTime = LocalDateTime.of(2016, 11, 30, 0, 2, 59);
            List<Pair<LocalDateTime, LocalDateTime>> list = DateFormatUtils.splitTimePieces(beginTime, endTime, 1,
                    ChronoUnit.SECONDS);
            list.forEach(pair -> {
                System.out.println(pair.getLeft()
                        .format(DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss")) + " - " + pair.getRight()
                        .format(DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss")));
            });
            System.out.println("list size : " + list.size());
        }
    }

    /**
     * 将指定时间范围切分成时间碎片, 仅支持秒级以上时间切割
     *
     * @param beginTime   开始时间
     * @param endTime     结束时间
     * @param pieceLength 时间碎片长度
     * @param pieceUnit   时间碎片单位
     * @return 时间碎片列表
     */
    public static List<Pair<LocalDateTime, LocalDateTime>> splitTimePieces(LocalDateTime beginTime,
                                                                           LocalDateTime endTime, long pieceLength,
                                                                           ChronoUnit pieceUnit) {

        if (beginTime == null || endTime == null) {
            throw new RuntimeException("必须指定起始时间和结束时间");
        }
        if (beginTime.getNano() > 0 || endTime.getNano() > 0) {
            throw new RuntimeException("不支持高于秒级精度的时间");
        }
        if (!beginTime.isBefore(endTime)) {
            throw new RuntimeException("起始时间必须早于结束时间");
        }
        if (pieceLength <= 0) {
            throw new RuntimeException("未指定有效时间碎片长度");
        }
        if (pieceUnit == null) {
            throw new RuntimeException("未指定时间碎片单位");
        }
        if (pieceUnit.ordinal() < ChronoUnit.SECONDS.ordinal()) {
            throw new RuntimeException("仅支持秒级以上时间碎片单位");
        }

        List<Pair<LocalDateTime, LocalDateTime>> resultList = Lists.newArrayList();

        long startIndex = 0;
        long endIndex = pieceLength;

        int i = 0;
        while (true) {
            LocalDateTime pieceBeginTime = beginTime.plus(startIndex, pieceUnit);
            if (pieceBeginTime.isAfter(endTime)) {
                break;
            }

            LocalDateTime pieceEndTime = beginTime.plus(endIndex, pieceUnit);
            if (pieceEndTime.isAfter(endTime)) {
                pieceEndTime = endTime;
            } else {
                pieceEndTime = pieceEndTime.minusSeconds(1);
            }

            resultList.add(Pair.of(pieceBeginTime.withNano(0), pieceEndTime.withNano(0)));

            i++;
            startIndex = i * pieceLength;
            endIndex = (i + 1) * pieceLength;
        }

        return resultList;
    }

    /**
     * 计算周岁年龄
     *
     * @param birthday 生日日期
     * @return 年龄
     */
    public static int calculateAge(Date birthday) {

        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);

        Calendar birthdayCalendar = Calendar.getInstance();
        birthdayCalendar.setTime(birthday);
        int birthYear = birthdayCalendar.get(Calendar.YEAR);

        int diffYears = currentYear - birthYear;
        if (diffYears < 0) {
            throw new RuntimeException("输入生日日期晚于当前时间");
        }
        birthdayCalendar.add(Calendar.YEAR, diffYears);

        if (birthdayCalendar.after(calendar)) {
            // 加上年份后在当前时间之后, 说明未满周岁
            return diffYears - 1;
        }

        return diffYears;
    }

    /**
     * 是否在2个时间段范围内
     *
     * @param timeStr 时间段 格式: 23:00:00-06:00:00
     * @param date    待比较时间
     * @return boolean
     */
    public static boolean isBetweenTime(String timeStr, LocalDateTime date) {

        String[] startEnd = StringUtils.split(timeStr, "-");

        LocalTime start = LocalTime.parse(startEnd[0], DateTimeFormatter.ofPattern("HH:mm:ss"));
        LocalTime end = LocalTime.parse(startEnd[1], DateTimeFormatter.ofPattern("HH:mm:ss"));

        return date.toLocalTime()
                .isAfter(start) && date.toLocalTime()
                .isBefore(end.plusSeconds(1));
    }

    /**
     * 格式化显示中文的周几
     *
     * @param date 日期
     * @return 周几
     */
    public static String formatChineseWeekDateName(Date date) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        int weekDate = calendar.get(Calendar.DAY_OF_WEEK);

        return "周" + chineseWeekDateName[weekDate - 1];
    }

    /**
     * 时间是否在春节期间
     *
     * @param date 日期
     * @return 是否在春节期间
     */
    public static boolean isDuringSpringFestival(Date date) {

        LocalDateTime ldt = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());

        int year = ldt.getYear();
        String springFestivalPeriod = springFestivalPeriodMap.get(year);
        if (StringUtils.isEmpty(springFestivalPeriod)) {
            return false;
        }

        String[] startEnd = StringUtils.split(springFestivalPeriod, "|");

        LocalDateTime start = LocalDateTime.parse(startEnd[0], DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        LocalDateTime end = LocalDateTime.parse(startEnd[1], DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        return ldt.isAfter(start) && ldt.isBefore(end.plusSeconds(1));
    }

    /**
     * 计算当前日期与{@code endDate}的间隔天数
     *
     * @param endDate
     * @return 间隔天数
     */
    public static long until(LocalDate endDate) {

        return LocalDate.now().until(endDate, ChronoUnit.DAYS);
    }

    /**
     * 计算日期{@code startDate}与{@code endDate}的间隔天数
     *
     * @param startDate
     * @param endDate
     * @return 间隔天数
     */
    public static long until(LocalDate startDate, LocalDate endDate) {

        return startDate.until(endDate, ChronoUnit.DAYS);
    }

    /**
     * 计算得到MongoDB存储的日期，（默认情况下mongo中存储的是标准的时间，中国时间是东八区，存在mongo中少8小时，所以增加8小时）
     * http://www.iteye.com/problems/88507
     */
    public static Date getMongoDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar ca = Calendar.getInstance();
        ca.setTime(date);
        ca.add(Calendar.HOUR_OF_DAY, 8);
        return parseDateTime(sdf.format(ca.getTime()));
    }


    /**
     * 判断参数是否时间戳，如果是进行转化，如果不是返回原始值
     *
     * @param param
     * @return
     */
    public static String parseTimeOrOrigin(String param) {
        if (StringUtils.isBlank(param) || param.length() != 13) {
            return param;
        }

        try {
            long timeMillis = Long.parseLong(param);
            LocalDateTime localDateTime = LocalDateTime.ofEpochSecond(timeMillis / 1000, 0, ZoneOffset.ofHours(8));
            return localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        } catch (Exception e) {
        }
        return param;

    }
}
