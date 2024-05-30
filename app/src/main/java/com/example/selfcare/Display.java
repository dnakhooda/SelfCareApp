package com.example.selfcare;

import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;

public class Display extends Fragment {
    private MainActivity mainActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {return inflater.inflate(R.layout.fragment_display, container, false);}

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get Main Instance and Set Navigation Element
        mainActivity = MainActivity.instance;
        mainActivity.setNavigationElement(R.id.displayItemNav);

        createDisplayItems();
    }

    private void createDisplayItems() {
        ArrayList<Task> tasks = mainActivity.getTasks();
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            // Check if they should get pop up on this day
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && task.doOnDayOfTheWeek(LocalDate.now().getDayOfWeek().name()) && !task.isFinished() && !task.getTime().isFurther(new Time(Calendar.getInstance().get(Calendar.HOUR_OF_DAY), Calendar.getInstance().get(Calendar.MINUTE))))
                createLayout(task);
        }
    }

    private void createLayout(Task task) {
        // Create a new display item layout
        LinearLayout displayLayout = mainActivity.findViewById(R.id.displayLinearLayout);
        LayoutInflater inflater = mainActivity.getLayoutInflater();
        View newLayout = inflater.inflate(R.layout.display_item_layout, displayLayout, false);

        // Set Image Based on Category
        ImageView categoryImage = newLayout.findViewById(R.id.displayImageCategory);
        switch (task.getCategory()) {
            case Hygiene:
                categoryImage.setImageResource(R.drawable.brush);
                break;
            case Medical:
                categoryImage.setImageResource(R.drawable.heart);
                break;
            case Vitamins:
                categoryImage.setImageResource(R.drawable.vitamins);
                break;
            case Exercise:
                categoryImage.setImageResource(R.drawable.exercise);
                break;
            case Mind:
                categoryImage.setImageResource(R.drawable.mind);
                break;
        }

        // Set Title and Time
        TextView timeText = newLayout.findViewById(R.id.Time);
        timeText.setText(task.getTime().toString());

        TextView title = newLayout.findViewById(R.id.Title);
        title.setText(task.getTitle());

        // Add Functionality for Check and Ignore Buttons
        Button check = newLayout.findViewById(R.id.Check);
        check.setOnClickListener((view) -> {
            task.finish(Task.finishCategory.checked, ""+Calendar.getInstance().get(Calendar.YEAR)+Calendar.getInstance().get(Calendar.MONTH)+Calendar.getInstance().get(Calendar.DATE));
            displayLayout.removeView(newLayout);
            mainActivity.saveTaskInformation(mainActivity.getTasks());
        });

        Button ignore = newLayout.findViewById(R.id.Ignore);
        ignore.setOnClickListener((view) -> {
            task.finish(Task.finishCategory.ignored, ""+Calendar.getInstance().get(Calendar.YEAR)+Calendar.getInstance().get(Calendar.MONTH)+Calendar.getInstance().get(Calendar.DATE));
            displayLayout.removeView(newLayout);
            mainActivity.saveTaskInformation(mainActivity.getTasks());
        });

        // Add Layout
        displayLayout.addView(newLayout);
    }
}