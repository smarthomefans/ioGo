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

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import de.nisnagel.iogo.R;
import de.nisnagel.iogo.service.Constants;

public class ImageUtils {
    static public Bitmap convertToBitmap(String data) {
        if (data == null) {
            return null;
        }
        String pureBase64Encoded = data.substring(data.indexOf(",") + 1);
        byte[] decodedString = Base64.decode(pureBase64Encoded, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }

    static public int getRoleImage(String role){
        switch (role) {
            case Constants.ROLE_BUTTON:
                return R.drawable.checkbox_blank_circle;
            case Constants.ROLE_BUTTON_START:
                return R.drawable.play;
            case Constants.ROLE_BUTTON_STOP:
                return R.drawable.stop;
            case Constants.ROLE_TEXT:
                return R.drawable.text;
            case Constants.ROLE_INDICATOR_CONNECTED:
                return R.drawable.lan_connect;
            case Constants.ROLE_INDICATOR_LOWBAT:
                return R.drawable.battery_10;
            case Constants.ROLE_LEVEL_DIMMER:
                return R.drawable.lightbulb;
            case Constants.ROLE_LEVEL_BLIND:
                return R.drawable.blinds;
            case Constants.ROLE_LEVEL_TEMPERATURE:
                return R.drawable.thermometer;
            case Constants.ROLE_VALVE:
                return R.drawable.ship_wheel;
            case Constants.ROLE_LEVEL_COLOR_RED:
                return R.drawable.palette;
            case Constants.ROLE_LEVEL_COLOR_GREEN:
                return R.drawable.palette;
            case Constants.ROLE_LEVEL_COLOR_BLUE:
                return R.drawable.palette;
            case Constants.ROLE_LEVEL_COLOR_WHITE:
                return R.drawable.palette;
            case Constants.ROLE_LEVEL_COLOR_HUE:
                return R.drawable.palette;
            case Constants.ROLE_LEVEL_COLOR_SATURATION:
                return R.drawable.palette;
            case Constants.ROLE_LEVEL_COLOR_RGB:
                return R.drawable.palette;
            case Constants.ROLE_LEVEL_COLOR_LUMINANCE:
                return R.drawable.palette;
            case Constants.ROLE_LEVEL_COLOR_TEMPERATURE:
                return R.drawable.palette;
            case Constants.ROLE_LEVEL_VOLUME:
                return R.drawable.volume_high;
            case Constants.ROLE_LEVEL_CURTAIN:
                return R.drawable.blinds;
            case Constants.ROLE_LEVEL_TILT:
                return R.drawable.blinds;
            case Constants.ROLE_SENSOR_DOOR:
                return R.drawable.door;
            case Constants.ROLE_SENSOR_WINDOW:
                return R.drawable.window_closed;
            case Constants.ROLE_SENSOR_MOTION:
                return R.drawable.run_fast;
            case Constants.ROLE_SENSOR_ALARM:
                return R.drawable.alarm_light;
            case Constants.ROLE_SENSOR_ALARM_FIRE:
                return R.drawable.fire;
            case Constants.ROLE_SENSOR_ALARM_SECURE:
                return R.drawable.security_close;
            case Constants.ROLE_SENSOR_ALARM_FLOOD:
                return R.drawable.water;
            case Constants.ROLE_SENSOR_ALARM_POWER:
                return R.drawable.flash;
            case Constants.ROLE_SENSOR_LOCK:
                return R.drawable.lock;
            case Constants.ROLE_SENSOR_LIGHT:
                return R.drawable.lightbulb;
            case Constants.ROLE_SENSOR_RAIN:
                return R.drawable.weather_rainy;
            case Constants.ROLE_SWITCH:
                return R.drawable.toggle_switch;
            case Constants.ROLE_SWITCH_LIGHT:
                return R.drawable.lightbulb;
            case Constants.ROLE_VALUE_TEMPERATURE:
                return R.drawable.thermometer;
            case Constants.ROLE_VALUE_HUMIDITY:
                return R.drawable.water_percent;
            case Constants.ROLE_VALUE_BRIGHTNESS:
                return R.drawable.brightness_5;
            case Constants.ROLE_VALUE_BATTERY:
                return R.drawable.battery;
            case Constants.ROLE_VALUE_BLIND:
                return R.drawable.blinds;
        }
        return R.drawable.information;
    }

}
