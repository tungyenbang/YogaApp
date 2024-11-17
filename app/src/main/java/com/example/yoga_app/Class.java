package com.example.yoga_app;

public class Class {
    private int courseId;
    private int id;
    private String teacher;
    private String date;
    private String comment;



    // Constructor không cần ID
    public Class(String comment, String teacher, String date, int courseId) {
        this.courseId = courseId;
        this.date = date;
        this.teacher = teacher;
        this.comment = comment;
    }

    public Class() {

    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    // Getters
    public int getCourseId() {
        return courseId; // ID sẽ được lấy sau khi chèn vào database
    }

    public int getId() {
        return id;
    }

    public void setCourseId(int classId) {
        this.courseId = classId;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}

