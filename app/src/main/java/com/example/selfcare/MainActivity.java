package com.example.selfcare;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.util.Pair;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    public static MainActivity instance;
    private BottomNavigationView navigation;
    private Fragment currentFragment;
    private ArrayList<Task> tasks = new ArrayList<>();
    private SettingsInformation settingsInformation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        instance = this;

        // Setting Up Navigation
        navigation = findViewById(R.id.NavigationBar);
        navigation.setOnNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.displayItemNav) {
                if (currentFragment instanceof Display)
                    return true;
                moveToDifferentFragment(new Display());
            }
            else if (item.getItemId() == R.id.editItemNav) {
                if (currentFragment instanceof Edit)
                    return true;
                moveToDifferentFragment(new Edit());
            }
            else if (item.getItemId() == R.id.recommendedItemNav) {
                if (currentFragment instanceof Recommended)
                    return true;
                moveToDifferentFragment(new Recommended());
            }
            else if (item.getItemId() == R.id.settingsItemNav) {
                if (currentFragment instanceof Settings)
                    return true;
                moveToDifferentFragment(new Settings());
            }
            return true;
        });

        // Move to Display Fragment
        moveToDifferentFragment(new Display());

        // Get and Sort Tasks
        tasks = getSavedTasksInformation();
        sortTasks(tasks);

        // Set Notifications
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel dailyMessage = new NotificationChannel("dailyMessage", "Daily Message", NotificationManager.IMPORTANCE_HIGH);
            NotificationChannel taskMessages = new NotificationChannel("taskMessages", "Task Messages", NotificationManager.IMPORTANCE_HIGH);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(dailyMessage);
            notificationManager.createNotificationChannel(taskMessages);
        }

        // Set up Settings
        settingsInformation = getSavedSettingsInformation();
        if (settingsInformation.getGiveNotifications())
            addNotifications();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // UnFinish tasks for a new day
        for (int i = 0; i < tasks.size(); i++) {
            if (!tasks.get(i).getFinishedDate().equals("" + Calendar.getInstance().get(Calendar.YEAR) + Calendar.getInstance().get(Calendar.MONTH) + Calendar.getInstance().get(Calendar.DATE)))
                tasks.get(i).unFinish();
        }

        // Do and Set Daily Notification
        if (!settingsInformation.getHasGivenDailyInfo().equals(""+Calendar.getInstance().get(Calendar.YEAR)+Calendar.getInstance().get(Calendar.MONTH)+Calendar.getInstance().get(Calendar.DATE)))
            settingsInformation.setHasGivenDailyInfo("false");
        if (settingsInformation.getGiveEndOfDayNotifications() && settingsInformation.getHasGivenDailyInfo().equals("false")) {
            // Determine if it is the right time to give daily notification
            createDailyNotification();
            Calendar myCalender = Calendar.getInstance();
            myCalender.set(Calendar.HOUR_OF_DAY, settingsInformation.getEndOfDayNotificationsTime().getHours());
            myCalender.set(Calendar.MINUTE, settingsInformation.getEndOfDayNotificationsTime().getMinutes());
            myCalender.set(Calendar.SECOND, 0);
            if (Calendar.getInstance().after(myCalender)) {
                // Use rateDay method to come up with message
                String message = rateDay(tasks);
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                // Create and then show alert dialog with message
                builder.setMessage(message)
                        .setCancelable(false)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {public void onClick(DialogInterface dialog, int id) {dialog.cancel();}});
                AlertDialog alert = builder.create();
                alert.setTitle("Daily Update!");
                alert.show();
                settingsInformation.setHasGivenDailyInfo(""+Calendar.getInstance().get(Calendar.YEAR)+Calendar.getInstance().get(Calendar.MONTH)+Calendar.getInstance().get(Calendar.DATE));
                saveSettingsInformation(settingsInformation.getGiveNotifications(), settingsInformation.getGiveEndOfDayNotifications(), settingsInformation.getEndOfDayNotificationsTime());
            }
        }
        else
            deleteDailyNotification();
    }

    public String rateDay(ArrayList<Task> tasks) {
        // Get the numbers of checked and ignored tasks
        int numberOfCheckedTasks = 0;
        int numberOfIgnoredTasks = 0;
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).isFinished()) {
                if (tasks.get(i).getFinishedCategory().equals(Task.finishCategory.checked))
                    numberOfCheckedTasks++;
                else
                    numberOfIgnoredTasks++;
            }
        }

        // Use this task information to return a string message
        if (tasks.size() < 1)
            return "You had no tasks today!";
        if (numberOfIgnoredTasks < numberOfCheckedTasks)
            return "You had a great day today!";
        if (numberOfIgnoredTasks > numberOfCheckedTasks)
            return "You did not have that great of a day. Try again tomorrow!";
        return "You did fine today!";
    }

    private void sortTasks(ArrayList<Task> tasks) {
        // Bubble Sort To Sort Tasks
        boolean finished = false;
        Task tempTask;
        while (!finished) {
            finished = true;
            for (int i = 0; i < tasks.size() - 1; i++) {
                if (tasks.get(i).getTime().isFurther(tasks.get(i+1).getTime())) {
                    tempTask = tasks.get(i);
                    tasks.set(i, tasks.get(i+1));
                    tasks.set(i+1, tempTask);
                    return;
                }
            }
        }
    }

    public void setTaskInformationById(int id, String title, RecommendedTask.Category category, Time time, ArrayList<Pair<String, Boolean>> daysOfWeek) {
        // Find and set Values for Task
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            if (id == task.getId()) {
                task.setTitle(title);
                task.setCategory(category);
                task.setTime(time);
                task.setDaysOfWeek(daysOfWeek);

                addNotification(task);

                sortTasks(tasks);
                saveTaskInformation(tasks);
                return;
            }
        }
    }

    public void addTask(Task task) {
        // Adds task to Array List. Then sorts and saves.
        tasks.add(task);

        sortTasks(tasks);
        addNotification(task);
        saveTaskInformation(tasks);
    }

    public void deleteTaskById(int id) {
        // Find and remove Task using id
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            if (id == task.getId()) {
                tasks.remove(i);

                deleteNotification(task);
                saveTaskInformation(tasks);
                return;
            }
        }
    }

    public ArrayList<Task> getTasks() {
        return tasks;
    }

    public void setNavigationElement(int id) {
        navigation.setSelectedItemId(id);
    }

    public void moveToDifferentFragment(Fragment frag) {
        // Takes in Fragment and moves said fragment
        FragmentManager myFragment = getSupportFragmentManager();
        myFragment.beginTransaction().replace(R.id.DisplayBody, frag).setReorderingAllowed(true).addToBackStack("name").commit();
        currentFragment = frag;
    }

    private void deleteNotification(Task task) {
        // Recreates Notification to be deleted
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent myIntent = new Intent(this, TaskNotification.class);
        // Add title and id to intent which gets sent to notification
        myIntent.putExtra("title", task.getTitle());
        myIntent.putExtra("id", task.getId());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, task.getId(), myIntent, PendingIntent.FLAG_MUTABLE);
        alarmManager.cancel(pendingIntent);
    }

    public void clearNotifications() {
        // Loop through and delete the notifications of every task
        for (int i = 0; i < tasks.size(); i++)
            deleteNotification(tasks.get(i));
    }

    public void addNotifications() {
        // Loop through and add the notifications of every task
        for (int i = 0; i < tasks.size(); i++)
            addNotification(tasks.get(i));
    }

    private void addNotification(Task task) {
        // Creates Notification from Calender
        Calendar myCalender = Calendar.getInstance();
        myCalender.set(Calendar.HOUR_OF_DAY, task.getTime().getHours());
        myCalender.set(Calendar.MINUTE, task.getTime().getMinutes());
        myCalender.set(Calendar.SECOND, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent myIntent = new Intent(this, TaskNotification.class);
        myIntent.putExtra("title", task.getTitle());
        myIntent.putExtra("id", task.getId());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, task.getId(), myIntent, PendingIntent.FLAG_MUTABLE);

        // If the time already happened add a day to the date
        if (myCalender.before(Calendar.getInstance()))
            myCalender.add(Calendar.DATE, 1);

        // Set Notifications
        alarmManager.cancel(pendingIntent);
        alarmManager.set(AlarmManager.RTC_WAKEUP, myCalender.getTimeInMillis(), pendingIntent);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, myCalender.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
    }

    public void createDailyNotification() {
        // Creates Special Everyday Notifications
        Calendar myCalender = Calendar.getInstance();
        myCalender.set(Calendar.HOUR_OF_DAY, settingsInformation.getEndOfDayNotificationsTime().getHours());
        myCalender.set(Calendar.MINUTE, settingsInformation.getEndOfDayNotificationsTime().getMinutes());
        myCalender.set(Calendar.SECOND, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent myIntent = new Intent(this, DailyNotification.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, myIntent, PendingIntent.FLAG_MUTABLE);

        // Set Notifications
        alarmManager.cancel(pendingIntent);
        alarmManager.set(AlarmManager.RTC_WAKEUP, myCalender.getTimeInMillis(), pendingIntent);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, myCalender.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
    }

    public void deleteDailyNotification() {
        // Recreates then deletes notification
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent myIntent = new Intent(this, DailyNotification.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, myIntent, PendingIntent.FLAG_MUTABLE);
        alarmManager.cancel(pendingIntent);
    }

    public void saveTaskInformation(ArrayList<Task> tasks) {
        this.tasks = tasks;
        File filesDir = getApplication().getFilesDir();
        try {
            FileOutputStream outputStream = new FileOutputStream(new File(filesDir, "selfCareTasks.txt"));
            // Converts text to Json using Gson. Then write it to file.
            Gson gson = new Gson();
            outputStream.write(gson.toJson(tasks).getBytes());
            outputStream.close();
        } catch (IOException e) {throw new RuntimeException(e);}
    }

    public void saveSettingsInformation(boolean giveNotifications, boolean giveEndOfDayNotifications, Time endOfDayNotificationsTime) {
        // Sets Settings Information on Main Class
        settingsInformation.setGiveNotifications(giveNotifications);
        settingsInformation.setGiveEndOfDayNotifications(giveEndOfDayNotifications);
        settingsInformation.setEndOfDayNotificationsTime(endOfDayNotificationsTime);
        File filesDir = getApplication().getFilesDir();
        try {
            FileOutputStream outputStream = new FileOutputStream(new File(filesDir, "selfCareSettings.txt"));
            // Convert to Json using Gson. Then write to file.
            Gson gson = new Gson();
            outputStream.write(gson.toJson(settingsInformation).getBytes());
            outputStream.close();
        } catch (IOException e) {throw new RuntimeException(e);}
    }

    private ArrayList<Task> getSavedTasksInformation() {
        File filesDir = getApplication().getFilesDir();
        File myFile = new File(filesDir, "selfCareTasks.txt");
        try {myFile.createNewFile();} catch (IOException e) {throw new RuntimeException(e);}
        byte[] byteInformation = new byte[(int) myFile.length()];

        try {
            FileInputStream inputStream = new FileInputStream(myFile);
            inputStream.read(byteInformation);
            String stringInformation = new String(byteInformation);
            // Return if the file is blank
            if (stringInformation.equals(""))
                return new ArrayList<>();
            // Convert Json text to arrayList object using Gson
            Gson gson = new Gson();
            Type listType = new TypeToken<ArrayList<Task>>(){}.getType();
            ArrayList<Task> toReturn = gson.fromJson(stringInformation, listType);
            return toReturn;
        } catch (IOException e) {throw new RuntimeException(e);}
    }

    private SettingsInformation getSavedSettingsInformation() {
        File filesDir = getApplication().getFilesDir();
        File myFile = new File(filesDir, "selfCareSettings.txt");
        try {myFile.createNewFile();} catch (IOException e) {throw new RuntimeException(e);}
        byte[] byteInformation = new byte[(int) myFile.length()];

        try {
            FileInputStream inputStream = new FileInputStream(myFile);
            inputStream.read(byteInformation);
            String stringInformation = new String(byteInformation);
            // Return if the file is blank
            if (stringInformation.equals(""))
                return new SettingsInformation(true, true, new Time(20,0), "false");
            // Convert Json text to arrayList object using Gson
            Gson gson = new Gson();
            return gson.fromJson(stringInformation, SettingsInformation.class);
        } catch (IOException e) {throw new RuntimeException(e);}
    }

    public SettingsInformation getSettingsInformation() {return this.settingsInformation;}
}