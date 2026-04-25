package com.example.moviemate;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class WatchHistoryListActivity extends AppCompatActivity {

    RecyclerView historyRecyclerView;
    WatchHistoryAdapter historyAdapter;
    ArrayList<MovieHistoryItem> historyList = new ArrayList<>();

    ImageView profileIcon;
    TextView welcomeTextView;
    String loggedInUsername = "User";
    String loggedInUserId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch_history_list);

        profileIcon = findViewById(R.id.profileIcon);
        welcomeTextView = findViewById(R.id.welcomeTextView);
        historyRecyclerView = findViewById(R.id.historyRecyclerView);

        SharedPreferences prefs = getSharedPreferences("MovieMatePrefs", Context.MODE_PRIVATE);
        loggedInUsername = prefs.getString("username", "User");
        loggedInUserId = prefs.getString("user_id", "");

        welcomeTextView.setText("Welcome, " + loggedInUsername);

        historyAdapter = new WatchHistoryAdapter(this, historyList);
        historyRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        historyRecyclerView.setAdapter(historyAdapter);

        profileIcon.setOnClickListener(this::showProfileMenu);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_history);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                startActivity(new Intent(WatchHistoryListActivity.this, HomeActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_movies) {
                startActivity(new Intent(WatchHistoryListActivity.this, MainActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_history) {
                return true;
            }
            return false;
        });

        if (!loggedInUserId.isEmpty()) {
            fetchHistory(loggedInUserId);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!loggedInUserId.isEmpty()) {
            fetchHistory(loggedInUserId);
        }
    }

    private void showProfileMenu(View anchor) {
        PopupMenu popup = new PopupMenu(this, anchor);
        popup.getMenu().add("Welcome, " + loggedInUsername);
        popup.getMenu().add("Change Password").setOnMenuItemClickListener(item -> {
            Intent changeIntent = new Intent(WatchHistoryListActivity.this, ChangePasswordActivity.class);
            changeIntent.putExtra("username", loggedInUsername);
            startActivity(changeIntent);
            return true;
        });
        popup.getMenu().add("Logout").setOnMenuItemClickListener(item -> {
            SharedPreferences prefs = getSharedPreferences("MovieMatePrefs", Context.MODE_PRIVATE);
            prefs.edit().clear().apply();
            Intent logoutIntent = new Intent(WatchHistoryListActivity.this, LoginActivity.class);
            logoutIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(logoutIntent);
            finish();
            return true;
        });
        popup.show();
    }

    private void fetchHistory(String userId) {
        String url = "http://10.0.2.2/MovieMateAPI/get_user_history.php?user_id=" + userId;
        RequestQueue queue = Volley.newRequestQueue(this);

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    historyList.clear();
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject item = response.getJSONObject(i);
                            int historyId = item.getInt("history_id"); 
                            int movieId = item.getInt("movie_id");
                            String title = item.getString("title");
                            String duration = item.getString("duration_minutes");
                            String genre = item.getString("genre");
                            String releaseYear = item.getString("release_year");
                            String imageUrl = item.getString("image_url");
                            int rating = item.getInt("rating");
                            String watchDate = item.getString("watch_date");

                            // Updated to pass imageUrl string directly
                            historyList.add(new MovieHistoryItem(historyId, movieId, title, imageUrl, duration, genre, releaseYear, rating, watchDate));
                        }
                        historyAdapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Log.e("History", error.toString())
        );
        queue.add(request);
    }
}
