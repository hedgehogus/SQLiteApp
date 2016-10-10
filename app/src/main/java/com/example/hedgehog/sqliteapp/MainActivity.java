package com.example.hedgehog.sqliteapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {
    final String LOG_TAG = "myLogs";

    int[] person_id = { 1, 2, 3, 4};
    String[] person_name = { "Иван", "Петр", "Антон", "Борис"};
    String[] person_surname = { "Иванов",  "Петров", "Антонов", "Борисов"};

    int[] settings_user_id = { 1, 3, 3, 2, 4, 3, 2, 1 };
    int[] settings_key = { 101, 102, 103, 104, 101, 102, 103, 104 };
    int[] settings_value = { 2, 3, 2, 2, 3, 1, 2, 4 };

    int chosenUserId = 3;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DBHelper dbh = new DBHelper(this);
        SQLiteDatabase db = dbh.getWritableDatabase();

        Cursor c;
        String sqlSelect = "SELECT PERSON.id AS Id, PERSON.name AS Name, PERSON.surname AS Surname, ST.key AS SettingsKey, ST.value AS SettingsValue "
                + "FROM person AS PERSON "
                + "INNER JOIN settings AS ST "
                + "ON PERSON.id = ST.userId "
                + "WHERE PERSON.id = ?";
        c = db.rawQuery(sqlSelect, new String[] {"" + chosenUserId});
        logCursor(c);

        c.close();
        dbh.close();
    }

    void logCursor(Cursor c) {
        if (c != null) {
            if (c.moveToFirst()) {
                String str;
                do {
                    str = "";
                    for (String cn : c.getColumnNames()) {
                        str = str.concat(cn + " = " + c.getString(c.getColumnIndex(cn)) + "; ");
                    }
                    Log.d(LOG_TAG, str);
                } while (c.moveToNext());
            }
        } else
            Log.d(LOG_TAG, "Cursor is null");
    }


    class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context context) {
            super(context, "myDB", null, 1);
        }

        public void onCreate(SQLiteDatabase db) {

            ContentValues cv = new ContentValues();

            db.execSQL("CREATE TABLE IF NOT EXISTS person ("
                    + "id integer primary key,"
                    + "name text,"
                    + "surname text"
                    + ");");

            for (int i = 0; i < person_id.length; i++) {
                cv.clear();
                cv.put("id", person_id[i]);
                cv.put("name", person_name[i]);
                cv.put("surname", person_surname[i]);
                db.insert("person", null, cv);
            }

            db.execSQL("CREATE TABLE IF NOT EXISTS settings ("
                    + "id integer primary key autoincrement,"
                    + "userId integer,"
                    + "key integer,"
                    + "value integer"
                    + ");");

            for (int i = 0; i < settings_user_id.length; i++) {
                cv.clear();
                cv.put("userId", settings_user_id[i]);
                cv.put("key", settings_key[i]);
                cv.put("value", settings_value[i]);
                db.insert("settings", null, cv);
            }
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }
    }
}
