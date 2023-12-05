package com.example.firebasedatabase;

public class Product {
    String key, name, price, quantity;

    public Product() {

    }

    public String getName() {
        return name;
    }

    public String getPrice() {
        return price;
    }

    public String getKey() {
        return key;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }
}