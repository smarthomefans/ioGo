package de.nisnagel.iogo.data.io;

public class IoAcl {

    private long object;
    private String owner;
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