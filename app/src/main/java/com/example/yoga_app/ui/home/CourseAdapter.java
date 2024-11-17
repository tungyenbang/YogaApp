package com.example.yoga_app.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yoga_app.Course;
import com.example.yoga_app.DatabaseHelper;
import com.example.yoga_app.R;
import com.example.yoga_app.ui.add.AddFragment;

import java.util.List;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.CourseViewHolder> {

    private List<Course> courseList;
    private HomeViewModel homeViewModel;
    private Fragment fragment;

    private DatabaseHelper databaseHelper;

    public CourseAdapter(List<Course> courseList, HomeViewModel homeViewModel, Fragment fragment) {
        this.courseList = courseList;
        this.homeViewModel = homeViewModel;
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.course_item, parent, false);
        return new CourseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {
        Course course = courseList.get(position);
        holder.tvCourseName.setText(" Course " + (position + 1));

        // Click listener cho nút More
        holder.btnMore.setOnClickListener(v -> {
            // Điều hướng tới trang chỉnh sửa với thông tin của course
            Bundle bundle = new Bundle();
            bundle.putInt("id", course.getId());
            bundle.putString("courseDay", course.getDay());
            bundle.putString("courseTime", course.getTime());
            bundle.putString("courseCapacity", course.getCapacity());
            bundle.putString("courseDuration", course.getDuration());
            bundle.putString("coursePrice", course.getPrice());
            bundle.putString("courseDescription", course.getDescription());
            bundle.putString("courseType", course.getType());

            // Điều hướng đến AddFragment và truyền dữ liệu
            NavHostFragment.findNavController(fragment)
                    .navigate(R.id.navigation_add, bundle);
        });

        // Click listener cho nút Delete
        holder.btnDelete.setOnClickListener(v -> {
            // Xóa course khỏi SQLite và cập nhật RecyclerView
            if (homeViewModel != null) {
                homeViewModel.deleteCourse(course);
            } else {
                Log.e("CourseAdapter", "HomeViewModel is null");
            }


        });

        // Click vào item để xem chi tiết lớp học
        holder.itemView.setOnClickListener(v -> {
            // Điều hướng đến trang chi tiết lớp học
            Bundle bundle = new Bundle();
            bundle.putInt("id", course.getId());
            bundle.putString("day", course.getDay());
            NavHostFragment.findNavController(fragment)
                    .navigate(R.id.navigation_classes, bundle);

        });
    }

    @Override
    public int getItemCount() {
        return courseList.size();
    }

    static class CourseViewHolder extends RecyclerView.ViewHolder {

        TextView tvCourseName;
        Button btnMore, btnDelete;

        public CourseViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCourseName = itemView.findViewById(R.id.tvCourseName);
            btnMore = itemView.findViewById(R.id.btnMore);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}

