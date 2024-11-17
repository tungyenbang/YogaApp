package com.example.yoga_app.ui.add;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.example.yoga_app.Course;
import com.example.yoga_app.DatabaseHelper;
import com.example.yoga_app.R;
import com.example.yoga_app.databinding.FragmentAddBinding;

import java.util.ArrayList;
import java.util.List;


public class AddFragment extends Fragment {

    private TextView textTT, etDay;
    private EditText etTime, etCapacity, etDuration, etPrice, etDescription;
    private Button btnAdd;
    private AddViewModel addViewModel;
    private RadioGroup rdType;
    private RadioButton rd1, rd2, rd3;

    // Các ngày trong tuần
    String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
    boolean[] selectedDays = new boolean[days.length];
    List<String> selectedDaysList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add, container, false);

        textTT = view.findViewById(R.id.title);
        etTime = view.findViewById(R.id.time_input);
        etDay = view.findViewById(R.id.day_input);
        etCapacity = view.findViewById(R.id.capacity_input);
        etDuration = view.findViewById(R.id.duration_input);
        etPrice = view.findViewById(R.id.price_input);
        etDescription = view.findViewById(R.id.description_input);
        rdType = view.findViewById(R.id.typeGroup);
        rd1 = view.findViewById(R.id.flow_yoga);
        rd2 = view.findViewById(R.id.aerial_yoga);
        rd3 = view.findViewById(R.id.family_yoga);
        btnAdd = view.findViewById(R.id.btn_add_class);

        addViewModel = new ViewModelProvider(this).get(AddViewModel.class);

        etDay.setOnClickListener(v -> showMultiChoiceDialog());

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCourse();
            }
        });




        // Lấy dữ liệu từ Bundle nếu có
        Bundle bundle = getArguments();
        if (bundle != null) {

            String day = bundle.getString("courseDay");
            String time = bundle.getString("courseTime");
            String capacity = bundle.getString("courseCapacity");
            String duration = bundle.getString("courseDuration");
            String price = bundle.getString("coursePrice");
            String description = bundle.getString("courseDescription");
            String type = bundle.getString("courseType");

            // Hiển thị dữ liệu vào các trường nhập liệu
            etDay.setText(day);
            etTime.setText(time);
            etCapacity.setText(capacity);
            etDuration.setText(duration);
            etPrice.setText(price);
            etDescription.setText(description);

            // Chọn loại khóa học dựa trên dữ liệu được truyền
            if (type != null) {
                switch (type) {
                    case "Flow Yoga":
                        rd1.setChecked(true);
                        break;
                    case "Aerial Yoga":
                        rd2.setChecked(true);
                        break;
                    case "Family Yoga":
                        rd3.setChecked(true);
                        break;
                }
            }

            textTT.setText("Edit Course");
            // Thay đổi nút thêm thành "Cập nhật"
            btnAdd.setText("Update");

            // Thêm chức năng cập nhật khóa học
            btnAdd.setOnClickListener(v -> {
                // Logic cập nhật khóa học
                updateCourse();
            });
        } else {
            // Nếu không có dữ liệu được truyền, đây là trường hợp thêm mới
            btnAdd.setOnClickListener(v -> {
                addCourse();
            });
        }

        addViewModel.getIsSyncSuccessful().observe(getViewLifecycleOwner(), isSuccessful -> {

            if (isSuccessful) {
                // Navigate to HomeFragment if sync is successful
                NavHostFragment.findNavController(AddFragment.this).navigate(R.id.navigation_home);
            } else {
                // Handle failure case
                Toast.makeText(getContext(), "Failed to add and sync course", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private void updateCourse() {
        String day = etDay.getText().toString();
        String time = etTime.getText().toString();
        String capacity = etCapacity.getText().toString();
        String duration = etDuration.getText().toString();
        String price = etPrice.getText().toString();
        int selectedTypeId = rdType.getCheckedRadioButtonId();
        String description = etDescription.getText().toString();

        if (day.isEmpty() || time.isEmpty() || capacity.isEmpty() || duration.isEmpty() || price.isEmpty() || selectedTypeId == -1) {
            Toast.makeText(getContext(), "Please fill all required fields", Toast.LENGTH_SHORT).show();
            return;
        }
        String type = ((RadioButton) getView().findViewById(selectedTypeId)).getText().toString();

        Bundle bundle = getArguments();
        if (bundle != null) {
            int courseId = bundle.getInt("id");

            Course updateCourse = new Course( day, time, capacity, duration, price, type, description);
            updateCourse.setId(courseId);
            // Gọi phương thức updateCourseAndSync trong ViewModel để cập nhật khóa học và đồng bộ Firebase
            addViewModel.updateCourseAndSync(updateCourse);
        } else {
            Toast.makeText(getContext(), "Error: Course ID not found", Toast.LENGTH_SHORT).show();
        }
    }

    private void addCourse() {
        String day = etDay.getText().toString();
        String time = etTime.getText().toString();
        String capacity = etCapacity.getText().toString();
        String duration = etDuration.getText().toString();
        String price = etPrice.getText().toString();
        int selectedTypeId = rdType.getCheckedRadioButtonId();
        String description = etDescription.getText().toString();

        if (day.isEmpty() || time.isEmpty() || capacity.isEmpty() || duration.isEmpty() || price.isEmpty() || selectedTypeId == -1) {
            Toast.makeText(getContext(), "Please fill all required fields", Toast.LENGTH_SHORT).show();
            return;
        }
        String type = ((RadioButton) getView().findViewById(selectedTypeId)).getText().toString();

        Course newCourse = new Course(day, time, capacity, duration, price, type, description);

        addViewModel.addCourseAndSync(newCourse);
    }

    private void showMultiChoiceDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setTitle("Chọn các ngày trong tuần");

        builder.setMultiChoiceItems(days, selectedDays, (dialog, which, isChecked) -> {
            selectedDays[which] = isChecked; // Update selected state
            if (isChecked) {
                selectedDaysList.add(days[which]);
            } else {
                selectedDaysList.remove(days[which]);
            }
        });

        builder.setPositiveButton("OK", (dialog, which) -> {
            // Update TextView with selected days
            etDay = getView().findViewById(R.id.day_input);
            etDay.setText(getSelectedDays());
            dialog.dismiss();
        });

        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private String getSelectedDays() {
        if (selectedDaysList.isEmpty()) {
            return "Không có ngày nào được chọn";
        }
        StringBuilder selectedDaysStr = new StringBuilder();
        for (String day : selectedDaysList) {
            selectedDaysStr.append(day).append(", ");
        }
        return selectedDaysStr.substring(0, selectedDaysStr.length() - 2);
    }
}