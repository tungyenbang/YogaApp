package com.example.yoga_app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.yoga_app.Course;
import com.example.yoga_app.DatabaseHelper;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SyncManager {

    private DatabaseHelper dbHelper;
    private DatabaseReference firebaseRef, firebaseRefClass;

    private Context context;

    private final String DATABASE_URL = "https://courseapp-bee7c-default-rtdb.europe-west1.firebasedatabase.app/";

    public interface SyncCallback {
        void onSyncSuccess();
        void onSyncFailure();
    }

    public SyncManager(Context context) {
        this.context = context;
        dbHelper = new DatabaseHelper(context);


    }





    // Kiểm tra kết nối mạng
    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    // Đồng bộ dữ liệu từ SQLite lên Firebase
    public void syncData(SyncCallback callback) {
        firebaseRef = FirebaseDatabase.getInstance(DATABASE_URL).getReference("courses");
        if (isNetworkAvailable()) {
            Cursor cursor = dbHelper.getUnsyncedCourses(); // Lấy dữ liệu chưa đồng bộ từ SQLite
            if (cursor.moveToFirst()) {
                do {

                    String day = cursor.getString(cursor.getColumnIndexOrThrow("day_of_week"));
                    String time = cursor.getString(cursor.getColumnIndexOrThrow("time"));
                    String capacity = cursor.getString(cursor.getColumnIndexOrThrow("capacity"));
                    String duration = cursor.getString(cursor.getColumnIndexOrThrow("duration"));
                    String price = cursor.getString(cursor.getColumnIndexOrThrow("price"));
                    String type = cursor.getString(cursor.getColumnIndexOrThrow("class_type"));
                    String description = cursor.getString(cursor.getColumnIndexOrThrow("description"));
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));

                    Course course = new Course( day, time, capacity, duration, price, type, description);
                    course.setId(id);
                    // Đẩy dữ liệu lên Firebase
                    firebaseRef.child(String.valueOf(id)).setValue(course)
                            .addOnSuccessListener(aVoid -> {
                                // Khi đẩy thành công, cập nhật trạng thái đồng bộ trong SQLite
                                dbHelper.updateSyncStatus(id, 1);
                                Log.d("SyncManager", "Course synced to Firebase: " + course.getDay());
                                callback.onSyncSuccess();
                            })
                            .addOnFailureListener(e -> {
                                Log.e("SyncManager", "Failed to sync course: " + course.getDay(), e);
                                callback.onSyncFailure();
                            });

                } while (cursor.moveToNext());
            }
        } else {
            Toast.makeText(context, "No network connection. Data will sync when network is available.", Toast.LENGTH_SHORT).show();
//            callback.onSyncFailure();
        }
    }


    public void deleteCourseFromFirebase(int courseId, SyncCallback callback) {
        firebaseRef = FirebaseDatabase.getInstance(DATABASE_URL).getReference("courses");
        firebaseRefClass = FirebaseDatabase.getInstance(DATABASE_URL).getReference("class");

        if (isNetworkAvailable()) {
            // Xóa khóa học từ Firebase
            firebaseRef.orderByKey().equalTo(String.valueOf(courseId)).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot courseSnapshot : dataSnapshot.getChildren()) {
                        courseSnapshot.getRef().removeValue().addOnSuccessListener(aVoid -> {
                            // Sau khi xóa khóa học, tiếp tục xóa các lớp học liên quan
                            firebaseRefClass.orderByChild("courseId").equalTo(courseId)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot classSnapshot) {
                                            for (DataSnapshot classData : classSnapshot.getChildren()) {
                                                classData.getRef().removeValue().addOnSuccessListener(aVoidClass -> {
                                                    Log.d("SyncManager", "Class deleted from Firebase for courseId: " + courseId);
                                                }).addOnFailureListener(e -> {
                                                    Log.e("SyncManager", "Failed to delete class for courseId: " + courseId, e);
                                                    callback.onSyncFailure();
                                                });
                                            }
                                            callback.onSyncSuccess(); // Thành công xóa khóa học và các lớp liên quan
                                            Toast.makeText(context, "Course and related classes deleted from Firebase.", Toast.LENGTH_SHORT).show();
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                            callback.onSyncFailure();
                                        }
                                    });
                        }).addOnFailureListener(e -> {
                            callback.onSyncFailure();
                            Toast.makeText(context, "Failed to delete course from Firebase.", Toast.LENGTH_SHORT).show();
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    callback.onSyncFailure();
                }
            });
        } else {
            callback.onSyncFailure();
        }
    }


    public void deleteClassFromFirebase(int classId, SyncCallback callback) {
        firebaseRef = FirebaseDatabase.getInstance(DATABASE_URL).getReference("class");
        if (isNetworkAvailable()) {

            firebaseRef.orderByKey().equalTo(String.valueOf(classId)).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot classSnapshot : dataSnapshot.getChildren()) {
                        classSnapshot.getRef().removeValue().addOnSuccessListener(aVoid -> {
                            callback.onSyncSuccess();  // Thành công xóa trên Firebase
                            Toast.makeText(context, "Class deleted from Firebase.", Toast.LENGTH_SHORT).show();
                        }).addOnFailureListener(e -> {
                            callback.onSyncFailure();  // Xảy ra lỗi
                            Toast.makeText(context, "Failed to delete class from Firebase.", Toast.LENGTH_SHORT).show();
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    callback.onSyncFailure();
                }
            });
        } else {
            callback.onSyncFailure();
        }
    }

    public void deleteAllDataFromFirebase(SyncCallback callback) {
        firebaseRef = FirebaseDatabase.getInstance(DATABASE_URL).getReference("courses");
        if (isNetworkAvailable()) {
            firebaseRef.removeValue()
                    .addOnSuccessListener(aVoid -> {
                        Log.d("Firebase", "All data deleted successfully.");
                        Toast.makeText(context, "All data deleted from Firebase.", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Log.e("Firebase", "Failed to delete data.", e);
                        Toast.makeText(context, "Failed to delete data from Firebase.", Toast.LENGTH_SHORT).show();
                    });
        } else {
            callback.onSyncFailure();
        }
    }

    public void synupdate(Course course, SyncCallback callback) {
        firebaseRef = FirebaseDatabase.getInstance(DATABASE_URL).getReference("courses");
        if (isNetworkAvailable()) {
            // Tìm khóa học trong Firebase dựa vào ID của nó
            firebaseRef.child(String.valueOf(course.getId())).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // Nếu khóa học tồn tại trên Firebase, cập nhật dữ liệu
                        firebaseRef.child(String.valueOf(course.getId()))
                                .setValue(course)
                                .addOnSuccessListener(aVoid -> {
                                    // Khi cập nhật thành công, cập nhật trạng thái đồng bộ trong SQLite
                                    dbHelper.updateSyncStatus(course.getId(), 1);
                                    Log.d("SyncManager", "Course updated in Firebase: " + course.getDay());
                                    callback.onSyncSuccess();
                                })
                                .addOnFailureListener(e -> {
                                    Log.e("SyncManager", "Failed to update course: " + course.getDay(), e);
                                    callback.onSyncFailure();
                                });
                    } else {
                        // Khóa học không tồn tại trên Firebase, đẩy dữ liệu mới lên
                        firebaseRef.child(String.valueOf(course.getId()))
                                .setValue(course)
                                .addOnSuccessListener(aVoid -> {
                                    // Cập nhật trạng thái đồng bộ trong SQLite
                                    dbHelper.updateSyncStatus(course.getId(), 1);
                                    Log.d("SyncManager", "Course added to Firebase: " + course.getDay());
                                    callback.onSyncSuccess();
                                })
                                .addOnFailureListener(e -> {
                                    Log.e("SyncManager", "Failed to add course: " + course.getDay(), e);
                                    callback.onSyncFailure();
                                });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e("SyncManager", "Firebase operation cancelled", databaseError.toException());
                    callback.onSyncFailure();
                }
            });
        } else {
            Toast.makeText(context, "No network connection. Sync will retry when network is available.", Toast.LENGTH_SHORT).show();
            callback.onSyncFailure();
        }
    }

    public void synUpdateClass(Class classs, SyncCallback callback) {
        firebaseRef = FirebaseDatabase.getInstance(DATABASE_URL).getReference("class");
        if (isNetworkAvailable()) {
            // Tìm lớp học trong Firebase dựa vào ID của nó
            firebaseRef.child(String.valueOf(classs.getId())).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // Nếu khóa học tồn tại trên Firebase, cập nhật dữ liệu
                        firebaseRef.child(String.valueOf(classs.getId()))
                                .setValue(classs)
                                .addOnSuccessListener(aVoid -> {
                                    // Khi cập nhật thành công, cập nhật trạng thái đồng bộ trong SQLite
                                    dbHelper.updateSyncStatus(classs.getId(), 1);

                                    callback.onSyncSuccess();
                                })
                                .addOnFailureListener(e -> {

                                    callback.onSyncFailure();
                                });
                    } else {
                        // Khóa học không tồn tại trên Firebase, đẩy dữ liệu mới lên
                        firebaseRef.child(String.valueOf(classs.getId()))
                                .setValue(classs)
                                .addOnSuccessListener(aVoid -> {
                                    // Cập nhật trạng thái đồng bộ trong SQLite
                                    dbHelper.updateSyncStatus(classs.getId(), 1);

                                    callback.onSyncSuccess();
                                })
                                .addOnFailureListener(e -> {

                                    callback.onSyncFailure();
                                });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e("SyncManager", "Firebase operation cancelled", databaseError.toException());
                    callback.onSyncFailure();
                }
            });
        } else {
            Toast.makeText(context, "No network connection. Sync will retry when network is available.", Toast.LENGTH_SHORT).show();
            callback.onSyncFailure();
        }
    }

    public void syncDataClass(SyncCallback callback) {
        firebaseRefClass = FirebaseDatabase.getInstance(DATABASE_URL).getReference("class");
        if (isNetworkAvailable()) {
            Cursor cursor = dbHelper.getUnsyncedClass(); // Lấy dữ liệu chưa đồng bộ từ SQLite
            if (cursor.moveToFirst()) {
                do {

                    String teacher = cursor.getString(cursor.getColumnIndexOrThrow("teacher"));
                    String date = cursor.getString(cursor.getColumnIndexOrThrow("date"));
                    String comment = cursor.getString(cursor.getColumnIndexOrThrow("comment"));
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow("class_id"));
                    int courseId = cursor.getInt(cursor.getColumnIndexOrThrow("id"));

                    Class classs = new Class(comment, teacher, date, courseId);
                    classs.setId(id);
                    Log.d("AddCLassFragment", "Received courseId: " + id);
                    // Đẩy dữ liệu lên Firebase
                    firebaseRefClass.child(String.valueOf(id)).setValue(classs)
                            .addOnSuccessListener(aVoid -> {
                                // Khi đẩy thành công, cập nhật trạng thái đồng bộ trong SQLite
                                dbHelper.updateSyncStatusClass(id, 1);
                                Log.d("SyncManager", "Class synced to Firebase: " );
                                callback.onSyncSuccess();
                            })
                            .addOnFailureListener(e -> {
                                Log.e("SyncManager", "Failed to sync class: " , e);
                                callback.onSyncFailure();
                            });

                } while (cursor.moveToNext());
            }
        } else {
            Toast.makeText(context, "No network connection. Data will sync when network is available.", Toast.LENGTH_SHORT).show();
//            callback.onSyncFailure();
        }
    }


}
