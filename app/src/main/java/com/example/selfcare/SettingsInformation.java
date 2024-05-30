package com.example.selfcare;

public class SettingsInformation {

    private boolean giveNotifications;
    private boolean giveEndOfDayNotifications;
    private Time endOfDayNotificationsTime;
    private String hasGivenDailyInfo;

    public SettingsInformation(boolean giveNotifications, boolean giveEndOfDayNotifications, Time endOfDayNotificationsTime, String hasGivenDailyInfo) {
        // All private fields are set with Constructor
        this.giveNotifications = giveNotifications;
        this.giveEndOfDayNotifications = giveEndOfDayNotifications;
        this.endOfDayNotificationsTime = endOfDayNotificationsTime;
        this.hasGivenDailyInfo = hasGivenDailyInfo;
    }

    public boolean getGiveNotifications() {
        return giveNotifications;
    }

    public void setGiveNotifications(boolean giveNotifications) {
        this.giveNotifications = giveNotifications;
    }

    public boolean getGiveEndOfDayNotifications() {
        return giveEndOfDayNotifications;
    }

    public void setGiveEndOfDayNotifications(boolean giveEndOfDayNotifications) {
        this.giveEndOfDayNotifications = giveEndOfDayNotifications;
    }

    public Time getEndOfDayNotificationsTime() {
        return endOfDayNotificationsTime;
    }

    public void setEndOfDayNotificationsTime(Time endOfDayNotificationsTime) {
        this.endOfDayNotificationsTime = endOfDayNotificationsTime;
    }

    public String getHasGivenDailyInfo() {
        return hasGivenDailyInfo;
    }

    public void setHasGivenDailyInfo(String hasGivenDailyInfo) {
        this.hasGivenDailyInfo = hasGivenDailyInfo;
    }
}
