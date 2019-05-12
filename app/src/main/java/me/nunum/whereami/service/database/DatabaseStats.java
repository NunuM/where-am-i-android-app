package me.nunum.whereami.service.database;


import android.content.Context;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;


public class DatabaseStats {

    private static final String TAG = DatabaseStats.class.getSimpleName();

    private final DatabaseService service;

    public DatabaseStats(final Context context) {
        this.service = DatabaseService.getInstance(context);
    }

    /**
     * @return number of samples
     */
    public Long totalRecords() {
        Long total = 0l;

        SQLiteDatabase database = null;


        try {
            database = this.service.getReadableDatabase();
            total = DatabaseUtils.queryNumEntries(database, DatabaseService.FINGERPRINT, null);
        } catch (SQLException e) {
            Log.e(TAG, "totalRecords: Error getting database reads", e);
        } finally {

            if (database != null) {
                database.close();
            }
        }

        return total;
    }

    public Long totalOfSamplesForLocalization(int lozalization) {
        Long total = 0l;

        SQLiteDatabase database = null;

        try {
            database = this.service.getReadableDatabase();
            total = DatabaseUtils.queryNumEntries(database, DatabaseService.FINGERPRINT, "build_id=" + lozalization);
        } catch (SQLException e) {
            Log.e(TAG, "totalOfSamplesForLocalization: Error getting database reads", e);
        } finally {

            if (database != null) {
                database.close();
            }
        }

        return total;
    }

    public Long totalOfSamplesForPosition(int lozalization, int position) {

        Long total = 0l;

        SQLiteDatabase database = null;

        try {
            database = this.service.getReadableDatabase();
            total = DatabaseUtils.queryNumEntries(database, DatabaseService.FINGERPRINT, "build_id=" + lozalization + " and position=" + position);
        } catch (SQLException e) {
            Log.e(TAG, "totalOfSamplesForPosition: Error getting database reads", e);
        } finally {

            if (database != null) {
                database.close();
            }
        }

        return total;

    }
}
