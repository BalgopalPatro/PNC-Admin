package com.gaurav.pnc_admin.Models;

public class Course_list_model {
    private String course;

    public Course_list_model() {
    }

    public Course_list_model(String course) {
        this.course = course;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }
}
