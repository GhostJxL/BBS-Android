package com.twtstudio.bbs.bdpqchen.bbs.commons.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by bdpqchen on 17-5-10.
 */

public final class StampUtil {

    private static final int diff = 0;

    public static int getDaysFromCreateToNow(int createTimeStamp) {
        if (createTimeStamp < 12721691) {
            return 0;
        }
        //由于创建时间要多8小时
        createTimeStamp -= diff;
        int currentTime = (int) (System.currentTimeMillis() / 1000);
//        LogUtil.d("current time", String.valueOf(currentTime));

        int days = (currentTime - createTimeStamp) / 86400;
//        LogUtil.d(days);
        return days + 1;    //不能从零天开始计算
    }

    private static String convert(String formatMode, int oldStamp) {
        oldStamp -= diff;
        long longTime = Long.parseLong(oldStamp + "000");
        SimpleDateFormat format = new SimpleDateFormat(formatMode);
        return format.format(new Date(longTime));
    }

    public static String getDatetimeByStamp(int postTime) {
        String datetimeMode = "yyyy-MM-dd HH:mm";
        return convert(datetimeMode, postTime + diff);
    }
    /*public static String getTimeFromNow(long time) {
        String datetimeMode = "MM-dd HH:mm:ss";
        return convert(datetimeMode, (int) (time + diff));
    }*/

    public static String getDateByStamp(int t_create) {
        String dateMode = "yyyy-MM-dd";
        return convert(dateMode, t_create);
    }

    public static String TimeStamp2Date(long timestampString) {
        Long timestamp = timestampString * 1000;
        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date(timestamp));
        return date;
    }

    public static String getTimeFromNow(int date) {
        Calendar calendar = Calendar.getInstance();
        int years = calendar.get(Calendar.YEAR);
        int months = calendar.get(Calendar.MONTH);
        int days = calendar.get(Calendar.DAY_OF_MONTH);
        int hours = calendar.get(Calendar.HOUR_OF_DAY);
        int minutes = calendar.get(Calendar.MINUTE);
        int seconds = calendar.get(Calendar.SECOND);

       /* LogUtil.dd("stamp formal", String.valueOf(calendar.getTimeInMillis()));
        LogUtil.dd("zone", String.valueOf(calendar.getTimeZone()));
        LogUtil.dd("years", String.valueOf(years));
        LogUtil.dd("months", String.valueOf(months));
        LogUtil.dd("days", String.valueOf(days));
        LogUtil.dd("hours", String.valueOf(hours));
        LogUtil.dd("minutes", String.valueOf(minutes));
        LogUtil.dd("seconds", String.valueOf(seconds));*/

        Long dateLong = Long.valueOf((date + "000"));
        calendar.setTimeInMillis(dateLong);
//        LogUtil.dd("stamp after", String.valueOf(calendar.getTimeInMillis()));

        years -= calendar.get(Calendar.YEAR);
        months -= calendar.get(Calendar.MONTH);
        days -= calendar.get(Calendar.DAY_OF_MONTH);
        hours -= calendar.get(Calendar.HOUR_OF_DAY);
        minutes -= calendar.get(Calendar.MINUTE);
        seconds -= calendar.get(Calendar.SECOND);

       /* LogUtil.dd("y", String.valueOf(years));
        LogUtil.dd("M", String.valueOf(months));
        LogUtil.dd("d", String.valueOf(days));
        LogUtil.dd("h", String.valueOf(hours));
        LogUtil.dd("m", String.valueOf(minutes));
        LogUtil.dd("s", String.valueOf(seconds));
*/
        if (years == 1 && months >= 0 || years > 1) {
            return years + "年前";
        } else if (months == 1 && days >= 0 || months > 1) {
            return months + "月前";
        } else if (days > 1) {
            return days + "天前";
        } else if (days == 1) {
            if (hours < 0) {
                hours += 24;
            }
            return hours + "小时前";
        } else if (hours > 1) {
            return hours + "小时前";
        } else if (hours == 1) {
            if (minutes < 0) {
                minutes += 60;
            }
            return minutes + "分钟前";
        } else if (minutes > 1) {
            return minutes + "分钟前";
        } else if (minutes == 1) {
            if (seconds < 0) {
                seconds += 60;
            }
            return seconds + "秒前";
        } else if (seconds > 0) {
            return seconds + " " + "秒前";
        } else {
            return "刚刚";
        }
    }
}
