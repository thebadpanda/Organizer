package com.organizer;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;


class DBHandle {
    static SQLiteDatabase createDBTables(Context context){
        SQLiteDatabase sqlDB=context.openOrCreateDatabase("organizerDB",context.MODE_PRIVATE, null);

        sqlDB.execSQL("CREATE TABLE IF NOT EXISTS ToDoList(Task varchar(200) PRIMARY KEY,Status char(5),DeadlineDate varchar(10))");

        return sqlDB;
    }
}
