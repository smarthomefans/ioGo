package com.example.nagel.io1.data.io;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class IoEnum {

    @SerializedName("rows")
    @Expose
    private List<IoRow> ioRows = null;

    public List<IoRow> getRows() {
        return ioRows;
    }

    public void setIoRows(List<IoRow> ioRows) {
        this.ioRows = ioRows;
    }

}
