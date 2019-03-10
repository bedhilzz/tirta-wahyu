package com.tirtawahyu.model;

/**
 * Created by Fadhil on 31-Oct-18.
 */

public class Ticket {
    private String ticketId;
    private String tipe;
    private int jumlah;
    private int total;

    public Ticket(String ticketId, String tipe, int jumlah, int total) {
        this.ticketId = ticketId;
        this.tipe = tipe;
        this.jumlah = jumlah;
        this.total = total;
    }

    public String getTicketId() {
        return ticketId;
    }

    public void setTicketId(String ticketId) {
        this.ticketId = ticketId;
    }

    public String getTipe() {
        return tipe;
    }

    public void setTipe(String tipe) {
        this.tipe = tipe;
    }

    public int getJumlah() {
        return jumlah;
    }

    public void setJumlah(int jumlah) {
        this.jumlah = jumlah;
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
