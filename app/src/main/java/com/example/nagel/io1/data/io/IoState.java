package com.example.nagel.io1.data.io;

public class IoState {
    private String val;
    private boolean ack;
    private long ts;
    private long lc;
    private String from;
    private int q;

    public String getVal() {
        return val;
    }

    public void setVal(String val) {
        this.val = val;
    }

    public boolean isAck() {
        return ack;
    }

    public void setAck(boolean ack) {
        this.ack = ack;
    }

    public long getTs() {
        return ts;
    }

    public void setTs(long ts) {
        this.ts = ts;
    }

    public long getLc() {
        return lc;
    }

    public void setLc(long lc) {
        this.lc = lc;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public int getQ() {
        return q;
    }

    public void setQ(int q) {
        this.q = q;
    }
}
