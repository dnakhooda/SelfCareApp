package com.example.selfcare;

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

import java.util.ArrayList;

public class Edit extends Fragment {
    private MainActivity mainActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {return inflater.inflate(R.layout.fragment_edit, container, false);}

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get Main Instance and Set Navigation Element
        mainActivity = MainActivity.instance;
        mainActivity.setNavigationElement(R.id.editItemNav);

        createTaskList(mainActivity.getTasks());
    }

    private void createTaskList(ArrayList<Task> tasks) {
        // Create Task Edit Items. The tasks are sorted so it should be chronological
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            createEditLayout(task);
        }

        // Add Button Sends to Addition Fragment and Gives Arguments
        Button addButton = mainActivity.findViewById(R.id.editAddButton);
        addButton.setOnClickListener(view -> {
            Bundle fragmentArgs = new Bundle();
            fragmentArgs.putString("title", "");
            fragmentArgs.putInt("hours", 0);
            fragmentArgs.putInt("minutes", 0);
            fragmentArgs.putString("category", "Hygiene");
            fragmentArgs.putInt("id", -1);
            Addition additionFragment = new Addition();
            fragmentArgs.putBooleanArray("daysOfWeek", new boolean[]{true, true, true, true, true, true, true});

            additionFragment.setArguments(fragmentArgs);
            mainActivity.moveToDifferentFragment(additionFragment);
        });
    }

    private void createEditLayout(Task task) {
        // Create a new edit item layout
        LinearLayout editFragmentLayout = mainActivity.findViewById(R.id.editLinearLayout);
        LayoutInflater inflater = mainActivity.getLayoutInflater();
        View newEditLayout = inflater.inflate(R.layout.edit_item_layout, editFragmentLayout, false);

        // Set Image Based on Category
        ImageView categoryImage = newEditLayout.findViewById(R.id.editItemCategorySymbol);
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
        TextView timeText = newEditLayout.findViewById(R.id.editItemTimeText);
        timeText.setText(task.getTime().toString());

        TextView title = newEditLayout.findViewById(R.id.editItemTitle);
        title.setText(task.getTitle());

        Button editButton = newEditLayout.findViewById(R.id.editItemButton);
        // If Edit Button Pressed, To Addition Fragment and Give Task Arguments to Fragment
        editButton.setOnClickListener(view -> {
            Bundle fragmentArgs = new Bundle();
            fragmentArgs.putString("title", task.getTitle());
            fragmentArgs.putInt("hours", task.getTime().getHours());
            fragmentArgs.putInt("minutes", task.getTime().getMinutes());
            fragmentArgs.putString("category", task.getCategory().toString());
            fragmentArgs.putInt("id", task.getId());
            fragmentArgs.putBooleanArray("daysOfWeek", new boolean[]{task.getDaysOfWeek().get(0).second, task.getDaysOfWeek().get(1).second, task.getDaysOfWeek().get(2).second, task.getDaysOfWeek().get(3).second, task.getDaysOfWeek().get(4).second, task.getDaysOfWeek().get(5).second, task.getDaysOfWeek().get(6).second});
            Addition additionFragment = new Addition();
            additionFragment.setArguments(fragmentArgs);
            mainActivity.moveToDifferentFragment(additionFragment);
        });

        // Add Layout
        editFragmentLayout.addView(newEditLayout);
    }

}