package com.example.yoga_app.ui.home;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.yoga_app.Course;
import com.example.yoga_app.DatabaseHelper;
import com.example.yoga_app.SyncManager;

import java.util.ArrayList;
import java.util.List;

public class HomeViewModel extends AndroidViewModel {

    private MutableLiveData<List<Course>> courseList = new MutableLiveData<>();
    private DatabaseHelper databaseHelper;
    private SyncManager syncManager;
    private MutableLiveData<Boolean> isSyncSuccessful;
    public HomeViewModel(@NonNull Application application) {
        super(application);
        databaseHelper = new DatabaseHelper(application); // Initialize SQLite helper
        isSyncSuccessful = new MutableLiveData<>();
        syncManager = new SyncManager(application);
        loadCourses();
    }

    // Hàm để load danh sách khóa học từ SQLite
    private void loadCourses() {
        List<Course> courses = databaseHelper.getAllCourses(); // Lấy danh sách từ database
        courseList.setValue(courses); // Cập nhật LiveData với danh sách khóa học
    }

    public LiveData<List<Course>> getCourseList() {
        return courseList;
    }

    // Hàm xóa khóa học khỏi LiveData và SQLite
    public void deleteCourse(Course course) {
        databaseHelper.deleteCourse(course); // Xóa khỏi SQLite
        List<Course> updatedList = courseList.getValue();
        if (updatedList != null) {
            updatedList.remove(course); // Xóa khỏi danh sách hiện tại
            courseList.setValue(updatedList); // Cập nhật lại LiveData
        }
        syncManager.deleteCourseFromFirebase(course.getId(), new SyncManager.SyncCallback() {
            @Override
            public void onSyncSuccess() {
                isSyncSuccessful.setValue(true);  // Báo thành công

            }

            @Override
            public void onSyncFailure() {
                isSyncSuccessful.setValue(false); // Báo lỗi
            }
        });
    }

    public void deleteAllCourses() {
        databaseHelper.deleteAllCourses(); // Xóa tất cả các khóa học khỏi SQLite
        courseList.setValue(new ArrayList<>()); // Cập nhật lại LiveData với danh sách rỗng
        syncManager.deleteAllDataFromFirebase(new SyncManager.SyncCallback() {
            @Override
            public void onSyncSuccess() {
                isSyncSuccessful.setValue(true);  // Báo thành công
            }

            @Override
            public void onSyncFailure() {
                isSyncSuccessful.setValue(false); // Báo lỗi
            }
        });
    }

}
