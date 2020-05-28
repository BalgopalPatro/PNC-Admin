package com.gaurav.pnc_admin.Models;

public class StudyOrAssign {
    private String name;
    private String url;
    private int slno;

    public StudyOrAssign(String name, String url, int slno) {
        this.name = name;
        this.url = url;
        this.slno = slno;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getSlno() {
        return slno;
    }

    public void setSlno(int slno) {
        this.slno = slno;
    }
}
