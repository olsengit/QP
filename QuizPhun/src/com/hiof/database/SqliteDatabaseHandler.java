package com.hiof.database;

import java.util.ArrayList;
import java.util.List;

import com.hiof.objects.User;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SqliteDatabaseHandler extends SQLiteOpenHelper {
	
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "userManager";
    private static final String TABLE_USERNAME = "username";
 
    private static final String KEY_ID = "id";
    private static final String KEY_USERNAME = "username";
 
    public SqliteDatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USERNAME_TABLE = "CREATE TABLE " + TABLE_USERNAME + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_USERNAME + " TEXT"  + ")";
        db.execSQL(CREATE_USERNAME_TABLE);
    }
 
    //If we wanna upgrade databaseversion
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERNAME);
        onCreate(db);
    }
 
    public void addUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
 
        ContentValues values = new ContentValues();
        values.put(KEY_USERNAME, user.getUserName());

        db.insert(TABLE_USERNAME, null, values);
        db.close();
    }
 
    public User getUser(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
 
        Cursor cursor = db.query(TABLE_USERNAME, new String[] { KEY_ID,
                KEY_USERNAME }, KEY_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
 
        User user = new User(Integer.parseInt(cursor.getString(0)),cursor.getString(1));
        return user;
    }
     
    public List<User> getAllUsers() {
        List<User> userList = new ArrayList<User>();
        String selectQuery = "SELECT  * FROM " + TABLE_USERNAME;
 
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
 
        if (cursor.moveToFirst()) {
            do {
                User user = new User();
                user.setId(Integer.parseInt(cursor.getString(0)));
                user.setUserName(cursor.getString(1));
                userList.add(user);
            } while (cursor.moveToNext());
        }
        return userList;
    }
 
    public int updateUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
 
        ContentValues values = new ContentValues();
        values.put(KEY_USERNAME, user.getUserName());
 
        return db.update(TABLE_USERNAME, values, KEY_ID + " = ?",
                new String[] { String.valueOf(user.getId()) });
    }
 
    public void deleteUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_USERNAME, KEY_ID + " = ?",
                new String[] { String.valueOf(user.getId()) });
        db.close();
    }
 
    public int getUserCount() {
        String countQuery = "SELECT  * FROM " + TABLE_USERNAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        return cursor.getCount();
    }
}



