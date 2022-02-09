package com.jimmy.lighthouse.server.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Jimmy mailto:835490943@qq.com
 * @version 1.0.0
 * @since 2022-02-08
 */
public class CommonUtil {


    private static final String DATE_PATTERN = "yyyy-MM-dd hh:mm:ss";

    public static Date convertMillsToDate(Long timeInMills) {
        Date date = new Date();
        date.setTime(timeInMills);
        return date;
    }

    public static Long convertDateToMills(Date date) {
        return date.getTime();
    }
}
