package com.example.yoga_app.ui.classes;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yoga_app.Course;
import com.example.yoga_app.DatabaseHelper;
import com.example.yoga_app.R;
import com.example.yoga_app.ui.classes.ClassAdapter;
import com.example.yoga_app.ui.home.CourseAdapter;
import com.example.yoga_app.ui.home.HomeFragment;
import com.example.yoga_app.ui.home.HomeViewModel;
import com.example.yoga_app.Class;

import java.util.List;

public class ClassesFragment extends Fragment {

    private RecyclerView recyclerView;
    private ClassAdapter classAdapter;
    private ClassViewModel classViewModel;
    private int courseId;
    private String day;



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_classes, container, false);
        recyclerView = view.findViewById(R.id.recyclerViewClasses);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        Button btnAdd = view.findViewById(R.id.btn_new);

        DatabaseHelper databaseHelper = new DatabaseHelper(getContext());
        classViewModel = new ViewModelProvider(this).get(ClassViewModel.class);

        if (getArguments() != null) {
            courseId = getArguments().getInt("id");
            day = getArguments().getString("day");
            classViewModel.setCourseId(courseId);
        }


        classViewModel.getClassList().observe(getViewLifecycleOwner(), new Observer<List<Class>>() {
            @Override
            public void onChanged(List<Class> classList) {
                classAdapter = new ClassAdapter(ClassesFragment.this, classList, classViewModel, day);
                recyclerView.setAdapter(classAdapter);
            }
        });

        btnAdd.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putInt("courseId", courseId);
            bundle.putString("day", day);
            NavHostFragment.findNavController(ClassesFragment.this)
                    .navigate(R.id.navigation_add_class, bundle);
        });

        ImageButton btnClose = view.findViewById(R.id.btn_close);
        btnClose.setOnClickListener(v -> {
            getParentFragmentManager().popBackStack(); // Quay lại màn hình trước
        });








        return view;
    }
}
