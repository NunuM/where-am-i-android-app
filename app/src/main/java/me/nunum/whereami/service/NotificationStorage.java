package me.nunum.whereami.service;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import me.nunum.whereami.R;
import me.nunum.whereami.service.database.DatabaseService;


public class NotificationStorage {

    public static void persistNotification(final Context context,
                                           final String message,
                                           final long sentTime) {
        persistNotification(context, message, false, sentTime);
    }

    private static void persistNotification(final Context context,
                                            final String message,
                                            final boolean seen,
                                            final long sentTime) {
        DatabaseService service = DatabaseService.getInstance(context);
        SQLiteDatabase database = null;
        try {
            database = service.getWritableDatabase();

            ContentValues contentValues = new ContentValues(5);
            contentValues.put("message", message);
            contentValues.put("seen", seen ? 1 : 0);
            contentValues.put("created", sentTime);
            contentValues.put("uid", UUID.randomUUID().toString());

            SimpleDateFormat format = new SimpleDateFormat("HH:mm MM/d/Y", Locale.getDefault());
            contentValues.put("prettyDate", context.getString(R.string.notification_item_sub_header, format.format(new Date(sentTime))));

            database.insertOrThrow(DatabaseService.NOTIFICATIONS, null, contentValues);

        } catch (Throwable e) {
            if (database != null) {
                database.close();
            }
        }
    }
}