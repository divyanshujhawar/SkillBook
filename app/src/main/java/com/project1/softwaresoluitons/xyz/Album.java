package com.project1.softwaresoluitons.xyz;

/**
 * Created by Lincoln on 18/05/16.
 */
public class Album {
    private String name;
    private String place;
    private String price;
    private String id;
    private String category;

    public String getId() {
        return id;
    }

    public String getCategory() {
        return category;
    }

    private int thumbnail;

    public Album() {
    }

    public Album(String id,String category,String name, String place, int thumbnail, String price) {
        this.id=id;
        this.category=category;
        this.name = name;
        this.place = place;
        this.thumbnail = thumbnail;
        this.price = price;
    }

    public String getPlace() {
        return place;
    }

    public String getPrice() {
        return price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public int getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(int thumbnail) {
        this.thumbnail = thumbnail;
    }
}
