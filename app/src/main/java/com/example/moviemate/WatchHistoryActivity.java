package com.example.moviemate;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import java.util.HashMap;
import java.util.Map;

public class WatchHistoryActivity extends AppCompatActivity {

    EditText minutesInput, ratingInput;
    Button submitButton;
    int movieId;
    int userId = 1; // static for now

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch_history);

        minutesInput = findViewById(R.id.minutesInput);
        ratingInput = findViewById(R.id.ratingInput);
        submitButton = findViewById(R.id.submitWatch);

        movieId = getIntent().getIntExtra("movie_id", -1);

        submitButton.setOnClickListener(v -> {
            int minutes = Integer.parseInt(minutesInput.getText().toString());
            int rating = Integer.parseInt(ratingInput.getText().toString());
            submitWatchHistory(userId, movieId, minutes, rating);
        });
    }

    public void submitWatchHistory(int userId, int movieId, int minutes, int rating) {
        String url = "http://10.0.2.2/MovieMateAPI/add_watch_history.php";

        StringRequest request = new StringRequest(Request.Method.POST, url,
            response -> Toast.makeText(this, "Watch history added", Toast.LENGTH_SHORT).show(),
            error -> Toast.makeText(this, "Error: " + error.toString(), Toast.LENGTH_SHORT).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", String.valueOf(userId));
                params.put("movie_id", String.valueOf(movieId));
                params.put("watch_date", "2025-07-12"); // you can generate current date later
                params.put("minutes_watched", String.valueOf(minutes));
                params.put("rating", String.valueOf(rating));
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }
}