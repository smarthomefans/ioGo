package de.nisnagel.iogo.data.io;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class IoAcl {

    @SerializedName("object")
    @Expose
    private long object;
    @SerializedName("owner")
    @Expose
    private String owner;
    @SerializedName("ownerGroup")
    @Expose
    private String ownerGroup;

    public long getObject() {
        return object;
    }

    public void setObject(long object) {
        this.object = object;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getOwnerGroup() {
        return ownerGroup;
    }

    public void setOwnerGroup(String ownerGroup) {
        this.ownerGroup = ownerGroup;
    }

}