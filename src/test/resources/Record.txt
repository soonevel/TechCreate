package org.example;

public class Record {
    private String balance;
    private String date;

    public Record (String balance, String date) {
        this.balance = balance;
        this.date = date;
    }

    @Override
    public String toString() {
        return "Record {balance='" + balance + "', date='" + date + "'}";
    }

    // Getters and Setters (if needed)
}