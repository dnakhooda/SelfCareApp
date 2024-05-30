package com.example.selfcare;

import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import com.google.android.material.textfield.TextInputEditText;
import androidx.fragment.app.Fragment;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioGroup;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Objects;

public class Addition extends Fragment {
    private MainActivity mainActivity;
    private Bundle arguments;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        arguments = getArguments();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {return inflater.inflate(R.layout.fragment_addition, container, false);}

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mainActivity = MainActivity.instance;
        setComponentFunctionality();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void setComponentFunctionality() {
        // Pre-Fill Views using Arguments Given
        TextInputEditText titleInput = mainActivity.findViewById(R.id.additionTitleInput);
        titleInput.setText(arguments.getString("title"));

        TimePicker timePicker = mainActivity.findViewById(R.id.additionTimePicker);
        timePicker.setHour(arguments.getInt("hours"));
        timePicker.setMinute(arguments.getInt("minutes"));

        RadioGroup categoryGroup = mainActivity.findViewById(R.id.additionCategoryGroup);

        RecommendedTask.Category pickedCategory = RecommendedTask.Category.valueOf(arguments.getString("category"));
        switch (pickedCategory) {
            case Medical:
                categoryGroup.check(R.id.additionMedicalButton);
                break;
            case Hygiene:
                categoryGroup.check(R.id.additionHygieneButton);
                break;
            case Vitamins:
                categoryGroup.check(R.id.additionVitaminsButton);
                break;
            case Exercise:
                categoryGroup.check(R.id.additionExerciseButton);
                break;
            case Mind:
                categoryGroup.check(R.id.additionMindButton);
                break;
        }

        // Pre-Fill Days Per Week Views Using Arguments
        CheckBox mondayButton = mainActivity.findViewById(R.id.additionMondayButton);
        mondayButton.setChecked(arguments.getBooleanArray("daysOfWeek")[0]);
        CheckBox tuesdayButton = mainActivity.findViewById(R.id.additionTuesdayButton);
        tuesdayButton.setChecked(arguments.getBooleanArray("daysOfWeek")[1]);
        CheckBox wednesdayButton = mainActivity.findViewById(R.id.additionWednesdayButton);
        wednesdayButton.setChecked(arguments.getBooleanArray("daysOfWeek")[2]);
        CheckBox thursdayButton = mainActivity.findViewById(R.id.additionThursdayButton);
        thursdayButton.setChecked(arguments.getBooleanArray("daysOfWeek")[3]);
        CheckBox fridayButton = mainActivity.findViewById(R.id.additionFridayButton);
        fridayButton.setChecked(arguments.getBooleanArray("daysOfWeek")[4]);
        CheckBox saturday = mainActivity.findViewById(R.id.additionSaturdayButton);
        saturday.setChecked(arguments.getBooleanArray("daysOfWeek")[5]);
        CheckBox sunday = mainActivity.findViewById(R.id.additionSundayButton);
        sunday.setChecked(arguments.getBooleanArray("daysOfWeek")[6]);

        // Set Add Button Functionality
        Button addButton = mainActivity.findViewById(R.id.additionAddButton);
        addButton.setOnClickListener(r -> {
            // Validate User Inputted Title
            if (Objects.requireNonNull(titleInput.getText()).length() < 1) {
                Toast.makeText(Objects.requireNonNull(getActivity()).getApplicationContext(), "You must have title!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (titleInput.getText().length() > 19) {
                Toast.makeText(Objects.requireNonNull(getActivity()).getApplicationContext(), "Your title is too long!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (titleInput.getText().toString().contains("{") || titleInput.getText().toString().contains("}") || titleInput.getText().toString().contains("[") || titleInput.getText().toString().contains("]") || titleInput.getText().toString().contains(":") || titleInput.getText().toString().contains(",") || titleInput.getText().toString().contains("\"") || titleInput.getText().toString().contains("'")) {
                Toast.makeText(Objects.requireNonNull(getActivity()).getApplicationContext(), "You cannot have the characters '{}' or '[]' or ' \" ' or ',' in the title of the task!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Get Inputted Category and Days Of Week Checked Off
            RecommendedTask.Category categoryChosen = RecommendedTask.Category.Hygiene;
            if (categoryGroup.getCheckedRadioButtonId() == R.id.additionMedicalButton)
                categoryChosen = RecommendedTask.Category.Medical;
            else if (categoryGroup.getCheckedRadioButtonId() == R.id.additionVitaminsButton)
                categoryChosen = RecommendedTask.Category.Vitamins;
            else if (categoryGroup.getCheckedRadioButtonId() == R.id.additionExerciseButton)
                categoryChosen = RecommendedTask.Category.Exercise;
            else if (categoryGroup.getCheckedRadioButtonId() == R.id.additionMindButton)
                categoryChosen = RecommendedTask.Category.Mind;

            ArrayList<Pair<String, Boolean>> daysOfWeek = new ArrayList<>();
            daysOfWeek.add(new Pair("Monday", mondayButton.isChecked()));
            daysOfWeek.add(new Pair("Tuesday", tuesdayButton.isChecked()));
            daysOfWeek.add(new Pair("Wednesday", wednesdayButton.isChecked()));
            daysOfWeek.add(new Pair("Thursday", thursdayButton.isChecked()));
            daysOfWeek.add(new Pair("Friday", fridayButton.isChecked()));
            daysOfWeek.add(new Pair("Saturday", saturday.isChecked()));
            daysOfWeek.add(new Pair("Sunday", sunday.isChecked()));

            // Either Create or Change Elements of Task
            if (arguments.getInt("id") == -1)
                mainActivity.addTask(new Task(titleInput.getText().toString(), categoryChosen, new Time(timePicker.getHour(), timePicker.getMinute()), daysOfWeek));
            else
                mainActivity.setTaskInformationById(arguments.getInt("id"), titleInput.getText().toString(), categoryChosen, new Time(timePicker.getHour(), timePicker.getMinute()), daysOfWeek);
            mainActivity.moveToDifferentFragment(new Edit());
        });

        // Delete and Cancel Button Functionality
        Button cancelButton = mainActivity.findViewById(R.id.additionCancelButton);
        cancelButton.setOnClickListener(r -> mainActivity.moveToDifferentFragment(new Edit()));

        Button deleteButton = mainActivity.findViewById(R.id.additionDeleteButton);
        deleteButton.setOnClickListener(r -> {
            mainActivity.deleteTaskById(arguments.getInt("id"));
            mainActivity.moveToDifferentFragment(new Edit());
        });
    }
}