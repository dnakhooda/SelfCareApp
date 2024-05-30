package com.example.selfcare;

import android.util.Pair;

import java.util.ArrayList;

// Extends From Recommended Task. Recommended Task and Task classes will both have time, category, and title properties
public class Task extends RecommendedTask{
    // Tasks can either be ignored or checked
    enum finishCategory {
        ignored,
        checked
    }

    // When Finished Information
    private finishCategory finishedCategory;
    private boolean finished = false;
    private String finishedDate = "";
    private static int idNum = 0;
    private final int id;
    private ArrayList<Pair<String, Boolean>> daysOfWeek;

    public Task(String title, Category category, Time time, ArrayList<Pair<String, Boolean>> daysOfWeek) {
        super(time, title, category);
        id = idNum;
        idNum++;
        this.daysOfWeek = daysOfWeek;
    }

    // Two Methods to change Finished Values
    public void finish(finishCategory finishCategory, String finishedDate) {
        finishedCategory = finishCategory;
        this.finishedDate = finishedDate;
        finished = true;
    }

    public void unFinish() {
        finishedCategory = null;
        this.finishedDate = "";
        finished = false;
    }

    // Returns if the task is on a day of the week
    public boolean doOnDayOfTheWeek(String dayOfWeek) {
        for (int i = 0; i < daysOfWeek.size(); i++) {
            if (dayOfWeek.equalsIgnoreCase(daysOfWeek.get(i).first.toLowerCase()))
                return daysOfWeek.get(i).second;
        }
        return false;
    }

    public boolean isFinished() {
        return finished;
    }
    public finishCategory getFinishedCategory() {
        return finishedCategory;
    }
    public int getId() {
        return id;
    }
    public String getFinishedDate() {
        return finishedDate;
    }
    public ArrayList<Pair<String, Boolean>> getDaysOfWeek() { return daysOfWeek; }
    public void setDaysOfWeek(ArrayList<Pair<String, Boolean>> daysOfWeek) { this.daysOfWeek = daysOfWeek; }
}
