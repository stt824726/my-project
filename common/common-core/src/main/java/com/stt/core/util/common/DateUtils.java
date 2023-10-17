package com.stt.core.util.common;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.lang.management.ManagementFactory;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@UtilityClass
@Slf4j
public class DateUtils extends org.apache.commons.lang3.time.DateUtils{

    public static String YYYY = "yyyy";

    public static String YYYY_MM = "yyyy-MM";

    public static String YYYY_MM_DD = "yyyy-MM-dd";

    public static String DATE = "yyyyMMdd";
    public static String TIME = "HHmmss";
    public static String YYYYMMDDHHMMSS = DATE+TIME;
    public static String DATE_TIME = DATE+TIME;

    public static String YYYYMMDDHHMMSSSSS = "yyyyMMddHHmmssSSS";

    public static String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";

    private static String[] parsePatterns = { "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", "yyyy-MM",
            "yyyy/MM/dd", "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm", "yyyy/MM", "yyyy.MM.dd", "yyyy.MM.dd HH:mm:ss",
            "yyyy.MM.dd HH:mm", "yyyy.MM" };

    /**
     * 获取当前Date型日期
     *
     * @return Date() 当前日期
     */
    public static Date getNowDate() {
        return new Date();
    }

    /**
     * 获取当前日期, 默认格式为yyyy-MM-dd
     *
     * @return String
     */
    public static String getDate() {
        return dateTimeNow(YYYY_MM_DD);
    }

    public static final String getTime() {
        return dateTimeNow(YYYY_MM_DD_HH_MM_SS);
    }

    public static final String dateTimeNow() {
        return dateTimeNow(YYYYMMDDHHMMSS);
    }

    public static final String dateTimeNow(final String format) {
        return parseDateToStr(format, new Date());
    }

    public static final String dateTime(final Date date) {
        return parseDateToStr(YYYY_MM_DD, date);
    }

    public static final String parseDateToStr(final String format, final Date date) {
        return new SimpleDateFormat(format).format(date);
    }

    public static final Date dateTime(final String format, final String ts) {
        try {
            return new SimpleDateFormat(format).parse(ts);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 日期路径 即年/月/日 如2018/08/08
     */
    public static final String datePath() {
        Date now = new Date();
        return DateFormatUtils.format(now, "yyyy/MM/dd");
    }

    /**
     * 日期路径 即年/月/日 如20180808
     */
    public static final String dateTime() {
        Date now = new Date();
        return DateFormatUtils.format(now, "yyyyMMdd");
    }

    /**
     * 日期型字符串转化为日期 格式
     */
    public static Date parseDate(Object str) {
        if (str == null) {
            return null;
        }
        try {
            return parseDate(str.toString(), parsePatterns);
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * 获取服务器启动时间
     */
    public static Date getServerStartDate() {
        long time = ManagementFactory.getRuntimeMXBean().getStartTime();
        return new Date(time);
    }

    /**
     * 计算两个时间差
     */
    public static String getDatePoor(Date endDate, Date nowDate) {
        long nd = 1000 * 24 * 60 * 60;
        long nh = 1000 * 60 * 60;
        long nm = 1000 * 60;
        // long ns = 1000;
        // 获得两个时间的毫秒时间差异
        long diff = endDate.getTime() - nowDate.getTime();
        // 计算差多少天
        long day = diff / nd;
        // 计算差多少小时
        long hour = diff % nd / nh;
        // 计算差多少分钟
        long min = diff % nd % nh / nm;
        // 计算差多少秒//输出结果
        // long sec = diff % nd % nh % nm / ns;
        return day + "天" + hour + "小时" + min + "分钟";
    }

    /**
     * 获取上一天日期
     *
     * @return
     */
    public static String getPerDate() {
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, -1);
        return dateTime(calendar.getTime());
    }

    /**
     * 获取上一天日期
     *
     * @return
     */
    public static String getPerDate2() {
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, -1);
        return DateFormatUtils.format(calendar.getTime(), "yyyyMMdd");
    }

    public static List<String> getDays() {
        // 返回的日期集合
        List<String> days = new ArrayList<String>();
        try {
            // 日期格式转换
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
            // 当前日期
            Calendar instance = Calendar.getInstance();
            // 调整到上周
            instance.add(Calendar.WEDNESDAY, -1);
            // 调整到上周1
            instance.set(Calendar.DAY_OF_WEEK, 6);
            // 循环打印
            for (int i = 1; i <= 3; i++) {
                days.add(format.format(instance.getTime()));
                instance.add(Calendar.DAY_OF_WEEK, 1);
            }
        } catch (Exception e) {
            log.error("getDays error",e);
        }
        return days;
    }

    public static boolean isMonday() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        int week = cal.get(Calendar.DAY_OF_WEEK) - 1;
        return week == 1 ? true : false;
    }

    public static List<String> getDateList(String startDate, String endxDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        int calendarType = Calendar.DATE;
        List<String> result = new ArrayList<>();
        Calendar min = Calendar.getInstance();
        Calendar max = Calendar.getInstance();
        try {
            min.setTime(sdf.parse(startDate));
            min.add(calendarType, 0);
            max.setTime(sdf.parse(endxDate));
            max.add(calendarType, 1);
        } catch (ParseException e) {
            log.error("getDateList error",e);
        }
        Calendar curr = min;
        while (curr.before(max)) {
            result.add(sdf.format(min.getTime()));
            curr.add(calendarType, 1);
        }
        return result;
    }
}
