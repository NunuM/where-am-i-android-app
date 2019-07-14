package me.nunum.whereami.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import java.util.ArrayList;
import java.util.List;

import me.nunum.whereami.R;
import me.nunum.whereami.adapters.NotificationRecyclerViewAdapter;
import me.nunum.whereami.model.Notification;
import me.nunum.whereami.service.database.DatabaseService;
import me.nunum.whereami.utils.AppConfig;

public class NotificationActivity
        extends AppCompatActivity {

    private static final String TAG = NotificationActivity.class.getSimpleName();

    private BroadcastReceiver mObserver;

    private SwipeRefreshLayout refreshLayout;
    private NotificationRecyclerViewAdapter adapter;

    private LocalBroadcastManager broadcastManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        broadcastManager = LocalBroadcastManager.getInstance(getApplicationContext());

        refreshLayout = findViewById(R.id.an_notifications_swipe);
        refreshLayout.setRefreshing(true);

        List<Notification> notifications = notificationList();

        final RecyclerView recyclerView = findViewById(R.id.an_list_view);
        final TextView emptyLabelView = findViewById(R.id.an_empty_view);
        final ViewSwitcher viewSwitcher = findViewById(R.id.an_switcher);
        final Context context = recyclerView.getContext();


        final LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);

        final DividerItemDecoration dividerItemDecorator = new DividerItemDecoration(
                recyclerView.getContext(),
                DividerItemDecoration.VERTICAL
        );
        recyclerView.addItemDecoration(dividerItemDecorator);

        adapter = new NotificationRecyclerViewAdapter(new NotificationRecyclerViewAdapter.OnNotificationClicked() {
            @Override
            public void onSeenClick(Notification notification) {
                final DatabaseService service = DatabaseService.getInstance(getApplicationContext());

                final SQLiteDatabase database = service.getWritableDatabase();

                try {
                    database.execSQL("UPDATE " + DatabaseService.NOTIFICATIONS + " SET seen=1 WHERE uid='" + notification.getUid() + "'");
                } catch (Throwable e) {
                    Log.e(TAG, "notification: Could not update seen flag", e);
                } finally {
                    database.close();
                }
            }

            @Override
            public boolean deleteNotification(Notification notification) {
                boolean result = false;
                final DatabaseService service = DatabaseService.getInstance(getApplicationContext());
                final SQLiteDatabase database = service.getWritableDatabase();

                try {
                    final int delete = database.delete(DatabaseService.NOTIFICATIONS, "uid=?", new String[]{notification.getUid()});
                    result = delete == 1;
                } catch (Throwable e) {
                    Log.e(TAG, "deleteNotification: Error removing notification", e);
                } finally {
                    database.close();
                }
                return result;
            }

            @Override
            public void listIsEmpty() {
                if (R.id.an_empty_view == viewSwitcher.getNextView().getId()) {
                    viewSwitcher.showNext();
                }
            }

            @Override
            public void onListSizeChange(int size) {
                if (size > 0) {

                    if (R.id.an_list_view == viewSwitcher.getNextView().getId()) {
                        viewSwitcher.showNext();
                    }
                } else if (R.id.an_empty_view == viewSwitcher.getNextView().getId()) {
                    viewSwitcher.showNext();
                }
            }

            @Override
            public Context context() {
                return getApplicationContext();
            }
        });
        recyclerView.setAdapter(adapter);
        adapter.addAll(notifications);
        refreshLayout.setRefreshing(false);

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateData(refreshLayout, adapter);
            }
        });

        this.mObserver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.i(TAG, "onReceive: Received Broadcast");
                String action = intent.getAction();
                if (action != null
                        && AppConfig.BROADCAST_NEW_NOTIFICATION_ACTION.equals(action)) {
                    updateData(refreshLayout, adapter);
                }
            }
        };

        if (broadcastManager != null) {
            broadcastManager.registerReceiver(mObserver, new IntentFilter(AppConfig.BROADCAST_NEW_NOTIFICATION_ACTION));
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.title_notifications);
        }
    }

    private synchronized void updateData(final SwipeRefreshLayout refreshLayout, final NotificationRecyclerViewAdapter adapter) {
        refreshLayout.setRefreshing(true);
        adapter.addAll(notificationList());
        refreshLayout.setRefreshing(false);
    }


    private List<Notification> notificationList() {

        final List<Notification> notifications = new ArrayList<>();
        final DatabaseService instance = DatabaseService.getInstance(getApplicationContext());
        Cursor cursor = null;

        try {
            cursor = instance
                    .getReadableDatabase()
                    .query(DatabaseService.NOTIFICATIONS,
                            new String[]{"rowid as _id", "message", "seen", "created", "prettyDate", "uid"},
                            null,
                            null,
                            null,
                            null,
                            "created desc");

            while (cursor.moveToNext()) {

                final int rowId = cursor.getInt(0);
                final String message = cursor.getString(1);
                final int seen = cursor.getInt(2);
                final long created = cursor.getLong(3);
                final String prettyDate = cursor.getString(4);
                final String uid = cursor.getString(5);

                notifications.add(new Notification(rowId, message, seen == 1, prettyDate, created, uid));
            }

        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return notifications;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.an_notification_options_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.an_notification_options_delete_all:
                if (clearAllNotifications()) {
                    adapter.clearNotifications();
                }
                return true;
            case R.id.an_notification_options_mark_as_seen:
                if (markAsSeen()) {
                    adapter.updateToAsSeen();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private boolean clearAllNotifications() {
        boolean result = true;
        final DatabaseService service = DatabaseService.getInstance(getApplicationContext());
        final SQLiteDatabase database = service.getWritableDatabase();

        try {
            database.execSQL("DELETE FROM " + DatabaseService.NOTIFICATIONS);
        } catch (Throwable e) {
            result = false;
            Log.e(TAG, "markAsSeen: Cannot mark all notifications as seen", e);
        } finally {
            database.close();
        }

        return result;
    }

    private boolean markAsSeen() {
        boolean result = true;
        final DatabaseService service = DatabaseService.getInstance(getApplicationContext());
        final SQLiteDatabase database = service.getWritableDatabase();

        try {
            database.execSQL("UPDATE " + DatabaseService.NOTIFICATIONS + " SET seen=1");
        } catch (Throwable e) {
            result = false;
            Log.e(TAG, "markAsSeen: Cannot mark all notifications as seen", e);
        } finally {
            database.close();
        }

        return result;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (this.mObserver != null) {
            broadcastManager.unregisterReceiver(mObserver);
        }

        mObserver = null;
        broadcastManager = null;
        refreshLayout = null;
        adapter = null;
    }
}
