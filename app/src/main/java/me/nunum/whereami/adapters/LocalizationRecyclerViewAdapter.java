package me.nunum.whereami.adapters;

import android.support.annotation.NonNull;
import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.ClientError;

import java.util.List;

import me.nunum.whereami.R;
import me.nunum.whereami.fragments.LocalizationFragment.OnListFragmentInteractionListener;
import me.nunum.whereami.framework.OnResponse;
import me.nunum.whereami.framework.SortedListCallbackImpl;
import me.nunum.whereami.model.Localization;
import me.nunum.whereami.service.HttpService;
import me.nunum.whereami.service.Services;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Localization} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 */
public class LocalizationRecyclerViewAdapter
        extends RecyclerView.Adapter<LocalizationRecyclerViewAdapter.ViewHolder> {

    private static final String TAG = LocalizationRecyclerViewAdapter.class.getSimpleName();

    private final SortedList<Localization> localizations;
    private final OnListFragmentInteractionListener listener;

    public LocalizationRecyclerViewAdapter(OnListFragmentInteractionListener listener) {
        localizations = new SortedList<>(Localization.class, new SortedListCallbackImpl<Localization>(this));
        this.listener = listener;

    }

    public void add(Localization localization) {
        this.localizations.add(localization);
    }

    public void addAll(List<Localization> localizations) {
        this.localizations.beginBatchedUpdates();
        for (int i = 0; i < localizations.size(); i++) {
            this.localizations.add(localizations.get(i));
        }
        this.localizations.endBatchedUpdates();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_localization_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.mItem = localizations.get(position);
        holder.mIdLocalizationLabel.setText(localizations.get(position).getLabel());
        holder.mIdLocalizationUsername.setText(localizations.get(position).getUser());

        holder.mIdLocalizationOptionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(listener.context(), holder.mIdLocalizationOptionsButton);

                popupMenu.inflate(R.menu.fli_localization_options_menu);

                if (!holder.mItem.isOwner()) {
                    popupMenu.getMenu().findItem(R.id.fli_localization_options_delete).setVisible(false);
                    popupMenu.getMenu().findItem(R.id.fli_localization_options_training).setVisible(false);
                }

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        final HttpService service = (HttpService) listener.getService(Services.HTTP);

                        switch (item.getItemId()) {
                            case R.id.fli_localization_options_training:

                                listener.openTrainingStatus(holder.mItem);

                                break;
                            case R.id.fli_localization_options_spam:

                                service.newSpam(holder.mItem.createSpamRequest(), new OnResponse<Void>() {
                                    @Override
                                    public void onSuccess(Void o) {
                                        Toast.makeText(listener.context(), R.string.fli_localization_spam_request_success, Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onFailure(Throwable throwable) {

                                        Log.e(TAG, "onFailure: Error submitting spam request for the localization :" + holder.mItem.getId(), throwable);

                                        if (throwable instanceof ClientError) {
                                            ClientError error = (ClientError) throwable;
                                            if (error.networkResponse.statusCode == 409) {
                                                Toast.makeText(listener.context(), R.string.fli_localization_spam_request_success, Toast.LENGTH_SHORT).show();
                                                return;
                                            }
                                        }


                                        Toast.makeText(listener.context(), R.string.fli_localization_spam_request_failure, Toast.LENGTH_SHORT).show();
                                    }
                                });

                                break;
                            case R.id.fli_localization_options_delete:

                                service.deleteLocalization(holder.mItem.id(), new OnResponse<Void>() {
                                    @Override
                                    public void onSuccess(Void o) {
                                        localizations.remove(holder.mItem);
                                    }

                                    @Override
                                    public void onFailure(Throwable throwable) {

                                        Log.e(TAG, "onFailure: Error deleting localization " + holder.mItem.getId(), throwable);

                                        Toast.makeText(listener.context(), R.string.fli_localization_delete_request_failure, Toast.LENGTH_SHORT).show();
                                    }
                                });

                                break;
                        }

                        return false;
                    }
                });

                popupMenu.show();
            }
        });

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != listener) {
                    listener.onLocalizationSelected(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return localizations.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        final View mView;
        final TextView mIdLocalizationLabel;
        final TextView mIdLocalizationUsername;
        final Button mIdLocalizationOptionsButton;
        Localization mItem;

        ViewHolder(View view) {
            super(view);
            mView = view;
            mIdLocalizationLabel = view.findViewById(R.id.fli_localization_label);
            mIdLocalizationUsername = view.findViewById(R.id.fli_localization_username);
            mIdLocalizationOptionsButton = view.findViewById(R.id.fli_localization_options);
        }

    }
}
