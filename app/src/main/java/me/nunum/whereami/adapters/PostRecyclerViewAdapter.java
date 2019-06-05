package me.nunum.whereami.adapters;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.net.URL;
import java.util.List;
import java.util.logging.Logger;

import me.nunum.whereami.R;
import me.nunum.whereami.fragments.HomeFragment;
import me.nunum.whereami.framework.OnResponse;
import me.nunum.whereami.framework.SortedListCallbackImpl;
import me.nunum.whereami.model.Post;
import me.nunum.whereami.service.HttpService;
import me.nunum.whereami.service.Services;

/**
 * Created by nuno on 04/06/2019.
 */

public class PostRecyclerViewAdapter extends RecyclerView.Adapter<PostRecyclerViewAdapter.ViewHolder> {

    private final String TAG = PostRecyclerViewAdapter.class.getCanonicalName();

    private final SortedList<Post> mList;
    private final HomeFragment.OnFragmentInteractionListener mListener;

    public PostRecyclerViewAdapter(HomeFragment.OnFragmentInteractionListener mListener) {
        this.mListener = mListener;
        this.mList = new SortedList<Post>(Post.class, new SortedListCallbackImpl<Post>(this));
    }


    public void addAll(List<Post> postList) {
        this.mList.beginBatchedUpdates();
        for (int i = 0; i < postList.size(); i++) {
            this.mList.add(postList.get(i));
        }
        this.mList.endBatchedUpdates();
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_home_post_item, parent, false);

        view.setClickable(true);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final Post post = this.mList.get(position);

        holder.mPostTitle.setText(post.getTitle());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mListener.launchBrowserIntent(new Intent(Intent.ACTION_VIEW, Uri.parse(post.getSourceURL())));
                } catch (NullPointerException e) {
                    Log.e(TAG, "onClick: Not possible to launch intent.", e);
                }
            }
        });

        final HttpService service = (HttpService) this.mListener.getService(Services.HTTP);

        service.image(post.getImageURL(), new OnResponse<Bitmap>() {
            @Override
            public void onSuccess(Bitmap o) {
                holder.mPostImageView.setImageBitmap(o);
            }

            @Override
            public void onFailure(Throwable throwable) {
                Log.i(TAG, "onFailure: Could not request image", throwable);
            }
        });

    }

    @Override
    public int getItemCount() {
        return this.mList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final ImageView mPostImageView;
        private final TextView mPostTitle;

        public ViewHolder(View itemView) {
            super(itemView);
            this.mPostTitle = (TextView) itemView.findViewById(R.id.fhpi_post_title);
            this.mPostImageView = (ImageView) itemView.findViewById(R.id.fhpi_post_image);
        }
    }
}
