package com.gaurav.pnc.Models;

public class User_info {
    private String name, phone, membership, designation, info;
    private String id;

    public User_info() {
    }

    public User_info(String name, String phone, String membership, String designation, String info, String id) {
        this.name = name;
        this.phone = phone;
        this.membership = membership;
        this.designation = designation;
        this.info = info;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getMembership() {
        return membership;
    }

    public void setMembership(String membership) {
        this.membership = membership;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
