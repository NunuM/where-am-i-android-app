package me.nunum.whereami.service.database;

import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;

public class DatabaseServiceErrorHandler implements DatabaseErrorHandler {

    /**
     * The method invoked when database corruption is detected.
     *
     * @param dbObj the {@link SQLiteDatabase} object representing the database on which corruption
     *              is detected.
     */
    @Override
    public void onCorruption(SQLiteDatabase dbObj) {
        dbObj.needUpgrade(dbObj.getVersion() + 1);
    }
}
