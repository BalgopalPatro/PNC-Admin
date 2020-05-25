package com.gaurav.pnc_admin.Models;

public class Chapter {
    private String name ;
    private boolean expanded;

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    public Chapter(String name) {
        this.name = name;
        this.expanded = false ;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
