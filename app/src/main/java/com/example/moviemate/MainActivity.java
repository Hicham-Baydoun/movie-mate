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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    RecyclerView movieRecyclerView;
    MovieAdapter movieAdapter;
    ArrayList<MovieItem> movieList = new ArrayList<>();
    ImageView profileIcon;
    TextView welcomeTextView;
    String loggedInUsername = "User";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        movieRecyclerView = findViewById(R.id.movieRecyclerView);
        profileIcon = findViewById(R.id.profileIcon);
        welcomeTextView = findViewById(R.id.welcomeTextView);

        SharedPreferences prefs = getSharedPreferences("MovieMatePrefs", MODE_PRIVATE);
        loggedInUsername = prefs.getString("username", "User");

        welcomeTextView.setText("Welcome, " + loggedInUsername);

        movieAdapter = new MovieAdapter(this, movieList);
        movieRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        movieRecyclerView.setAdapter(movieAdapter);

        fetchMovies();

        profileIcon.setOnClickListener(this::showProfileMenu);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_movies);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                startActivity(new Intent(MainActivity.this, HomeActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_movies) {
                return true;
            } else if (itemId == R.id.nav_history) {
                startActivity(new Intent(MainActivity.this, WatchHistoryListActivity.class));
                finish();
                return true;
            }
            return false;
        });
    }

    private void showProfileMenu(View anchor) {
        PopupMenu popup = new PopupMenu(this, anchor);
        popup.getMenu().add("Welcome, " + loggedInUsername);
        popup.getMenu().add("Change Password").setOnMenuItemClickListener(item -> {
            Intent changeIntent = new Intent(MainActivity.this, ChangePasswordActivity.class);
            changeIntent.putExtra("username", loggedInUsername);
            startActivity(changeIntent);
            return true;
        });
        popup.getMenu().add("Logout").setOnMenuItemClickListener(item -> {
            SharedPreferences prefs = getSharedPreferences("MovieMatePrefs", MODE_PRIVATE);
            prefs.edit().clear().apply();
            Intent logoutIntent = new Intent(MainActivity.this, LoginActivity.class);
            logoutIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(logoutIntent);
            finish();
            return true;
        });
        popup.show();
    }

    private void fetchMovies() {
        String url = "http://10.0.2.2/MovieMateAPI/get_movies.php";
        RequestQueue queue = Volley.newRequestQueue(this);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    Log.d("FetchMovies", "Received " + response.length() + " movies from server.");
                    movieList.clear();
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject movie = response.getJSONObject(i);
                            int id = movie.getInt("movie_id");
                            String title = movie.getString("title");
                            String duration = movie.getString("duration_minutes");
                            String genre = movie.getString("genre");
                            String year = movie.getString("release_year");
                            String imageUrl = movie.getString("image_url");

                            movieList.add(new MovieItem(id, title, imageUrl, duration, genre, year));
                        } catch (JSONException e) {
                            Log.e("FetchMovies", "Error parsing movie at index " + i, e);
                        }
                    }
                    movieAdapter.notifyDataSetChanged();
                    Log.d("FetchMovies", "Adapter notified. List size: " + movieList.size());
                },
                error -> {
                    Log.e("FetchMovies", "Volley Error: " + error.toString());
                    Toast.makeText(this, "Error: " + error.toString(), Toast.LENGTH_LONG).show();
                }
        );
        queue.add(jsonArrayRequest);
    }
}
