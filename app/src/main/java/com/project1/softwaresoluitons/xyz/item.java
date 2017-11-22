package com.project1.softwaresoluitons.xyz;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

/**
 * Created by root on 11/5/17.
 */
public class item {
        String title;
        String location;
        String price;
        String contact;
        Bitmap b;
        String category;
        String id;


    public item() {
    }

    item(String id, String title, String location, String price, Bitmap b, String cat) {
            this.title=title;
            this.location=location;
            this.price=price;
            this.contact="NULL";
            this.b = b;
            this.category=cat;
            Log.i("b",b+"");
            this.id = id;
        }

    public String getTitle() {
        return title;
    }

    public String getLocation() {
        return location;
    }

    public String getPrice() {
        return price;
    }

    public String getContact() {
        return contact;
    }

    public Bitmap getB() {
        return b;
    }

    public String getCategory() {
        return category;
    }

    public String getId() {
        return id;
    }

    public void setB(Bitmap b) {
        this.b = b;
    }
}
