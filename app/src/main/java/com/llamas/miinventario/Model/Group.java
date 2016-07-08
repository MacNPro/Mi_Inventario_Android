package com.llamas.miinventario.Model;

import java.util.ArrayList;

public class Group {

    public String groupId;
    public String groupName;
    public ArrayList<Item> childrens;

    public Group(String groupId, String groupName,
                 ArrayList<Item> childrens) {
        super();
        this.groupId = groupId;
        this.groupName = groupName;
        this.childrens = childrens;
    }

    public String getGroupId() {
        return groupId;
    }
    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
    public String getGroupName() {
        return groupName;
    }
    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
    public ArrayList<Item> getChildrens() {
        return childrens;
    }
    public void setChildrens(ArrayList<Item> childrens) {
        this.childrens = childrens;
    }

}
