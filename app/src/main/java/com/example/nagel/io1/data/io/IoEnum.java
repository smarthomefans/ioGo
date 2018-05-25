package com.example.nagel.io1.data.io;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class IoEnum {

    @SerializedName("rows")
    @Expose
    private List<Row> rows = null;

    public List<Row> getRows() {
        return rows;
    }

    public void setRows(List<Row> rows) {
        this.rows = rows;
    }

}
