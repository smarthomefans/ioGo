package de.nisnagel.iogo.data.io;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;

public class IoCommon {

    @SerializedName("name")
    @Expose
    private String name;

    private String type;
    private String role;
    private boolean read;
    private boolean write;
    private String unit;
    private String def;
    private String desc;

    @SerializedName("members")
    @Expose
    private List<String> members = null;

    @SerializedName("states")
    @Expose
    private Map<String,String> states;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean getRead() {
        return read;
    }

    public boolean getWrite() {
        return write;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public List<String> getMembers() {
        return members;
    }

    public void setMembers(List<String> members) {
        this.members = members;
    }

    public Map<String, String> getStates() {
        return states;
    }

    public void setStates(Map<String, String> states) {
        this.states = states;
    }
}