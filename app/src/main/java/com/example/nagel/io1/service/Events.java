package com.example.nagel.io1.service;

public class Events {

    public static class SetState {

        private String id;
        private String val;

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

    public static class sync {}

}