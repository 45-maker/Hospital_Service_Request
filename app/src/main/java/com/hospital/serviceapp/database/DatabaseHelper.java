package com.hospital.serviceapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.hospital.serviceapp.models.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "HospitalService.db";
    private static final int DATABASE_VERSION = 1;

    // User table
    private static final String TABLE_USER = "users";
    private static final String COLUMN_USER_ID = "id";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_ROLE = "role";

    // Service table
    private static final String TABLE_SERVICE = "services";
    private static final String COLUMN_SERVICE_ID = "id";
    private static final String COLUMN_SERVICE_CODE = "service_code";
    private static final String COLUMN_SERVICE_NAME = "service_name";

    // Request table
    private static final String TABLE_REQUEST = "requests";
    private static final String COLUMN_REQUEST_ID = "id";
    private static final String COLUMN_USER_ID_FK = "user_id";
    private static final String COLUMN_SERVICE_ID_FK = "service_id";
    private static final String COLUMN_WARD_NUMBER = "ward_number";
    private static final String COLUMN_BED_NUMBER = "bed_number";
    private static final String COLUMN_NOTES = "notes";
    private static final String COLUMN_STATUS = "status";
    private static final String COLUMN_TIMESTAMP = "timestamp";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create users table
        String CREATE_USER_TABLE = "CREATE TABLE " + TABLE_USER + "("
                + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_USERNAME + " TEXT UNIQUE,"
                + COLUMN_EMAIL + " TEXT UNIQUE,"
                + COLUMN_PASSWORD + " TEXT,"
                + COLUMN_ROLE + " TEXT DEFAULT 'normal')";
        db.execSQL(CREATE_USER_TABLE);

        // Create services table
        String CREATE_SERVICE_TABLE = "CREATE TABLE " + TABLE_SERVICE + "("
                + COLUMN_SERVICE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_SERVICE_CODE + " TEXT UNIQUE,"
                + COLUMN_SERVICE_NAME + " TEXT)";
        db.execSQL(CREATE_SERVICE_TABLE);

        // Create requests table
        String CREATE_REQUEST_TABLE = "CREATE TABLE " + TABLE_REQUEST + "("
                + COLUMN_REQUEST_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_USER_ID_FK + " INTEGER,"
                + COLUMN_SERVICE_ID_FK + " INTEGER,"
                + COLUMN_WARD_NUMBER + " TEXT,"
                + COLUMN_BED_NUMBER + " TEXT,"
                + COLUMN_NOTES + " TEXT,"
                + COLUMN_STATUS + " TEXT DEFAULT 'pending',"
                + COLUMN_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP,"
                + "FOREIGN KEY(" + COLUMN_USER_ID_FK + ") REFERENCES " + TABLE_USER + "(" + COLUMN_USER_ID + "),"
                + "FOREIGN KEY(" + COLUMN_SERVICE_ID_FK + ") REFERENCES " + TABLE_SERVICE + "(" + COLUMN_SERVICE_ID + "))";
        db.execSQL(CREATE_REQUEST_TABLE);

        // Insert default services
        insertDefaultServices(db);

        // Insert default admin
        insertDefaultAdmin(db);
    }

    private void insertDefaultServices(SQLiteDatabase db) {
        String[][] services = {
                {"CL001", "Cleaning"},
                {"EP002", "Equipment assistance"},
                {"LC001", "Linen change"},
                {"PS001", "Porter services"}
        };

        for (String[] service : services) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_SERVICE_CODE, service[0]);
            values.put(COLUMN_SERVICE_NAME, service[1]);
            db.insert(TABLE_SERVICE, null, values);
        }
    }

    private void insertDefaultAdmin(SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, "Nelly");
        values.put(COLUMN_EMAIL, "nelly@hospital.com");
        values.put(COLUMN_PASSWORD, "nelly123");
        values.put(COLUMN_ROLE, "admin");
        db.insert(TABLE_USER, null, values);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_REQUEST);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SERVICE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        onCreate(db);
    }

    // User methods
    public boolean registerUser(String username, String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_EMAIL, email);
        values.put(COLUMN_PASSWORD, password);

        long result = db.insert(TABLE_USER, null, values);
        db.close();
        return result != -1;
    }

    public User loginUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_USER + " WHERE " + COLUMN_USERNAME + "=? AND " + COLUMN_PASSWORD + "=?";
        Cursor cursor = db.rawQuery(query, new String[]{username, password});

        if (cursor.moveToFirst()) {
            User user = new User(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4)
            );
            cursor.close();
            db.close();
            return user;
        }
        cursor.close();
        db.close();
        return null;
    }

    // Service methods
    public List<Service> getAllServices() {
        List<Service> serviceList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_SERVICE;
        Cursor cursor = db.rawQuery(query, null);

        while (cursor.moveToNext()) {
            Service service = new Service(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2)
            );
            serviceList.add(service);
        }
        cursor.close();
        db.close();
        return serviceList;
    }

    public boolean addService(String code, String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_SERVICE_CODE, code);
        values.put(COLUMN_SERVICE_NAME, name);

        long result = db.insert(TABLE_SERVICE, null, values);
        db.close();
        return result != -1;
    }

    public boolean removeService(int serviceId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_SERVICE, COLUMN_SERVICE_ID + "=?", new String[]{String.valueOf(serviceId)});
        db.close();
        return result > 0;
    }

    // Request methods
    public boolean submitRequest(int userId, int serviceId, String wardNumber, String bedNumber, String notes) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_ID_FK, userId);
        values.put(COLUMN_SERVICE_ID_FK, serviceId);
        values.put(COLUMN_WARD_NUMBER, wardNumber);
        values.put(COLUMN_BED_NUMBER, bedNumber);
        values.put(COLUMN_NOTES, notes);

        long result = db.insert(TABLE_REQUEST, null, values);
        db.close();
        return result != -1;
    }

    public List<Request> getAllRequests() {
        List<Request> requestList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT r.*, u.username, s.service_name FROM " + TABLE_REQUEST + " r "
                + "JOIN " + TABLE_USER + " u ON r." + COLUMN_USER_ID_FK + "=u." + COLUMN_USER_ID + " "
                + "JOIN " + TABLE_SERVICE + " s ON r." + COLUMN_SERVICE_ID_FK + "=s." + COLUMN_SERVICE_ID
                + " ORDER BY r." + COLUMN_TIMESTAMP + " DESC";
        Cursor cursor = db.rawQuery(query, null);

        while (cursor.moveToNext()) {
            Request request = new Request(
                    cursor.getInt(0),
                    cursor.getInt(1),
                    cursor.getInt(2),
                    cursor.getString(3),
                    cursor.getString(4),
                    cursor.getString(5),
                    cursor.getString(6),
                    cursor.getString(7),
                    cursor.getString(8),
                    cursor.getString(9)
            );
            requestList.add(request);
        }
        cursor.close();
        db.close();
        return requestList;
    }

    public boolean deleteRequest(int requestId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_REQUEST, COLUMN_REQUEST_ID + "=?", new String[]{String.valueOf(requestId)});
        db.close();
        return result > 0;
    }

    // User management for admin
    public List<User> getAllUsers() {
        List<User> userList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_USER;
        Cursor cursor = db.rawQuery(query, null);

        while (cursor.moveToNext()) {
            User user = new User(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4)
            );
            userList.add(user);
        }
        cursor.close();
        db.close();
        return userList;
    }

    public boolean deleteUser(int userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_USER, COLUMN_USER_ID + "=?", new String[]{String.valueOf(userId)});
        db.close();
        return result > 0;
    }
}
