package com.project1.softwaresoluitons.xyz;

/**
 * Created by Divyanshu on 06-10-2017.
 */

public class Category {
    String name;
    String id;

    public Category() {
    }

    public Category(String id , String category) {
        this.name = category;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }
}
