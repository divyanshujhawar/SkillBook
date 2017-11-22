package com.project1.softwaresoluitons.xyz;

/**
 * Created by Divyanshu on 06-10-2017.
 */

public class Training {
    String name;
    String userId;
    String location;
     String price;
    String duration;
     String description;
    String category;
    String availability;
    String keyLearining1, keyLearining2, keyLearining3;
    String date;
    String id;

    public Training() {
    }

    public Training(String id, String name, String userId, String location, String price, String duration, String description, String category,
                    String availability, String keyLearining1, String keyLearining2, String keyLearining3, String date) {
        this.id = id;
        this.name = name;
        this.userId = userId;
        this.location = location;
        this.price = price;
        this.duration = duration;
        this.description = description;
        this.category = category;
        this.availability = availability;
        this.keyLearining1 = keyLearining1;
        this.keyLearining2 = keyLearining2;
        this.keyLearining3 = keyLearining3;
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public String getUserId() {
        return userId;
    }

    public String getId() {
        return id;
    }

    public String getLocation() {
        return location;
    }

    public String getPrice() {
        return price;
    }

    public String getDuration() {
        return duration;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }

    public String getAvailability() {
        return availability;
    }

    public String getKeyLearining1() {
        return keyLearining1;
    }

    public String getKeyLearining2() {
        return keyLearining2;
    }

    public String getKeyLearining3() {
        return keyLearining3;
    }

    public String getDate() {
        return date;
    }
}
