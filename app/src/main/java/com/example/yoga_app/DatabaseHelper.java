package com.example.yoga_app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "courses.db";
    private static final int DATABASE_VERSION = 3;

    public static final String TABLE_COURSE = "courses";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_DAY_OF_WEEK = "day_of_week";
    public static final String COLUMN_TIME = "time";
    public static final String COLUMN_CAPACITY = "capacity";
    public static final String COLUMN_DURATION = "duration";
    public static final String COLUMN_PRICE = "price";
    public static final String COLUMN_CLASS_TYPE = "class_type";
    public static final String COLUMN_DESCRIPTION = "description";
    private static final String COL_SYNC_STATUS = "SYNC_STATUS";

    public static final String TABLE_CLASS = "Class";
    public static final String COLUMN_CLASS_ID = "class_id";
    public static final String COLUMN_TEACHER = "teacher";
    public static final String COLUMN_CLASS_COURSE_ID = "id";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_COMMENT = "comment";
    private static final String COL_CLASS_STATUS = "SYNC_STATUS_CLASS";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createCourseTable = "CREATE TABLE " + TABLE_COURSE + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_DAY_OF_WEEK + " TEXT, " +
                COLUMN_TIME + " TEXT, " +
                COLUMN_CAPACITY + " TEXT, " +
                COLUMN_DURATION + " TEXT, " +
                COLUMN_PRICE + " TEXT, " +
                COLUMN_CLASS_TYPE + " TEXT, " +
                COLUMN_DESCRIPTION + " TEXT," +
                COL_SYNC_STATUS + " INTEGER DEFAULT 0)";
        db.execSQL(createCourseTable);

        String createClassTable = "CREATE TABLE " + TABLE_CLASS + " (" +
                COLUMN_CLASS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TEACHER + " TEXT, " +
                COLUMN_CLASS_COURSE_ID + " INTEGER, " +
                COLUMN_DATE + " TEXT, " +
                COLUMN_COMMENT + " TEXT," +
                COL_CLASS_STATUS + " INTEGER DEFAULT 0," +
                "FOREIGN KEY(" + COLUMN_CLASS_COURSE_ID + ") REFERENCES " + TABLE_COURSE + "(" + COLUMN_ID + ")  ON DELETE CASCADE)";
        db.execSQL(createClassTable);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE " + TABLE_COURSE + " ADD COLUMN " + COL_SYNC_STATUS + " INTEGER DEFAULT 0");
            db.execSQL("ALTER TABLE " + TABLE_CLASS + " ADD COLUMN " + COL_CLASS_STATUS + " INTEGER DEFAULT 0");
        }

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CLASS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COURSE);
        onCreate(db);
    }

    public Cursor getUnsyncedCourses() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM courses WHERE SYNC_STATUS = 0", null);
    }

    public Cursor getUnsyncedClass() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM Class WHERE SYNC_STATUS_CLASS = 0", null);
    }

    public void updateSyncStatus(int id, int syncStatus) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("SYNC_STATUS", syncStatus);
        db.update("courses", contentValues, "id = ?", new String[]{String.valueOf(id)});
    }
    public void updateSyncStatusClass(int id, int syncStatus) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("SYNC_STATUS_CLASS", syncStatus);
        db.update("Class", contentValues, "class_id = ?", new String[]{String.valueOf(id)});
    }


    public long addCourse(Course course) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_DAY_OF_WEEK, course.getDay());
        values.put(COLUMN_TIME, course.getTime());
        values.put(COLUMN_CAPACITY, course.getCapacity());
        values.put(COLUMN_DURATION, course.getDuration());
        values.put(COLUMN_PRICE, course.getPrice());
        values.put(COLUMN_CLASS_TYPE, course.getType());
        values.put(COLUMN_DESCRIPTION, course.getDescription());

        return db.insert(TABLE_COURSE, null, values);
    }

    public long addClass(Class classs) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_TEACHER, classs.getTeacher());
        values.put(COLUMN_DATE, classs.getDate());
        values.put(COLUMN_COMMENT, classs.getComment());
        values.put(COLUMN_CLASS_COURSE_ID, classs.getCourseId());



        return db.insert(TABLE_CLASS, null, values);
    }

    public boolean updateCourse(Course course) {
        // Lấy đối tượng SQLiteDatabase
        SQLiteDatabase db = this.getWritableDatabase();

        // Tạo ContentValues để chứa dữ liệu cần cập nhật
        ContentValues values = new ContentValues();
        values.put(COLUMN_DAY_OF_WEEK, course.getDay());
        values.put(COLUMN_TIME, course.getTime());
        values.put(COLUMN_CAPACITY, course.getCapacity());
        values.put(COLUMN_DURATION, course.getDuration());
        values.put(COLUMN_PRICE, course.getPrice());
        values.put(COLUMN_CLASS_TYPE, course.getType());
        values.put(COLUMN_DESCRIPTION, course.getDescription());

        // Thực hiện cập nhật dữ liệu
        int result = db.update(TABLE_COURSE, values, COLUMN_ID + " = ?", new String[]{String.valueOf(course.getId())});

        // Kiểm tra kết quả, nếu số dòng cập nhật > 0 thì trả về true
        return result > 0;
    }

    public boolean updateClass(Class classs) {
        // Lấy đối tượng SQLiteDatabase
        SQLiteDatabase db = this.getWritableDatabase();

        // Tạo ContentValues để chứa dữ liệu cần cập nhật
        ContentValues values = new ContentValues();
        values.put(COLUMN_TEACHER, classs.getTeacher());
        values.put(COLUMN_DATE, classs.getDate());
        values.put(COLUMN_COMMENT, classs.getComment());
        values.put(COLUMN_CLASS_COURSE_ID, classs.getCourseId());

        // Thực hiện cập nhật dữ liệu
        int result = db.update(TABLE_CLASS, values, COLUMN_CLASS_ID + " = ?", new String[]{String.valueOf(classs.getId())});

        // Kiểm tra kết quả, nếu số dòng cập nhật > 0 thì trả về true
        return result > 0;
    }



    public List<Course> getAllCourses() {
        List<Course> courseList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_COURSE, null);

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



                Course course = new Course(day, time, capacity, duration, price, type, description);
                courseList.add(course);

                course.setId(id);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return courseList;
    }

    public List<Class> getAllClasses(int courseId) {
        List<Class> classList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_CLASS + " WHERE " + COLUMN_CLASS_COURSE_ID + " = ?",
                new String[]{String.valueOf(courseId)});

        if (cursor.moveToFirst()) {
            do {
                String teacher = cursor.getString(cursor.getColumnIndexOrThrow("teacher"));
                String date = cursor.getString(cursor.getColumnIndexOrThrow("date"));
                String comment = cursor.getString(cursor.getColumnIndexOrThrow("comment"));
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("class_id"));
                int courseID = cursor.getInt(cursor.getColumnIndexOrThrow("id"));



                Class classs = new Class( comment, teacher, date, courseID);
                classs.setCourseId(courseID);
                classs.setId(id);
                classList.add(classs);

            } while (cursor.moveToNext());
        }

        cursor.close();
        return classList;
    }

    public int deleteCourse(Course course) {
        SQLiteDatabase db = this.getWritableDatabase();
        // Xóa lớp học (class) liên quan đến khóa học
        db.delete(TABLE_CLASS, COLUMN_CLASS_COURSE_ID + " = ?", new String[]{String.valueOf(course.getId())});
        // Xóa khóa học
        return db.delete(TABLE_COURSE, COLUMN_ID + " = ?", new String[]{String.valueOf(course.getId())});
    }

    public void deleteAllCourses() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CLASS, null, null); // Xóa tất cả lớp học liên quan
        db.delete(TABLE_COURSE, null, null); // Xóa tất cả khóa học
    }

    public int deleteClass(Class classs) {
        SQLiteDatabase db = this.getWritableDatabase();
        // Xóa lớp học
        return db.delete(TABLE_CLASS, COLUMN_CLASS_ID + " = ?", new String[]{String.valueOf(classs.getId())});
    }

    public List<Class> getClassByTeacher(String teacherName) {
        List<Class> classList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String searchQuery = "%" + teacherName.toLowerCase() + "%";
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_CLASS + " WHERE LOWER(" + COLUMN_TEACHER + ") LIKE ?",
                new String[]{String.valueOf(searchQuery)});

        if (cursor.moveToFirst()) {
            do {
                String teacher = cursor.getString(cursor.getColumnIndexOrThrow("teacher"));
                String date = cursor.getString(cursor.getColumnIndexOrThrow("date"));
                String comment = cursor.getString(cursor.getColumnIndexOrThrow("comment"));
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("class_id"));
                int courseID = cursor.getInt(cursor.getColumnIndexOrThrow("id"));



                Class classs = new Class( comment, teacher, date, courseID);
                classs.setCourseId(courseID);
                classs.setId(id);
                classList.add(classs);

            } while (cursor.moveToNext());
        }

        cursor.close();
        return classList;
    }

}
