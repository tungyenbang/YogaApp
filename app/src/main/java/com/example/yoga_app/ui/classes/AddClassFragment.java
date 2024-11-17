package com.example.yoga_app.ui.classes;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yoga_app.R;
import com.example.yoga_app.Class;
import com.example.yoga_app.ui.add.AddViewModel;
import com.example.yoga_app.ui.classes.ClassViewModel;


import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AddClassFragment extends Fragment {

    private TextView tvTitle, tvTeacher, tvDate, tvComment, etDate;
    private EditText etTeacher, etComment;
    private Button btnAdd;
    private ImageButton btnClose;
    private ClassViewModel classViewModel;
    private int courseId;
    private String day;
    private List<String> allowedDays;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_class, container, false);

        tvTitle = view.findViewById(R.id.tvTitle);
        tvTeacher = view.findViewById(R.id.teacher_text);
        tvDate = view.findViewById(R.id.date_text);
        tvComment = view.findViewById(R.id.comment_text);
        etTeacher = view.findViewById(R.id.teacher_input);
        etDate = view.findViewById(R.id.date_input);
        etComment = view.findViewById(R.id.comment_input);
        btnAdd = view.findViewById(R.id.btn_add_class);

        classViewModel = new ViewModelProvider(this).get(ClassViewModel.class);

        if (getArguments() != null && getArguments().containsKey("day")) {
            courseId = getArguments().getInt("courseId");
            day = getArguments().getString("day");

            allowedDays = Arrays.asList(day.split(",\\s*"));

        }

        etDate.setOnClickListener(v -> {
            DatePickerFragment datePickerFragment = DatePickerFragment.newInstance(allowedDays);
            datePickerFragment.setTargetFragment(AddClassFragment.this, 0);
            datePickerFragment.show(getParentFragmentManager(), "datePicker");
        });

        btnAdd.setOnClickListener(v -> {
            addClass();
            getParentFragmentManager().popBackStack();
        });

        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey("classTeacher") && bundle.containsKey("classDate")) {
            String teacher = bundle.getString("classTeacher");
            String date = bundle.getString("classDate");
            String comment = bundle.getString("classComment");
            String updateDay = bundle.getString("updateDay");

            allowedDays = Arrays.asList(updateDay.split(",\\s*"));
            etTeacher.setText(teacher);
            etDate.setText(date);
            etComment.setText(comment);

            tvTitle.setText("Update Class");

            btnAdd.setText("Update");

            etDate.setOnClickListener(v -> {
                DatePickerFragment datePickerFragment = DatePickerFragment.newInstance(allowedDays);
                datePickerFragment.setTargetFragment(AddClassFragment.this, 0);
                datePickerFragment.show(getParentFragmentManager(), "datePicker");
            });

            btnAdd.setOnClickListener(v -> {
                // Logic cập nhật lớp học
                updateClass();
                getParentFragmentManager().popBackStack();
            });
        } else {
            btnAdd.setOnClickListener(v -> {
                addClass();
                getParentFragmentManager().popBackStack();
            });
        }










        btnClose = view.findViewById(R.id.btn_close);
        btnClose.setOnClickListener(v -> {
            getParentFragmentManager().popBackStack(); // Quay lại màn hình trước
        });


        return view;
    }

    public void updateDate(int year, int month, int day) {
        // Cập nhật ngày đã chọn vào EditText
        String selectedDate = String.format("%02d/%02d/%d", day, month, year);
        etDate.setText(selectedDate);
    }

    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        private List<String> allowedDays;

        // Tạo một phương thức newInstance để truyền allowedDays
        public static DatePickerFragment newInstance(List<String> allowedDays) {
            DatePickerFragment fragment = new DatePickerFragment();
            Bundle args = new Bundle();
            args.putStringArrayList("allowed_days", new ArrayList<>(allowedDays));
            fragment.setArguments(args);
            return fragment;
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
            if (getArguments() != null) {
                allowedDays = getArguments().getStringArrayList("allowed_days"); // Nhận danh sách ngày hợp lệ
            }
            // Lấy ngày hiện tại
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH); // Tháng bắt đầu từ 0
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            // Trả về một đối tượng DatePickerDialog với ngày hiện tại
            return new DatePickerDialog(requireActivity(), this, year, month, day);
        }

        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, dayOfMonth);
            String selectedDayOfWeek = new SimpleDateFormat("EEEE", Locale.getDefault()).format(calendar.getTime()); // Lấy tên ngày trong tuần

            Fragment targetFragment = getTargetFragment();
            if (targetFragment instanceof AddClassFragment) {
                AddClassFragment addClassFragment = (AddClassFragment) targetFragment;

                // Kiểm tra xem ngày đã chọn có nằm trong danh sách allowedDays không
                if (allowedDays.contains(selectedDayOfWeek)) {
                    // Nếu hợp lệ, cập nhật ngày
                    addClassFragment.updateDate(year, month + 1, dayOfMonth);
                } else {
                    // Nếu không hợp lệ, hiển thị thông báo lỗi
                    Toast.makeText(requireActivity(), "Ngày chọn không hợp lệ. Vui lòng chọn ngày trong " + allowedDays, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }





    private void addClass() {
        String teacher = etTeacher.getText().toString();
        String date = etDate.getText().toString();
        String comment = etComment.getText().toString();

        if(teacher.isEmpty() || date.isEmpty()){
            Toast.makeText(getContext(), "Please fill all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        Class classs = new Class(comment, teacher, date, courseId);


        classViewModel.addClasses(classs);


    }

    private void updateClass(){
        String teacher = etTeacher.getText().toString();
        String date = etDate.getText().toString();
        String comment = etComment.getText().toString();

        if(teacher.isEmpty() || date.isEmpty()){
            Toast.makeText(getContext(), "Please fill all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey("classTeacher") && bundle.containsKey("classDate")) {
            int id = bundle.getInt("classId");
            int courseId = bundle.getInt("courseId");
            Class updateClass = new Class(comment, teacher, date, courseId);
            updateClass.setCourseId(courseId);
            updateClass.setId(id);
            classViewModel.updateClass(updateClass);
        } else {
            Toast.makeText(getContext(), "Error: Course ID not found", Toast.LENGTH_SHORT).show();
        }
    }
}