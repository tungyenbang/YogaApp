package com.example.yoga_app.ui.classes;

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

import java.util.List;

public class ClassAdapter extends RecyclerView.Adapter<ClassAdapter.ClassViewHolder>{

    private Fragment fragment;
    private List<Class> classList;
    private ClassViewModel classViewModel;
    private String day;

    public ClassAdapter(Fragment fragment, List<Class> classList, ClassViewModel classViewModel, String day) {
        this.fragment = fragment;
        this.classList = classList;
        this.classViewModel = classViewModel;
        this.day = day;
    }

    @NonNull
    @Override
    public ClassAdapter.ClassViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_class, parent, false);
        return new ClassAdapter.ClassViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClassAdapter.ClassViewHolder holder, int position) {
        Class yogaClass = classList.get(position);
        holder.tvClassName.setText(" Class " + (position + 1));


        holder.btnEdit.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putInt("classId", yogaClass.getId());
            bundle.putInt("courseId", yogaClass.getCourseId());
            bundle.putString("classTeacher", yogaClass.getTeacher());
            bundle.putString("classDate", yogaClass.getDate());
            bundle.putString("classComment", yogaClass.getComment());
            bundle.putString("updateDay", day);

            NavHostFragment.findNavController(fragment)
                    .navigate(R.id.navigation_add_class, bundle);
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (classViewModel != null) {
                classViewModel.deleteClass(yogaClass);
                Log.d("ClassAdapter", "Deleting class with ID: " + yogaClass.getId());
            } else {
                Log.e("ClassAdapter", "null");
            }
        });

    }

    @Override
    public int getItemCount() {
        return classList.size();
    }
    static class ClassViewHolder extends RecyclerView.ViewHolder {

        TextView tvClassName;
        Button btnEdit, btnDelete;

        public ClassViewHolder(@NonNull View itemView) {
            super(itemView);
            tvClassName = itemView.findViewById(R.id.class_name);
            btnEdit = itemView.findViewById(R.id.btn_edit);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }
    }
}
