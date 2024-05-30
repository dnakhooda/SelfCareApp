package com.example.selfcare;

public class RecommendedTask {
    // Different Categories for Tasks
    enum Category {
        Medical,
        Hygiene,
        Vitamins,
        Exercise,
        Mind,
    }

    private Time time;
    private String title;
    private Category category;

    public RecommendedTask(Time time, String title, Category category) {
        // All private fields are set with Constructor
        this.time = time;
        this.title = title;
        this.category = category;
    }

    public Time getTime() {
        return time;
    }

    public String getTitle() {
        return title;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setTime(Time time) {
        this.time = time;
    }
}
