package com.example.scamarmor;

public class Comment {
    private long id;
    private String comment;

    public Comment(long id, String comment) {
        this.id = id;
        this.comment = comment;
    }

    public long getId() {
        return id;
    }

    public String getComment() {
        return comment;
    }

    @Override
    public String toString() {
        return comment;
    }
}
