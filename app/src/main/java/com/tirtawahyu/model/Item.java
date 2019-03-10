package com.tirtawahyu.model;

public class Item {
    private String itemId;
    private String tipe;
    private int price;

    public Item(String ticketId, String tipe, int price) {
        this.itemId = ticketId;
        this.tipe = tipe;
        this.price = price;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getTipe() {
        return tipe;
    }

    public void setTipe(String tipe) {
        this.tipe = tipe;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}
