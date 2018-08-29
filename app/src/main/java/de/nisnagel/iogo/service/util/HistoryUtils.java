package de.nisnagel.iogo.service.util;

import java.util.Calendar;

public class HistoryUtils {
    private static long durDay = 86400;
    private static long durWeek = durDay * 7;
    private static long durMonth = durDay * 31;
    private static long durYear = durDay * 365;
    static public long endOfDay = getEndOfToday();
    static public long startOfDay = endOfDay - durDay;
    static public long startOfWeek = endOfDay -HistoryUtils.durWeek;
    static public long startOfMonth = endOfDay -HistoryUtils.durMonth;
    static public long startOfYear = endOfDay - HistoryUtils.durYear;

    static public long getEndOfToday() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return (calendar.getTimeInMillis() / 1000) + durDay;
    }

}
