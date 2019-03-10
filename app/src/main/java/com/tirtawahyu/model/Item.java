package com.tirtawahyu.model;

public class Item {
    private String itemId;
    private String ticketType;
    private int price;

    public Item(String ticketId, String ticketType, int price) {
        this.itemId = ticketId;
        this.ticketType = ticketType;
        this.price = price;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getTicketType() {
        return ticketType;
    }

    public void setTicketType(String ticketType) {
        this.ticketType = ticketType;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}
