package com.gaurav.pnc;

public class Subject {
    private String name,img;

    public Subject(String name, String img) {
        this.name = name;
        this.img = img;
    }

    public Subject() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }
}
