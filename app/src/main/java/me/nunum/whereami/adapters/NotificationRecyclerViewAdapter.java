package me.nunum.whereami.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;

import java.util.List;

import me.nunum.whereami.R;
import me.nunum.whereami.framework.SortedListCallbackImpl;
import me.nunum.whereami.model.Notification;

/**
 * Created by nuno on 25/06/2019.
 */

public class NotificationRecyclerViewAdapter extends RecyclerView.Adapter<NotificationRecyclerViewAdapter.ViewHolder> {

    private static final String TAG = NotificationRecyclerViewAdapter.class.getCanonicalName();

    private final SortedList<Notification> mValues;
    private final OnNotificationClicked mListener;

    public NotificationRecyclerViewAdapter(OnNotificationClicked mListener) {
        this.mValues = new SortedList<Notification>(Notification.class, new SortedListCallbackImpl<Notification>(this));
        this.mListener = mListener;
    }

    public void updateToAsSeen() {
        this.mValues.beginBatchedUpdates();
        for (int i = 0; i < mValues.size(); i++) {
            Notification notification = mValues.get(i);
            if (!notification.isSeen()) {
                notification.setSeen(true);
                notifyItemChanged(i);
            }
        }
        this.mValues.endBatchedUpdates();
    }

    public void clearNotifications() {
        this.mValues.beginBatchedUpdates();
        this.mValues.clear();
        this.mValues.endBatchedUpdates();

        mListener.onListSizeChange(mValues.size());
    }

    public void addAll(List<Notification> notifications) {
        this.mValues.beginBatchedUpdates();
        for (int i = 0; i < notifications.size(); i++) {
            this.mValues.add(notifications.get(i));
        }
        this.mValues.endBatchedUpdates();

        mListener.onListSizeChange(mValues.size());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_notification_item, parent, false);

        return new NotificationRecyclerViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        final Notification n = mValues.get(position);

        holder.mMessage.setText(n.getMessage());

        if (!n.isSeen()) {
            Typeface boldTypeface = Typeface.defaultFromStyle(Typeface.BOLD);
            holder.mMessage.setTypeface(boldTypeface);

            holder.mMessage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.mMessage.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
                    mListener.onSeenClick(n);
                }
            });
        }

        holder.mDate.setText(n.getPrettyDate());
        holder.mOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(mListener.context(), holder.mOptions);

                popupMenu.inflate(R.menu.an_notification_item_options_menu);

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.an_notification_options_delete:
                                if (mListener.deleteNotification(n)) {
                                    mValues.remove(n);

                                    if (mValues.size() == 0) {
                                        mListener.listIsEmpty();
                                    }
                                }
                                break;
                        }
                        return false;
                    }
                });

                popupMenu.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public interface OnNotificationClicked {
        void onSeenClick(Notification notification);

        boolean deleteNotification(Notification notification);

        void listIsEmpty();

        void onListSizeChange(int size);

        Context context();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView mMessage;
        private final TextView mDate;
        private final Button mOptions;

        public ViewHolder(View itemView) {
            super(itemView);
            this.mMessage = itemView.findViewById(R.id.aci_notification_message);
            this.mDate = itemView.findViewById(R.id.aci_notification_date);
            this.mOptions = itemView.findViewById(R.id.aci_notification_options);
        }
    }
}
