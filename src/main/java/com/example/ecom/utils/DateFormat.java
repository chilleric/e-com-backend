package com.example.ecom.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.example.ecom.exception.InvalidDateFormat;

public class DateFormat {
    public static Date getCurrentTime() {
        Date date = java.util.Calendar.getInstance().getTime();
        return date;
    }

    public static String toDateString(Date date, String format) {
        if (date == null)
            return "";
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        String result = null;
        try {
            result = sdf.format(date);
        } catch (Exception e) {
            throw new InvalidDateFormat("Wrong date format. Date format must be " + format);
        }
        return result;
    }
}