package com.example.moviemate;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class WatchHistoryAdapter extends RecyclerView.Adapter<WatchHistoryAdapter.HistoryViewHolder> {

    private final ArrayList<MovieHistoryItem> historyList;
    private final Context context;

    public WatchHistoryAdapter(Context context, ArrayList<MovieHistoryItem> historyList) {
        this.context = context;
        this.historyList = historyList;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.history_movie_item, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        MovieHistoryItem item = historyList.get(position);

        holder.movieTitleTextView.setText(item.getTitle());
        holder.movieDurationTextView.setText(item.getDuration() + "m");
        holder.ratingTextView.setText(String.valueOf(item.getRating()));
        holder.watchDateTextView.setText("Watched on: " + item.getWatchDate());

        String imageUrl = item.getImageUrl();
        
        if (imageUrl != null && imageUrl.startsWith("http")) {
            Glide.with(context)
                    .load(imageUrl)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.placeholder_image)
                    .error(getResId(item.getTitle()))
                    .into(holder.movieImageView);
        } else {
            int resId = getResId(imageUrl != null ? imageUrl : item.getTitle());
            Glide.with(context)
                    .load(resId != 0 ? resId : R.drawable.placeholder_image)
                    .placeholder(R.drawable.placeholder_image)
                    .into(holder.movieImageView);
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, RateMovieActivity.class);
            intent.putExtra("movie_id", item.getMovie_id());
            context.startActivity(intent);
        });

        holder.deleteBtn.setOnClickListener(v -> deleteHistoryItem(item, holder.getAdapterPosition()));
    }

    private int getResId(String name) {
        if (name == null || name.isEmpty()) return 0;
        
        String resName = name.toLowerCase().trim()
                .replace(" ", "_")
                .replace("-", "_")
                .replace(":", "_")
                .replace("'", "");
        
        if (resName.contains(".")) {
            resName = resName.substring(0, resName.lastIndexOf('.'));
        }
        
        resName = resName.replaceAll("_+", "_");

        return context.getResources().getIdentifier(resName, "drawable", context.getPackageName());
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    private void deleteHistoryItem(MovieHistoryItem item, int position) {
        String url = "http://10.0.2.2/MovieMateAPI/delete_history.php";
        RequestQueue queue = Volley.newRequestQueue(context);

        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    Log.d("DeleteResponse", "Server said: " + response);
                    if (response.trim().contains("success")) {
                        if (position != RecyclerView.NO_POSITION) {
                            historyList.remove(position);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, historyList.size());
                            Toast.makeText(context, "Removed from history", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(context, "Failed to remove: " + response.trim(), Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(context, "Connection error", Toast.LENGTH_SHORT).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("history_id", String.valueOf(item.getHistory_id()));
                return params;
            }
        };
        queue.add(postRequest);
    }

    public static class HistoryViewHolder extends RecyclerView.ViewHolder {
        ImageView movieImageView;
        TextView movieTitleTextView, movieDurationTextView, ratingTextView, watchDateTextView;
        ImageButton deleteBtn;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            movieImageView = itemView.findViewById(R.id.moviePoster);
            movieTitleTextView = itemView.findViewById(R.id.movieTitle);
            movieDurationTextView = itemView.findViewById(R.id.durationText);
            ratingTextView = itemView.findViewById(R.id.ratingText);
            watchDateTextView = itemView.findViewById(R.id.watchDateText);
            deleteBtn = itemView.findViewById(R.id.deleteHistoryBtn);
        }
    }
}
