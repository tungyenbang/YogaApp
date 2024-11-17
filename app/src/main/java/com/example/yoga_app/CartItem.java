package com.example.yoga_app;

import java.util.ArrayList;
import java.util.List;

public class CartItem {
    private Long classId;
    private String comment;
    private String date;
    private String duration;
    private String price;
    private String teacher;
    private String time;
    private String type;

    // Các thuộc tính mới
    private int quantity;  // Số lượng
    private List<String> keys;  // Danh sách keys

    // Constructor mặc định
    public CartItem() {
        this.keys = new ArrayList<>();
    }

    // Constructor có tham số
    public CartItem(Long classId, String comment, String date, String duration, String price, String teacher, String time, String type) {
        this.classId = classId;
        this.comment = comment;
        this.date = date;
        this.duration = duration;
        this.price = price;
        this.teacher = teacher;
        this.time = time;
        this.type = type;
        this.keys = new ArrayList<>();
        this.quantity = 1;  // Mặc định số lượng là 1
    }

    // Getter và Setter cho các thuộc tính
    public Long getClassId() { return classId; }
    public void setClassId(Long classId) { this.classId = classId; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getDuration() { return duration; }
    public void setDuration(String duration) { this.duration = duration; }

    public String getPrice() { return price; }
    public void setPrice(String price) { this.price = price; }

    public String getTeacher() { return teacher; }
    public void setTeacher(String teacher) { this.teacher = teacher; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public List<String> getKeys() { return keys; }
    public void setKeys(List<String> keys) { this.keys = keys; }

    public void addKey(String key) {
        if (!keys.contains(key)) {
            keys.add(key);
        }
    }
}
