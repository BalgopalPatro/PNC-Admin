package com.gaurav.pnc_admin.Models;

public class Video {

    private String code;
    private  String name;
    private int slno;

    public Video(String code, String name, int slno) {
        this.code = code;
        this.name = name;
        this.slno = slno;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSlno() {
        return slno;
    }

    public void setSlno(int slno) {
        this.slno = slno;
    }
}
