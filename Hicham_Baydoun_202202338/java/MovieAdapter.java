package com.example.moviemate;

import android.content.Context;
import android.content.SharedPreferences;
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
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    private final ArrayList<MovieItem> movieList;
    private final Context context;

    public MovieAdapter(Context context, ArrayList<MovieItem> movieList) {
        this.context = context;
        this.movieList = movieList;
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.movie_item, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        MovieItem movie = movieList.get(position);

        holder.titleView.setText(movie.getTitle());
        holder.genreView.setText(movie.getGenre());
        holder.yearView.setText(movie.getReleaseYear());

        // We now only look for local images in your drawable folder
        int resId = getResId(movie.getImageUrl() != null ? movie.getImageUrl() : movie.getTitle());

        Glide.with(context)
                .load(resId != 0 ? resId : R.drawable.placeholder_image)
                .placeholder(R.drawable.placeholder_image)
                .into(holder.posterView);

        holder.markAsWatchedButton.setOnClickListener(v -> markMovieAsWatched(movie));
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

        // Fixed Schindler spelling check
        if (resName.contains("schlinder")) resName = resName.replace("schlinder", "schindler");
        if (resName.equals("schindlers_list")) resName = "schindler_s_list";

        return context.getResources().getIdentifier(resName, "drawable", context.getPackageName());
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    public static class MovieViewHolder extends RecyclerView.ViewHolder {
        TextView titleView, genreView, yearView;
        ImageView posterView;
        ImageButton markAsWatchedButton;

        public MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            titleView = itemView.findViewById(R.id.movieTitle);
            posterView = itemView.findViewById(R.id.moviePoster);
            genreView = itemView.findViewById(R.id.genreText);
            yearView = itemView.findViewById(R.id.yearText);
            markAsWatchedButton = itemView.findViewById(R.id.markAsWatchedButton);
        }
    }

    private void markMovieAsWatched(MovieItem movie) {
        String url = "http://10.0.2.2/MovieMateAPI/add_watch_history.php";

        SharedPreferences prefs = context.getSharedPreferences("MovieMatePrefs", Context.MODE_PRIVATE);
        String userId = prefs.getString("user_id", "");

        if (userId.isEmpty()) {
            Toast.makeText(context, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String watchDate = sdf.format(new Date());

        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    if (response.toLowerCase().contains("success")) {
                        Toast.makeText(context, "Added to history!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Server Error", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(context, "Connection error", Toast.LENGTH_SHORT).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", userId);
                params.put("movie_id", String.valueOf(movie.getMovie_id()));
                params.put("watch_date", watchDate);
                params.put("minutes_watched", String.valueOf(movie.getDurationMinutes()));
                return params;
            }
        };
        VolleySingleton.getInstance(context).addToRequestQueue(postRequest);
    }
}
