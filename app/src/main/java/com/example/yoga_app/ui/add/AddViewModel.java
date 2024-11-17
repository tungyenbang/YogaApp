package com.example.yoga_app.ui.add;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import android.app.Application;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import com.example.yoga_app.SyncManager;

import com.example.yoga_app.Course;
import com.example.yoga_app.DatabaseHelper;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class AddViewModel extends AndroidViewModel {

    private DatabaseHelper dbHelper;

    private SyncManager syncManager;

    private List<Course> courseList;

    private MutableLiveData<Boolean> isSyncSuccessful; // LiveData to observe sync status

    private final String DATABASE_URL = "https://testapp-d619f-default-rtdb.europe-west1.firebasedatabase.app/";
    FirebaseDatabase database;

    public AddViewModel(@NonNull Application application) {
        super(application);
        dbHelper = new DatabaseHelper(application); // Initialize SQLite helper
        isSyncSuccessful = new MutableLiveData<>();
        syncManager = new SyncManager(application);
    }

    // Getter for LiveData to observe sync status
    public LiveData<Boolean> getIsSyncSuccessful() {
        return isSyncSuccessful;
    }

    // Method to add course to SQLite and sync to Firebase
    public void addCourseAndSync(Course course) {
        // Step 1: Add to SQLite
        long id = dbHelper.addCourse(course);

        // Shows a toast with the automatically generated id
        Toast.makeText(getApplication(), "Course has been created with id: " + id,
                Toast.LENGTH_LONG
        ).show();

        if (id != -1) {
            Toast.makeText(getApplication(), "Course added to SQLite with ID: " + id, Toast.LENGTH_LONG).show();
            // Sau khi thêm vào SQLite, kiểm tra mạng và đồng bộ

//            syncManager.syncData();

            syncManager.syncData(new SyncManager.SyncCallback() {
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
            Toast.makeText(getApplication(), "Failed to add course to SQLite", Toast.LENGTH_SHORT).show();
        }
    }

    // Method to update course in SQLite and sync to Firebase
    public void updateCourseAndSync(Course course) {
        // Step 1: Update course in SQLite
        boolean isUpdated = dbHelper.updateCourse(course);

        if (isUpdated) {
            // If the course was updated in SQLite successfully, show a success toast
            Toast.makeText(getApplication(), "Course updated in SQLite", Toast.LENGTH_LONG).show();

            // Step 2: Sync updated course data to Firebase
            syncManager.synupdate(course, new SyncManager.SyncCallback() {
                @Override
                public void onSyncSuccess() {
                    isSyncSuccessful.setValue(true);  // Notify success
                    Toast.makeText(getApplication(), "Course synced to Firebase", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSyncFailure() {
                    isSyncSuccessful.setValue(false); // Notify failure
                    Toast.makeText(getApplication(), "Failed to sync course to Firebase", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // If the update in SQLite failed, show an error toast
            Toast.makeText(getApplication(), "Failed to update course in SQLite", Toast.LENGTH_SHORT).show();
            isSyncSuccessful.setValue(false);
        }
    }



}