package com.project1.softwaresoluitons.xyz;

/**
 * Created by Divyanshu on 05-10-2017.
 */

public class User {

    public String fname,lname,location,mobile,email,id;

    public User() {
    }

    public User(String fname, String lname, String location, String mobile, String email, String id) {
        this.fname = fname;
        this.lname = lname;
        this.location = location;
        this.mobile = mobile;
        this.email = email;
        this.id = id;
    }

    public String getFname() {
        return fname;
    }

    public String getLname() {
        return lname;
    }

    public String getLocation() {
        return location;
    }

    public String getMobile() {
        return mobile;
    }

    public String getEmail() {
        return email;
    }

    public String getId() {
        return id;
    }
}
