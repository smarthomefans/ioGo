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

public class Events {

    public static class SetState {

        private String id;
        private String val;

        public SetState(String id, String val) {
            this.id = id;
            this.val = val;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getVal() {
            return val;
        }

        public void setVal(String val) {
            this.val = val;
        }

    }

    public static class SyncObjects {}

    public static class LoadHistory {
        public String id;

        public LoadHistory(String id){
            this.id = id;
        }
    }

    public static class User {
        public String name;
        public String token;

        public User(String name, String token){
            this.name = name;
            this.token = token;
        }
    }
}