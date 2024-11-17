package com.example.yoga_app.ui.home;

import static androidx.navigation.Navigation.findNavController;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yoga_app.Course;
import com.example.yoga_app.DatabaseHelper;
import com.example.yoga_app.R;
import com.example.yoga_app.ui.add.AddViewModel;

import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private CourseAdapter courseAdapter;
    private HomeViewModel homeViewModel;
    private Button iconCart;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewCourses);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        Button btnDeleteAll = view.findViewById(R.id.deleteButton);
        iconCart = view.findViewById(R.id.iconCart);
        // Khởi tạo DatabaseHelper và HomeViewModel
        DatabaseHelper databaseHelper = new DatabaseHelper(getContext());
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        btnDeleteAll.setOnClickListener(v -> {
            homeViewModel.deleteAllCourses(); // Gọi hàm xóa tất cả
        });

        iconCart.setOnClickListener(v ->{
            NavController navController = NavHostFragment.findNavController(this);
            navController.navigate(R.id.action_homeFragment_to_viewCartFragment);


        });

        // Quan sát LiveData từ ViewModel và cập nhật RecyclerView
        homeViewModel.getCourseList().observe(getViewLifecycleOwner(), new Observer<List<Course>>() {
            @Override
            public void onChanged(List<Course> courseList) {
                courseAdapter = new CourseAdapter(courseList, homeViewModel, HomeFragment.this);
                recyclerView.setAdapter(courseAdapter);
            }
        });

        return view;
    }
}
