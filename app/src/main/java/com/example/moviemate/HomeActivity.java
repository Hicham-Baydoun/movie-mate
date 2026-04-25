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

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

public class HomeActivity extends AppCompatActivity {

    private TextView welcomeTextView;
    private ImageView profileIcon;
    private TextView totalWatchTimeTextView;
    private TextView totalMoviesWatchedTextView;
    private TextView rankTextView;
    private String loggedInUsername = "User";
    private String loggedInUserId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        welcomeTextView = findViewById(R.id.welcomeTextView);
        profileIcon = findViewById(R.id.profileIcon);
        totalWatchTimeTextView = findViewById(R.id.totalWatchTimeTextView);
        totalMoviesWatchedTextView = findViewById(R.id.totalMoviesWatchedTextView);
        rankTextView = findViewById(R.id.rankTextView);

        SharedPreferences prefs = getSharedPreferences("MovieMatePrefs", MODE_PRIVATE);
        loggedInUsername = prefs.getString("username", "User");
        loggedInUserId = prefs.getString("user_id", "");

        welcomeTextView.setText("Welcome back, " + loggedInUsername);
        profileIcon.setOnClickListener(this::showProfileMenu);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_home);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                return true;
            } else if (itemId == R.id.nav_movies) {
                startActivity(new Intent(HomeActivity.this, MainActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_history) {
                startActivity(new Intent(HomeActivity.this, WatchHistoryListActivity.class));
                finish();
                return true;
            }
            return false;
        });

        if (!loggedInUserId.isEmpty()) {
            fetchStats(loggedInUserId);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!loggedInUserId.isEmpty()) {
            fetchStats(loggedInUserId);
        }
    }

    private void showProfileMenu(View anchor) {
        PopupMenu popup = new PopupMenu(this, anchor);
        popup.getMenu().add("Welcome, " + loggedInUsername);
        popup.getMenu().add("Change Password").setOnMenuItemClickListener(item -> {
            Intent changeIntent = new Intent(HomeActivity.this, ChangePasswordActivity.class);
            changeIntent.putExtra("username", loggedInUsername);
            startActivity(changeIntent);
            return true;
        });
        popup.getMenu().add("Logout").setOnMenuItemClickListener(item -> {
            SharedPreferences prefs = getSharedPreferences("MovieMatePrefs", Context.MODE_PRIVATE);
            prefs.edit().clear().apply();
            Intent logoutIntent = new Intent(HomeActivity.this, LoginActivity.class);
            logoutIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(logoutIntent);
            finish();
            return true;
        });
        popup.show();
    }

    private void fetchStats(String userId) {
        String url = "http://10.0.2.2/MovieMateAPI/get_user_history.php?user_id=" + userId;
        RequestQueue queue = Volley.newRequestQueue(this);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    long totalMinutes = 0;
                    int movieCount = response.length();
                    
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject historyEntry = response.getJSONObject(i);
                            totalMinutes += historyEntry.optLong("minutes_watched", 0);
                        } catch (JSONException e) {
                            Log.e("HomeActivity", "JSON parsing error: " + e.getMessage());
                        }
                    }
                    updateUI(totalMinutes, movieCount);
                },
                error -> Log.e("HomeActivity", "Volley error: " + error.toString())
        );
        queue.add(jsonArrayRequest);
    }

    private void updateUI(long totalMinutes, int movieCount) {
        long hours = totalMinutes / 60;
        long minutes = totalMinutes % 60;
        totalWatchTimeTextView.setText(String.format(Locale.getDefault(), "%dh %dm", hours, minutes));
        totalMoviesWatchedTextView.setText(String.valueOf(movieCount));
        
        // Dynamic Rank Logic
        if (movieCount >= 20) {
            rankTextView.setText("Movie Buff");
        } else if (movieCount >= 10) {
            rankTextView.setText("Cinephile");
        } else if (movieCount >= 5) {
            rankTextView.setText("Silver");
        } else {
            rankTextView.setText("Bronze");
        }
    }
}
