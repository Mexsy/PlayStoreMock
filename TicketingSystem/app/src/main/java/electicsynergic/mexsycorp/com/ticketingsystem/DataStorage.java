package electicsynergic.mexsycorp.com.ticketingsystem;

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
    private static final String DATABASE_NAME = "Ticket.db";
    private static final String TABLE_NAME = "users";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_ORG = "organisation";
    private static final String COLUMN_EMAIL = "email";

    private static final String TABLE_NAME_2 = "Ticket";
    private static final String COLUMN_ID_TICKET = "ticketID";
    private static final String COLUMN_STATE_CODE= "state_code";
    private static final String COLUMN_TICKET_NUMBER = "ticket_number";
    private static final String COLUMN_STATUS = "ticket_number";

    private static final String TABLE_NAME_3 = "StateCodePrefix";
    private static final String COLUMN_PREFIX = "prefix";


    private static final String CREATE_TABLE = "create table users (id integer primary key not null , "+
            "userid integer not null, email text not null, organisation text not null);";

    private static final String CREATE_TABLE_2 = "create table invoice (id_invoice integer primary key not null , "+
            "tin text not null, lastname text not null, invoice_amount text not null, amount_paid text not null, amount_outstanding text not null);";

    private static final String CREATE_TABLE_3 = "create table StateCodePrefix (id integer primary key not null , "+
            "prefix text unique not null);";

    private SQLiteDatabase db;

    public DataStorage(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE);
        sqLiteDatabase.execSQL(CREATE_TABLE_2);
        sqLiteDatabase.execSQL(CREATE_TABLE_3);
        this.db = sqLiteDatabase;
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        String query = "DROP TABLE IF EXISTS" + TABLE_NAME;
        String query2 = "DROP TABLE IF EXISTS" + TABLE_NAME_2;
        String query3 = "DROP TABLE IF EXISTS" + TABLE_NAME_3;
        sqLiteDatabase.execSQL(query);
        sqLiteDatabase.execSQL(query2);
        sqLiteDatabase.execSQL(query3);
        this.onCreate(sqLiteDatabase);

    }

    void insertData(String value){
        db = getWritableDatabase();
        ContentValues values = new ContentValues();


        String query = "select * from StateCodePrefix";
        Cursor cursor = db.rawQuery(query, null);
        int count = cursor.getCount();

        values.put(COLUMN_ID, count);
        values.put(COLUMN_PREFIX, value);

        db.insert(TABLE_NAME_3,null,values);
        //db.insertWithOnConflict(TABLE_NAME_3, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        cursor.close();
    }

    void insertRegDetails(String email,String Org){
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
    public List<String> getAllStateCodes(){
        List<String> labels = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT prefix FROM " + TABLE_NAME_3;

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
        String query = "select prefix from StateCodePrefix";
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
}
