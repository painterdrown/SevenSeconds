package com.goldfish.sevenseconds.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by lenovo on 2017/2/23.
 */

public class ChattingDatabaseHelper extends SQLiteOpenHelper {

    public static final String CREATE_MSG = "create table Message("
            + "id integer primary key autoincrement, "
            + "account text, "
            + "message text, "
            + "time text, "
            + "sendOrReceive integer, "
            + "readOrNot integer)";

    private Context mContext;

    public ChattingDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_MSG);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists Message");
        onCreate(db);
    }
}
