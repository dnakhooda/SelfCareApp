package com.example.selfcare;

import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TimePicker;

public class Settings extends Fragment {
    private MainActivity mainActivity;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {return inflater.inflate(R.layout.fragment_settings, container, false);}

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get Main Instance and Set Navigation Element
        mainActivity = MainActivity.instance;
        mainActivity.setNavigationElement(R.id.settingsItemNav);

        addViewFunctionality();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void addViewFunctionality() {
        // Get and Set View Properties
        CheckBox notificationsCheckBox = mainActivity.findViewById(R.id.settingsNotificationsCheckBox);
        notificationsCheckBox.setChecked(mainActivity.getSettingsInformation().getGiveNotifications());

        CheckBox endOfDayNotificationsCheckBox = mainActivity.findViewById(R.id.settingsEndOfDayNotificationsCheckBox);
        endOfDayNotificationsCheckBox.setChecked(mainActivity.getSettingsInformation().getGiveEndOfDayNotifications());

        TimePicker endOfDayNotificationTimePicker = mainActivity.findViewById(R.id.settingsDailyNotificationsPicker);
        endOfDayNotificationTimePicker.setHour(mainActivity.getSettingsInformation().getEndOfDayNotificationsTime().getHours());
        endOfDayNotificationTimePicker.setMinute(mainActivity.getSettingsInformation().getEndOfDayNotificationsTime().getMinutes());

        // If notificationsCheckBox changed then either add Notifications or remove Notifications depending on if checked or not
        notificationsCheckBox.setOnCheckedChangeListener((v1, v2) -> {
            mainActivity.saveSettingsInformation(notificationsCheckBox.isChecked(), endOfDayNotificationsCheckBox.isChecked(), new Time(endOfDayNotificationTimePicker.getHour(), endOfDayNotificationTimePicker.getMinute()));
            if (notificationsCheckBox.isChecked())
                mainActivity.addNotifications();
            else
                mainActivity.clearNotifications();
        });
        // If endOfDayNotificationsCheckBox changed then either add daily Notifications or remove daily Notifications depending on if checked or not
        endOfDayNotificationsCheckBox.setOnCheckedChangeListener((v1, v2) -> {
            mainActivity.saveSettingsInformation(notificationsCheckBox.isChecked(), endOfDayNotificationsCheckBox.isChecked(), new Time(endOfDayNotificationTimePicker.getHour(), endOfDayNotificationTimePicker.getMinute()));
            if (endOfDayNotificationsCheckBox.isChecked())
                mainActivity.createDailyNotification();
            else
                mainActivity.deleteDailyNotification();
        });
        // Save endOfDayNotificationsCheckBox time on change
        endOfDayNotificationTimePicker.setOnTimeChangedListener((v1, v2, v3) -> mainActivity.saveSettingsInformation(notificationsCheckBox.isChecked(), endOfDayNotificationsCheckBox.isChecked(), new Time(endOfDayNotificationTimePicker.getHour(), endOfDayNotificationTimePicker.getMinute())));
    }
}