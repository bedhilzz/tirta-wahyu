package com.tirtawahyu;

/**
 * Created by Fadhil on 31-Oct-18.
 */

public class Ticket {
    private String tipe;
    private int jumlah;
    private int total;

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
}
