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

public class Recommended extends Fragment {
    private MainActivity mainActivity;
    private final ArrayList<RecommendedTask> recommendedList = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {return inflater.inflate(R.layout.fragment_recommended, container, false);}

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get Main Instance and Set Navigation Element
        mainActivity = MainActivity.instance;
        mainActivity.setNavigationElement(R.id.recommendedItemNav);

        displayRecommendedItems();
    }

    private void displayRecommendedItems() {
        // Create List of Example Tasks
        recommendedList.add(new RecommendedTask(new Time(6,45), "Take A Shower", RecommendedTask.Category.Hygiene));
        recommendedList.add(new RecommendedTask(new Time(7,0), "Morning Brush Teeth", RecommendedTask.Category.Hygiene));
        recommendedList.add(new RecommendedTask(new Time(16,0), "Meditate", RecommendedTask.Category.Mind));
        recommendedList.add(new RecommendedTask(new Time(17,0), "Take A Run", RecommendedTask.Category.Exercise));
        recommendedList.add(new RecommendedTask(new Time(7,0), "Take Vitamin D", RecommendedTask.Category.Vitamins));
        recommendedList.add(new RecommendedTask(new Time(20,0), "Night Brush Teeth", RecommendedTask.Category.Hygiene));

        // Loop through the list. If the user does not already contain the task, then show and give the user the option to add it.
        for (int i = 0; i < recommendedList.size(); i++) {
            if (!containsTitle(recommendedList.get(i).getTitle())) {
                RecommendedTask task = recommendedList.get(i);
                createRecommendedLayout(task);
            }
        }
    }

    private void createRecommendedLayout(RecommendedTask task) {
        // Create a new recommended item layout
        LinearLayout theLayout = mainActivity.findViewById(R.id.recommendedLinearLayout);
        LayoutInflater inflater = mainActivity.getLayoutInflater();
        View newLayout = inflater.inflate(R.layout.recommended_item_layout, theLayout, false);

        // Set Image Based on Category
        ImageView categoryImage = newLayout.findViewById(R.id.recommendedImageCategory);
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
        TextView timeText = newLayout.findViewById(R.id.timeRecommended);
        timeText.setText(task.getTime().toString());

        TextView title = newLayout.findViewById(R.id.titleRecommended);
        title.setText(task.getTitle());

        // Go to Addition Fragment if the user wants to add it to their schedule. Gives title, time, category as arguments.
        Button addButton = newLayout.findViewById(R.id.addButtonRecommended);
        addButton.setOnClickListener(view -> {
            Bundle fragmentArgs = new Bundle();
            fragmentArgs.putString("title", task.getTitle());
            fragmentArgs.putInt("hours", task.getTime().getHours());
            fragmentArgs.putInt("minutes", task.getTime().getMinutes());
            fragmentArgs.putString("category", task.getCategory().toString());
            fragmentArgs.putInt("id", -1);
            fragmentArgs.putBooleanArray("daysOfWeek", new boolean[]{true, true, true, true, true, true, true});
            Addition additionFragment = new Addition();
            additionFragment.setArguments(fragmentArgs);
            mainActivity.moveToDifferentFragment(additionFragment);
        });

        // Add Layout
        theLayout.addView(newLayout);
    }

    private boolean containsTitle(String title) {
        // Loop through each already created task and see if the title already exists
        for (int i = 0; i < mainActivity.getTasks().size(); i++) {
            if (mainActivity.getTasks().get(i).getTitle().toLowerCase().contains(title.toLowerCase()))
                return true;
        }
        return false;
    }
}