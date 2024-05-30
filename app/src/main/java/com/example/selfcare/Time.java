package com.example.selfcare;

import androidx.annotation.NonNull;

public class Time {
    private final int hours;
    private final int minutes;

    public Time(int hours, int minutes) {
        this.hours = hours;
        this.minutes = minutes;
    }

    // Checks to see if another time is further than this time
    public boolean isFurther(Time time) {
        // All private fields are set with Constructor
        int myMinutes = hours*60 + minutes;
        int otherMinutes = time.hours*60 + time.minutes;

        return myMinutes > otherMinutes;
    }

    // Converts from military time to conventional time in string form
    @NonNull
    public String toString() {
        String timeOfDay = "AM";
        String textHours;
        String textMinutes;

        if (minutes < 10)
            textMinutes = "0" + minutes;
        else
            textMinutes = minutes + "";

        if (hours > 12) {
            textHours = hours - 12 + "";
            timeOfDay = "PM";
        }
        else if (hours == 12) {
            textHours = hours + "";
            timeOfDay = "PM";
        }
        else {
            if (hours == 0)
                textHours = "12";
            else
                textHours = hours + "";
        }
        return textHours + ":" + textMinutes + " " + timeOfDay;
    }

    public int getHours() {
        return hours;
    }
    public int getMinutes() {
        return minutes;
    }
}
