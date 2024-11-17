package com.example.yoga_app.ui.search;

import android.app.AlertDialog;
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

import com.example.yoga_app.Class;
import com.example.yoga_app.R;
import com.example.yoga_app.ui.classes.ClassViewModel;

import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ClassViewHolder>{

    private Fragment fragment;
    private List<Class> classList;
    private SearchViewModel searchViewModel;


    public SearchAdapter(Fragment fragment, List<Class> classList, SearchViewModel searchViewModel) {
        this.fragment = fragment;
        this.classList = classList;
        this.searchViewModel = searchViewModel;
    }

    @NonNull
    @Override
    public SearchAdapter.ClassViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search, parent, false);
        return new SearchAdapter.ClassViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchAdapter.ClassViewHolder holder, int position) {
        Class yogaClass = classList.get(position);
        holder.tvClassName.setText(" Class of teacher " + yogaClass.getTeacher() + " " + yogaClass.getDate());

        holder.itemView.setOnClickListener(v -> {
            // Tạo dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(fragment.getContext());
            builder.setTitle("Class Details");

            // Nội dung chi tiết của lớp học
            builder.setMessage(
                    "Teacher: " + yogaClass.getTeacher() + "\n" +
                    "Date: " + yogaClass.getDate() + "\n" +
                    "Comment:" + yogaClass.getComment());

            // Nút đóng dialog
            builder.setPositiveButton("Close", (dialog, which) -> dialog.dismiss());

            // Hiển thị dialog
            AlertDialog dialog = builder.create();
            dialog.show();
        });


    }

    @Override
    public int getItemCount() {
        return classList.size();
    }
    static class ClassViewHolder extends RecyclerView.ViewHolder {

        TextView tvClassName;

        public ClassViewHolder(@NonNull View itemView) {
            super(itemView);
            tvClassName = itemView.findViewById(R.id.class_name);

        }
    }
}
