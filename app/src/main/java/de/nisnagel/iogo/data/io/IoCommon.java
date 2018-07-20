package de.nisnagel.iogo.data.io;

import java.util.List;
import java.util.Map;

public class IoCommon {

    private IoName name;
    private String type;
    private String role;
    private boolean read;
    private boolean write;
    private String unit;
    private String def;
    private String desc;

    private List<String> members = null;

    private Map<String,String> states;

    public String getName() {
        return name.toString();
    }

    public void setName(IoName name) {
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

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public boolean isWrite() {
        return write;
    }

    public void setWrite(boolean write) {
        this.write = write;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getDef() {
        return def;
    }

    public void setDef(String def) {
        this.def = def;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
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