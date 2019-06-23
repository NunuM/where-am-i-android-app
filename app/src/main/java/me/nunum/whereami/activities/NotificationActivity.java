package me.nunum.whereami.activities;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import me.nunum.whereami.R;
import me.nunum.whereami.service.database.DatabaseService;

public class NotificationActivity
        extends AppCompatActivity {

    private Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        final ListView listView = findViewById(R.id.an_list_view);

        DatabaseService instance = DatabaseService.getInstance(getApplicationContext());

        cursor = instance
                .getReadableDatabase()
                .query(DatabaseService.NOTIFICATIONS,
                        new String[]{"rowid as _id", "message", "seen", "created", "prettyDate"},
                        null,
                        null,
                        null,
                        null,
                        "created desc");

        Log.i("NotificationActivity", "onCreate: " + cursor.getCount());

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                getApplicationContext(),
                R.layout.activity_notification_item,
                cursor,
                new String[]{"message", "prettyDate"},
                new int[]{R.id.aci_notification_message, R.id.aci_notification_date},
                SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

        listView.setAdapter(adapter);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.title_notifications);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (cursor != null) {
            cursor.close();
            cursor = null;
        }
    }
}
