package com.azetech.questionare;

public class resultModel {
    String name;
    String mark;
    String date;
    String childName;
    String parent;

    public String getChildName() {
        return childName;
    }

    public void setChildName(String childName) {
        this.childName = childName;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }

    public String getDate() {
        return date;
    }

    public String getParent() {
        return parent;
    }

    public resultModel(String name, String mark, String date, String childName, String parent) {
        this.name = name;
        this.mark = mark;
        this.date = date;
        this.childName = childName;
        this.parent = parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public resultModel() {
    }


}
