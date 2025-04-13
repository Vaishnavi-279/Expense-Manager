package com.example.expensemanager;

public class Transaction {
    private int id;
    private int userId;
    private float amount;
    private String description;
    private long timestamp;
    private String type; // "income" or "expense"

    public Transaction(int userId, float amount, String description, long timestamp, String type) {
        this.userId = userId;
        this.amount = amount;
        this.description = description;
        this.timestamp = timestamp;
        this.type = type;
    }

    public Transaction(int id, int userId, float amount, String description, long timestamp, String type) {
        this.id = id;
        this.userId = userId;
        this.amount = amount;
        this.description = description;
        this.timestamp = timestamp;
        this.type = type;
    }

    // Getters and Setters

    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public float getAmount() {
        return amount;
    }

    public String getDescription() {
        return description;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getType() {
        return type;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void setType(String type) {
        this.type = type;
    }
}
