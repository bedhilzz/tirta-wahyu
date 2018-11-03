package com.tirtawahyu.model;

import java.util.Date;

public class Receipt {
    private int umum;
    private int member;
    private int freePass;
    private int total;
    private Date createdAt;

    public Receipt() {}

    public Receipt(int umum, int member, int freePass, int total, Date createdAt) {
        this.umum = umum;
        this.member = member;
        this.freePass = freePass;
        this.total= total;
        this.createdAt = createdAt;
    }

    public int getUmum() {
        return umum;
    }

    public int getMember() {
        return member;
    }

    public int getFreePass() {
        return freePass;
    }

    public int getTotal() {
        return total;
    }

    public Date getCreatedAt() {
        return createdAt;
    }
}
