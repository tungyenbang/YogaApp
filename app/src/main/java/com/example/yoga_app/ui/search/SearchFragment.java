package com.example.yoga_app.ui.search;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yoga_app.Class;
import com.example.yoga_app.DatabaseHelper;
import com.example.yoga_app.R;
import com.example.yoga_app.databinding.FragmentSearchBinding;
import com.example.yoga_app.ui.classes.ClassAdapter;
import com.example.yoga_app.ui.classes.ClassViewModel;
import com.example.yoga_app.ui.classes.ClassesFragment;
import com.example.yoga_app.ui.home.HomeViewModel;

import java.util.List;

public class SearchFragment extends Fragment {

    private RecyclerView recyclerView;
    private SearchViewModel searchViewModel;
    private SearchAdapter searchAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        recyclerView = view.findViewById(R.id.recyclerViewSearch);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        DatabaseHelper databaseHelper = new DatabaseHelper(getContext());
        searchViewModel = new ViewModelProvider(this).get(SearchViewModel.class);
        searchViewModel.getClassList().observe(getViewLifecycleOwner(), new Observer<List<Class>>() {
            @Override
            public void onChanged(List<Class> classList) {
                searchAdapter = new SearchAdapter(SearchFragment.this, classList, searchViewModel);
                recyclerView.setAdapter(searchAdapter);
            }
        });

        EditText text_input = view.findViewById(R.id.text_input);
        Button searchBtn = view.findViewById(R.id.btn_search);

        searchBtn.setOnClickListener(v -> {
            String teacherName = text_input.getText().toString().trim(); // Lấy tên giáo viên từ EditText
            searchViewModel.setSearchText(teacherName);
        });

        return view;
    }


}