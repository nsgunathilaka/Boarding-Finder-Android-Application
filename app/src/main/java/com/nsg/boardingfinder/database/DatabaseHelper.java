package com.nsg.boardingfinder.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.nsg.boardingfinder.model.Boarding;
import com.nsg.boardingfinder.model.User;


public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION=3;
    private static final String DATABASE_NAME="boarding_finder.db";
    private static final String TABLE_USER="user";
    private static final String COLUMN_USER_ID="user_id";
    private static final String COLUMN_USER_NAME="user_name";
    private static final String COLUMN_USER_EMAIL="user_email";
    private static final String COLUMN_USER_ADDRESS="user_address";
    private static final String COLUMN_USER_PHONE="user_phone";
    private static final String COLUMN_USER_PASSWORD="password";

    //table name
    public static final String BOARDING_TABLE_NAME = "boarding";
    //Columns
    public static final String ID = "id";
    public static final String B_TITLE = "title";
    public static final String B_LOCATION = "location";
    public static final String B_PRICE = "price";
    public static final String B_CONTACT = "contact";

    public static final String BOARDING_IMAGE = "image";







    private String CREATE_USER_TABLE = "CREATE TABLE "+ TABLE_USER + "("
            + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_USER_NAME + " TEXT,"
            + COLUMN_USER_EMAIL+ " TEXT,"
            + COLUMN_USER_ADDRESS+ " TEXT,"
            + COLUMN_USER_PHONE + " TEXT,"
            + COLUMN_USER_PASSWORD + " TEXT" +
            ")";



    private String CREATE_BOARDING_TABLE = "CREATE TABLE "+ BOARDING_TABLE_NAME + "("
            + ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + B_TITLE + " TEXT,"
            + B_LOCATION+ " TEXT,"
            + B_CONTACT+ " TEXT,"
            + B_PRICE+ " TEXT" +
            ")";




    private String DROP_USER_TABLE = "DROP TABLE IF EXISTS " +TABLE_USER;
    private String DROP_BOARDING_TABLE = "DROP TABLE IF EXISTS " +BOARDING_TABLE_NAME;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_USER_TABLE);
        db.execSQL(CREATE_BOARDING_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_USER_TABLE);
        db.execSQL(DROP_BOARDING_TABLE);
        onCreate(db);
    }

    public void addUser(User user){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_NAME, user.getName());
        values.put(COLUMN_USER_EMAIL, user.getEmail());
        values.put(COLUMN_USER_PASSWORD, user.getPassword());
        values.put(COLUMN_USER_ADDRESS, user.getAddress());
        values.put(COLUMN_USER_PHONE,user.getPhone());
        db.insert(TABLE_USER, null, values);
        db.close();
    }

  /*  public void addBoarding(Boarding boarding){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(B_TITLE, boarding.getTitle());
        values.put(B_LOCATION, boarding.getLocation());
        values.put(B_PRICE, boarding.getPrice());
        values.put(B_CONTACT, boarding.getContact());
        db.insert(BOARDING_TABLE_NAME, null, values);
        db.close();
    }*/



    public boolean checkEmail(String email){
        String [] column = { COLUMN_USER_ID};

        SQLiteDatabase db = this.getReadableDatabase();
        String selection = COLUMN_USER_EMAIL + " = ? ";
        String[] selectionArgs = {email};

        Cursor cursor = db.query(TABLE_USER, column, selection, selectionArgs, null,null,null);
        int cursorCount = cursor.getCount();
        if(cursorCount>0){
            return true;
        }
        else{
            return false;
        }
    }

    //Registered user
    public boolean checkUser(String email, String password){
        String [] column = { COLUMN_USER_ID};

        SQLiteDatabase db = this.getReadableDatabase();
        String selection = COLUMN_USER_EMAIL + " = ?" + " AND " + COLUMN_USER_PASSWORD + " = ?";
        String[] selectionArgs = {email, password};

        Cursor cursor = db.query(TABLE_USER, column, selection, selectionArgs, null,null,null);
        int cursorCount = cursor.getCount();
        if(cursorCount>0){
            return true;
        }
        else{
            return false;
        }
    }

}
