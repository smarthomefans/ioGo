package de.nisnagel.iogo.data.io;

import com.google.gson.annotations.SerializedName;

public class IoCustom {

    @SerializedName("sql.0")
    private IoCustomAdapter sql0;
    @SerializedName("history.0")
    private IoCustomAdapter history0;

    public IoCustomAdapter getSql0() {
        return sql0;
    }

    public void setSql0(IoCustomAdapter sql0) {
        this.sql0 = sql0;
    }

    public IoCustomAdapter getHistory0() {
        return history0;
    }

    public void setHistory0(IoCustomAdapter history0) {
        this.history0 = history0;
    }
}
