package com.tirtawahyu.model;

/**
 * Created by Fadhil on 31-Oct-18.
 */

public class Ticket {
    private String ticketId;
    private String ticketType;
    private int quantity;
    private int total;

    public Ticket(String ticketId, String tipe, int jumlah, int total) {
        this.ticketId = ticketId;
        this.ticketType = tipe;
        this.quantity = jumlah;
        this.total = total;
    }

    public String getTicketId() {
        return ticketId;
    }

    public void setTicketId(String ticketId) {
        this.ticketId = ticketId;
    }

    public String getTicketType() {
        return ticketType;
    }

    public void setTicketType(String ticketType) {
        this.ticketType = ticketType;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    @Override
    public boolean equals(Object obj) {
        Ticket other = (Ticket) obj;
        return ticketId.equals(other.ticketId);
    }
}
