package com.llamas.miinventario.Model;

/**
 * Created by MacNPro on 7/7/16.
 */
public class Item {

    public String chiledId;
    public String childName;

    public Item(String chiledId, String childName) {
        super();
        this.chiledId = chiledId;
        this.childName = childName;
    }

    public String getChiledId() {
        return chiledId;
    }

    public void setChiledId(String chiledId) {
        this.chiledId = chiledId;
    }

    public String getChildName() {
        return childName;
    }

    public void setChildName(String childName) {
        this.childName = childName;
    }

}
