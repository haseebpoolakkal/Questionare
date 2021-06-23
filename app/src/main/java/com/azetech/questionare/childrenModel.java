package com.azetech.questionare;

public class childrenModel {
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public childrenModel() {
    }

    String uid;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public childrenModel(String uid, String name, String parent, String category) {
        this.uid = uid;
        this.name = name;
        this.parent = parent;
        this.category = category;
    }
    String name;
    String parent;
    String category;
}
