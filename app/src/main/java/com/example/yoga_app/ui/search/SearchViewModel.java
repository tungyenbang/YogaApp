package com.example.yoga_app.ui.search;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.yoga_app.Class;
import com.example.yoga_app.DatabaseHelper;

import java.util.List;

public class SearchViewModel extends AndroidViewModel {

    private  MutableLiveData<List<Class>> classList = new MutableLiveData<>();
    private DatabaseHelper databaseHelper;
    private MutableLiveData<Boolean> isSyncSuccessful;
    private String searchText;


    public SearchViewModel(@NonNull Application application) {
        super(application);
        databaseHelper = new DatabaseHelper(application);
        isSyncSuccessful = new MutableLiveData<>();

    }

    public void setSearchText(String searchText) {
        this.searchText = searchText;
        loadClasses();
    }

    private void loadClasses() {
        List<Class> classs = databaseHelper.getClassByTeacher(searchText);
        classList.setValue(classs);
    }

    public  LiveData<List<Class>> getClassList() {
        return classList;
    }
}