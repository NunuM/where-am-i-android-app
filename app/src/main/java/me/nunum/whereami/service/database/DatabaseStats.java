package me.nunum.whereami.service.database;


import android.content.Context;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import me.nunum.whereami.model.Position;


public class DatabaseStats {

    private static final String TAG = DatabaseStats.class.getSimpleName();

    private final DatabaseService service;

    public DatabaseStats(final Context context) {
        this.service = DatabaseService.getInstance(context);
    }

    /**
     * @return number of samples
     */
    public Long totalRecords(Position position) {
        long total = 0L;

        SQLiteDatabase database = null;


        try {
            database = this.service.getReadableDatabase();
            total = DatabaseUtils.queryNumEntries(database, DatabaseService.FINGERPRINT, "position=" + position.getId());
        } catch (SQLException e) {
            Log.e(TAG, "totalRecords: Error getting database reads", e);
        } finally {

            if (database != null) {
                database.close();
            }
        }

        return total;
    }
}
