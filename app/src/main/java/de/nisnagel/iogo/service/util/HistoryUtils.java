/*
 * ioGo - android app to control ioBroker home automation server.
 *
 * Copyright (C) 2018  Nis Nagel
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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

    static private long getEndOfToday() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return (calendar.getTimeInMillis() / 1000) + durDay;
    }

}
