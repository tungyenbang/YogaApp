package com.example.yoga_app.ui.classes;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.yoga_app.Class;
import com.example.yoga_app.Course;
import com.example.yoga_app.DatabaseHelper;
import com.example.yoga_app.SyncManager;

import java.util.List;

public class ClassViewModel extends AndroidViewModel  {

    private  MutableLiveData<List<Class>> classList = new MutableLiveData<>();
    private DatabaseHelper databaseHelper;
    private SyncManager syncManager;
    private MutableLiveData<Boolean> isSyncSuccessful;
    private int courseId;

    public ClassViewModel(@NonNull Application application) {
        super(application);
        databaseHelper = new DatabaseHelper(application); // Initialize SQLite helper
        isSyncSuccessful = new MutableLiveData<>();
        syncManager = new SyncManager(application);

    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
        loadClasses();

    }

    private void loadClasses() {
        List<Class> classs = databaseHelper.getAllClasses(courseId); // Lấy danh sách từ database
        for (Class c : classs) {
            Log.d("ClassViewModel", "Loaded class ID: " + c.getId());
        }
        classList.setValue(classs); // Cập nhật LiveData với danh sách lớp học
    }

    public  LiveData<List<Class>> getClassList() {
        return classList;
    }

    public void addClasses(Class classes){
        long id = databaseHelper.addClass(classes);

        Toast.makeText(getApplication(), "Class has been created with id: " + id,
                Toast.LENGTH_LONG
        ).show();

        if (id != -1) {

            Toast.makeText(getApplication(), "Class added to SQLite with ID: " + id, Toast.LENGTH_LONG).show();
            // Sau khi thêm vào SQLite, kiểm tra mạng và đồng bộ



            syncManager.syncDataClass(new SyncManager.SyncCallback() {
                @Override
                public void onSyncSuccess() {
                    isSyncSuccessful.setValue(true);  // Báo thành công
                }

                @Override
                public void onSyncFailure() {
                    isSyncSuccessful.setValue(false); // Báo lỗi
                }
            });
        } else {
//            isSyncSuccessful.setValue(false);
            Toast.makeText(getApplication(), "Failed to add clas to SQLite", Toast.LENGTH_SHORT).show();
        }
    }

    public void deleteClass(Class classs) {
        databaseHelper.deleteClass(classs); // Xóa khỏi SQLite

        List<Class> updatedList = classList.getValue();
        if (updatedList != null) {
            updatedList.remove(classs); // Xóa khỏi danh sách hiện tại
            classList.setValue(updatedList); // Cập nhật lại LiveData
        }
        syncManager.deleteClassFromFirebase(classs.getId(), new SyncManager.SyncCallback() {


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

    public void updateClass(Class classs){
        boolean isUpdated = databaseHelper.updateClass(classs);

        if(isUpdated) {
            Toast.makeText(getApplication(), "Class updated in SQLite", Toast.LENGTH_LONG).show();

            syncManager.synUpdateClass(classs, new SyncManager.SyncCallback() {
                @Override
                public void onSyncSuccess() {
                    isSyncSuccessful.setValue(true);  // Notify success
                    Toast.makeText(getApplication(), "Class update to Firebase", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSyncFailure() {
                    isSyncSuccessful.setValue(false); // Notify failure
                    Toast.makeText(getApplication(), "Failed to update class to Firebase", Toast.LENGTH_SHORT).show();
                }

            });
        }    else {
            // If the update in SQLite failed, show an error toast
            Toast.makeText(getApplication(), "Failed to update class in SQLite", Toast.LENGTH_SHORT).show();
            isSyncSuccessful.setValue(false);
        }
    }
}
