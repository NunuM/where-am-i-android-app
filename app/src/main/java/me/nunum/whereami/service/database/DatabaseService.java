package me.nunum.whereami.service.database;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import me.nunum.whereami.utils.AppConfig;

public class DatabaseService extends SQLiteOpenHelper {

    private static DatabaseService sInstance;

    private static final String TAG = DatabaseService.class.getSimpleName();

    public static final String FINGERPRINT = "FINGERPRINT";
    private static final String FINGERPRINT_IDX = "fingerprint_position_idx";

    public static final String NOTIFICATIONS = "NOTIFICATIONS";
    private static final String NOTIFICATIONS_IDX = "notification_uid_idx";


    private DatabaseService(Context context, SQLiteDatabase.CursorFactory factory, DatabaseErrorHandler errorHandler) {
        super(context, AppConfig.DATABASE_NAME, factory, AppConfig.DATABASE_VERSION, errorHandler);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        Log.d(TAG, "onCreate: Create database ");

        final String createFingerprintTable = "CREATE TABLE IF NOT EXISTS %s ( bssid TEXT, timestamp TEXT,position INTEGER,centerfreq0 INTEGER,centerfreq1 INTEGER,channel_width INTEGER,frequency INTEGER,leveldbm INTEGER,localization INTEGER,ssid TEXT);";
        final String createFingerprintTablePositionIdx = "CREATE INDEX IF NOT EXISTS %s ON %s (position);";

        final String createNotificationTable = "CREATE TABLE IF NOT EXISTS %s (message TEXT, seen INTEGER, prettyDate TEXT, created INTEGER, uid TEXT);";
        final String createNotificationTableUidIdx = "CREATE INDEX IF NOT EXISTS %s ON %s (uid);";

        db.execSQL(String.format(createFingerprintTable, FINGERPRINT));
        db.execSQL(String.format(createFingerprintTablePositionIdx, FINGERPRINT_IDX, FINGERPRINT));

        db.execSQL(String.format(createNotificationTable, NOTIFICATIONS));
        db.execSQL(String.format(createNotificationTableUidIdx, NOTIFICATIONS_IDX, NOTIFICATIONS));
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            // Simplest implementation is to drop all old tables and recreate them
            db.execSQL("DROP INDEX IF EXISTS " + FINGERPRINT_IDX);
            db.execSQL("DROP INDEX IF EXISTS " + NOTIFICATIONS_IDX);
            db.execSQL("DROP TABLE IF EXISTS " + FINGERPRINT);
            db.execSQL("DROP TABLE IF EXISTS " + NOTIFICATIONS);
            onCreate(db);
        }
    }

    public static synchronized DatabaseService getInstance(Context context) {

        if (sInstance == null) {
            sInstance = new DatabaseService(context, new SqlCursorFactory(), new DatabaseServiceErrorHandler());
        }

        return sInstance;
    }
}
