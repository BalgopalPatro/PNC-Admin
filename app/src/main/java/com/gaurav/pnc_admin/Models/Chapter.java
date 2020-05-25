package com.gaurav.pnc_admin.Models;

public class Chapter {
    private int slno ;
    private String name ;
    private boolean expanded;

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    public Chapter(String name, int slno) {
        this.name = name;
        this.slno = slno;
        this.expanded = false ;
    }

    public int getSlno() {
        return slno;
    }

    public void setSlno(int slno) {
        this.slno = slno;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

