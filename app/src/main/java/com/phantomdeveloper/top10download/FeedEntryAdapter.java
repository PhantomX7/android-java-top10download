package com.phantomdeveloper.top10download;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FeedEntryAdapter extends RecyclerView.Adapter<FeedEntryAdapter.FeedEntryViewHolder> {

    private List<FeedEntry> applications;
    private Context context;

    public FeedEntryAdapter(Context context, List<FeedEntry> applications) {
        this.applications = applications;
        this.context = context;
    }

    @NonNull
    @Override
    public FeedEntryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(parent == null){

        }
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        FeedEntryViewHolder viewHolder = new FeedEntryViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull FeedEntryViewHolder holder, int position) {
        holder.bindFeedEntry(applications.get(position));
    }

    @Override
    public int getItemCount() {
        return applications.size();
    }

    public class FeedEntryViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tvName)
        TextView tvName;
        @BindView(R.id.tvArtist)
        TextView tvArtist;
        @BindView(R.id.tvSummary)
        TextView tvSummary;
        @BindView(R.id.imgPicture)
        ImageView imgPicture;

        private Context context;

        public FeedEntryViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            context = itemView.getContext();
        }

        public void bindFeedEntry(FeedEntry feedEntry) {
            Picasso.get().load(feedEntry.getImageURL()).into(imgPicture);
            tvName.setText(feedEntry.getName());
            tvArtist.setText(feedEntry.getArtist());
            tvSummary.setText(feedEntry.getSummary());
        }
    }
}
