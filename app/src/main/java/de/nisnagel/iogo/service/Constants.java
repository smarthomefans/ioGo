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

package de.nisnagel.iogo.service;

public class Constants {
    //Common
    public static final String ROLE_TEXT = "text";

    //Sensor
    public static final String ROLE_SENSOR = "sensor";
    public static final String ROLE_SENSOR_DOOR = "sensor.door";
    public static final String ROLE_SENSOR_WINDOW = "sensor.window";
    public static final String ROLE_SENSOR_MOTION = "sensor.motion";
    public static final String ROLE_SENSOR_ALARM = "sensor.alarm";
    public static final String ROLE_SENSOR_ALARM_FIRE = "sensor.alarm.fire";
    public static final String ROLE_SENSOR_ALARM_FLOOD = "sensor.alarm.flood";
    public static final String ROLE_SENSOR_ALARM_SECURE = "sensor.alarm.secure";
    public static final String ROLE_SENSOR_ALARM_POWER = "sensor.alarm.power";
    public static final String ROLE_SENSOR_LOCK = "sensor.lock";
    public static final String ROLE_SENSOR_LIGHT = "sensor.light";
    public static final String ROLE_SENSOR_RAIN = "sensor.rain";

    //Button
    public static final String ROLE_BUTTON = "button";
    public static final String ROLE_BUTTON_START = "button.start";
    public static final String ROLE_BUTTON_STOP = "button.stop";

    //Value
    public static final String ROLE_VALUE = "value";
    public static final String ROLE_VALUE_TEMPERATURE = "value.temperature";
    public static final String ROLE_VALUE_HUMIDITY = "value.humidity";
    public static final String ROLE_VALUE_BRIGHTNESS = "value.brightness";
    public static final String ROLE_VALUE_BATTERY = "value.battery";
    public static final String ROLE_VALUE_BLIND = "value.blind";
    public static final String ROLE_VALUE_WINDOW = "value.window";

    //Indicator
    public static final String ROLE_INDICATOR_LOWBAT = "indicator.lowbat";
    public static final String ROLE_INDICATOR_CONNECTED = "indicator.connected";
    public static final String ROLE_INDICATOR_MOTION = "indicator.motion";

    //Level
    public static final String ROLE_LEVEL_DIMMER = "level.dimmer";
    public static final String ROLE_LEVEL_BLIND = "level.blind";
    public static final String ROLE_LEVEL_TEMPERATURE = "level.temperature";
    public static final String ROLE_LEVEL_VALVE = "level.valve";
    public static final String ROLE_LEVEL_COLOR_RED = "level.color.red";
    public static final String ROLE_LEVEL_COLOR_GREEN = "level.color.green";
    public static final String ROLE_LEVEL_COLOR_BLUE = "level.color.blue";
    public static final String ROLE_LEVEL_COLOR_WHITE = "level.color.white";
    public static final String ROLE_LEVEL_COLOR_HUE = "level.color.hue";
    public static final String ROLE_LEVEL_COLOR_SATURATION = "level.color.saturation";
    public static final String ROLE_LEVEL_COLOR_RGB = "level.color.rgb";
    public static final String ROLE_LEVEL_COLOR_LUMINANCE = "level.color.luminance";
    public static final String ROLE_LEVEL_COLOR_TEMPERATURE = "level.color.temperature";
    public static final String ROLE_LEVEL_VOLUME = "level.volume";
    public static final String ROLE_LEVEL_CURTAIN = "level.curtain";
    public static final String ROLE_LEVEL_TILT = "level.tilt";

    //Switch
    public static final String ROLE_SWITCH = "switch";
    public static final String ROLE_SWITCH_LIGHT = "switch.light";

    public static final String ARG_ENUM_ID = "enum_id";
    public static final String ARG_STATE_ID = "state_id";
    public static final String ARG_CLASS = "class";
}
