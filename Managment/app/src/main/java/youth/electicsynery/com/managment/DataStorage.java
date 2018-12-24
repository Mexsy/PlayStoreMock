package youth.electicsynery.com.managment;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Emeka Chukumah on 04/02/2016.
 */
public class DataStorage extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "Youth.db";
    private static final String TABLE_NAME = "users";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_ORG = "organisation";
    private static final String COLUMN_EMAIL = "email";

    private static final String TABLE_NAME_2 = "phoneNumbers";
    private static final String COLUMN_NUMBER = "number";

    private static final String TABLE_NAME_3 = "Youth";
    private static final String COLUMN_YOUTH_NAME = "name";

    private static final String TABLE_NAME_4 = "homeAddress";
    private static final String COLUMN_ADDRESS = "address";

    private static final String TABLE_NAME_5 = "emailAddress";

    private static final String TABLE_NAME_6 = "ID";
    private static final String COLUMN_YOUTH_ID = "youth_id";


    private static final String CREATE_TABLE = "create table users (id integer primary key not null , "+
            "userid integer not null, email text not null, organisation text not null);";

    private static final String CREATE_TABLE_2 = "create table invoice (id_invoice integer primary key not null , "+
            "tin text not null, lastname text not null, invoice_amount text not null, amount_paid text not null, amount_outstanding text not null);";

    private static final String CREATE_TABLE_3 = "create table Youth (id integer primary key not null , "+
            "name text unique not null);";

    private static final String CREATE_TABLE_4 = "create table phoneNumbers (id integer PRIMARY KEY AUTOINCREMENT not null , "+
            "number text unique, name text not null, FOREIGN KEY(name) REFERENCES " +TABLE_NAME_3 + " (name));";

    private static final String CREATE_TABLE_5 = "create table homeAddress (id integer PRIMARY KEY AUTOINCREMENT not null , "+
            "address text unique, name text not null, FOREIGN KEY(name) REFERENCES " +TABLE_NAME_3 + " (name));";

    private static final String CREATE_TABLE_6 = "create table emailAddress (id integer PRIMARY KEY AUTOINCREMENT not null , "+
            "email text unique, name text not null, FOREIGN KEY(name) REFERENCES " +TABLE_NAME_3 + " (name));";

    /*private static final String CREATE_TABLE_7 = "create table ID (id integer PRIMARY KEY AUTOINCREMENT not null , "+
            "youth_id int unique not null);";
    */

    private SQLiteDatabase db;

    public DataStorage(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE);
        sqLiteDatabase.execSQL(CREATE_TABLE_2);
        sqLiteDatabase.execSQL(CREATE_TABLE_3);
        sqLiteDatabase.execSQL(CREATE_TABLE_4);
        sqLiteDatabase.execSQL(CREATE_TABLE_5);
        sqLiteDatabase.execSQL(CREATE_TABLE_6);
        //sqLiteDatabase.execSQL(CREATE_TABLE_7);
        this.db = sqLiteDatabase;
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        String query = "DROP TABLE IF EXISTS" + TABLE_NAME;
        String query2 = "DROP TABLE IF EXISTS" + TABLE_NAME_2;
        String query3 = "DROP TABLE IF EXISTS" + TABLE_NAME_3;
        String query4 = "DROP TABLE IF EXISTS" + TABLE_NAME_4;
        String query5 = "DROP TABLE IF EXISTS" + TABLE_NAME_5;
        String query6 = "DROP TABLE IF EXISTS" + TABLE_NAME_6;
        sqLiteDatabase.execSQL(query);
        sqLiteDatabase.execSQL(query2);
        sqLiteDatabase.execSQL(query3);
        sqLiteDatabase.execSQL(query4);
        sqLiteDatabase.execSQL(query5);
        sqLiteDatabase.execSQL(query6);
        this.onCreate(sqLiteDatabase);

    }

    void insertData(String value){
        db = getWritableDatabase();
        ContentValues values = new ContentValues();


        String query = "select * from Youth";
        Cursor cursor = db.rawQuery(query, null);
        values.put(COLUMN_YOUTH_NAME, value);

        db.insert(TABLE_NAME_3,null,values);
        //db.insertWithOnConflict(TABLE_NAME_3, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        cursor.close();
    }

    public int getFreeID(){
        db = getWritableDatabase();

        String query = "select * from Youth";
        Cursor cursor = db.rawQuery(query, null);
        int count = cursor.getCount();
        count++;
        cursor.close();
        return count;
    }

    void insertDataNumbers(String value, String name){
        db = getWritableDatabase();
        ContentValues values = new ContentValues();

        String query = "select * from phoneNumbers";
        Cursor cursor = db.rawQuery(query, null);
        values.put(COLUMN_NUMBER, value);
        values.put(COLUMN_YOUTH_NAME, name);

        db.insert(TABLE_NAME_2,null,values);
        //db.insertWithOnConflict(TABLE_NAME_3, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        cursor.close();
    }

    void insertDataYouthID(long value){
        db = getWritableDatabase();
        ContentValues values = new ContentValues();

        String query = "select * from " + TABLE_NAME_6;
        Cursor cursor = db.rawQuery(query, null);
        values.put(COLUMN_YOUTH_ID, value);

        db.insert(TABLE_NAME_6,null,values);
        //db.insertWithOnConflict(TABLE_NAME_3, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        cursor.close();
    }

    void insertDataEmail(String value, String name){
        db = getWritableDatabase();
        ContentValues values = new ContentValues();

        String query = "select * from emailAddress";
        Cursor cursor = db.rawQuery(query, null);
        int count = cursor.getCount();

        values.put(COLUMN_EMAIL, value);
        values.put(COLUMN_YOUTH_NAME, name);

        db.insert(TABLE_NAME_5,null,values);
        //db.insertWithOnConflict(TABLE_NAME_3, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        cursor.close();
    }

    void insertDataAddress(String value, String name){
        db = getWritableDatabase();
        ContentValues values = new ContentValues();

        String query = "select * from homeAddress";
        Cursor cursor = db.rawQuery(query, null);
        int count = cursor.getCount();

        values.put(COLUMN_ADDRESS, value);
        values.put(COLUMN_YOUTH_NAME, name);

        db.insert(TABLE_NAME_4,null,values);
        //db.insertWithOnConflict(TABLE_NAME_3, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        cursor.close();
    }

    void insertRegDetails(String email, String Org){
        db = getWritableDatabase();
        ContentValues values = new ContentValues();

        String query = "select * from users";
        Cursor cursor = db.rawQuery(query, null);
        int count = cursor.getCount();

        values.put(COLUMN_ID, count);
        values.put(COLUMN_EMAIL, email);
        values.put(COLUMN_ORG, Org);

        db.insert(TABLE_NAME,null,values);
        //db.insertWithOnConflict(TABLE_NAME_3, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        cursor.close();
    }

    /**
     * Getting all labels
     * returns list of labels
     * */
    public List<String> getAllNames(){
        List<String> labels = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT name FROM " + TABLE_NAME_3;

        db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                //String id = cursor.getString(1);
                labels.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }

        // closing connection
        cursor.close();
        //db.close();

        // returning lables
        return labels;
    }

    boolean checkValueExist(String value) {
        db = this.getWritableDatabase();
        String query = "select name from Youth";
        Cursor cursor = db.rawQuery(query, null);
        String dbVal;
        boolean s = false;
        if (cursor.moveToFirst()) {
                do {
                    dbVal = cursor.getString(0);
                    if (dbVal.equals(value)){
                        s = true;
                    }
                }
                while (cursor.moveToNext());

        }
        cursor.close();
        return s;
    }

    public String getNumber(String name){
        // Select All Query
        String selectQuery = "SELECT number,name FROM " + TABLE_NAME_2+" WHERE name ="+"'"+name+"'";

        db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        String number = "";
        String youth_name;

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                youth_name = cursor.getString(1);
                if (youth_name.equals(name)){
                    number = cursor.getString(0);
                    break;
                }
            } while (cursor.moveToNext());
        }

        // closing connection
        cursor.close();
        return number;
    }

    public String getEmail(String name){
        // Select All Query
        String selectQuery = "SELECT email,name FROM " + TABLE_NAME_5+" WHERE name ="+"'"+name+"'";

        db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        String number = "";
        String youth_email;

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                youth_email = cursor.getString(1);
                if (youth_email.equals(name)){
                    number = cursor.getString(0);
                    break;
                }
            } while (cursor.moveToNext());
        }

        // closing connection
        cursor.close();
        return number;
    }

    public String getEmailID(String id){
        // Select All Query
        String selectQuery = "SELECT email,id FROM " + TABLE_NAME_5+" WHERE name ="+"'"+id+"'";

        db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        String number = "";
        String youth_id;

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                youth_id = cursor.getString(1);
                if (youth_id.equals(id)){
                    number = cursor.getString(0);
                    break;
                }
            } while (cursor.moveToNext());
        }

        // closing connection
        cursor.close();
        return number;
    }

    public List<String> getAllEmail(){
        // Select All Query
        String selectQuery = "SELECT email FROM " + TABLE_NAME_5;
        db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        List<String> emails = new ArrayList<>();
        String youth_email;

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                youth_email = cursor.getString(0);
                emails.add(youth_email);
            } while (cursor.moveToNext());
        }
        // closing connection
        cursor.close();
        return emails;
    }

    public List<String> getPhoneNumbers() {

        List<String> labels = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT number FROM " + TABLE_NAME_2;

        db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                //String id = cursor.getString(1);
                labels.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }

        // closing connection
        cursor.close();
        //db.close();

        // returning lables
        return labels;
    }

    public List<String> getAllNumbers() {
        return null;
    }
}
