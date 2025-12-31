// In model/OMenuItem.java
package com.simats.foodstall.model;
import com.google.gson.annotations.SerializedName;
public class OMenuItem {
    @SerializedName("item_id") private int itemId;
    @SerializedName("item_name") private String name;
    @SerializedName("item_price") private double price;
    @SerializedName("item_category") private String category;
    @SerializedName("item_image") private String itemImage; // ADD THIS
    public int getItemId(){return itemId;}
    public String getName(){return name;}
    public void setName(String name){this.name = name;}
    public double getPrice(){return price;}
    public void setPrice(double price){this.price = price;}
    public String getCategory(){return category;}
    public void setCategory(String category){this.category = category;}
    public String getItemImage(){return itemImage;} // ADD THIS
}