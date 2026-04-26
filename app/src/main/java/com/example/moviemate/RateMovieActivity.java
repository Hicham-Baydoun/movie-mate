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

public class RateMovieActivity extends AppCompatActivity {
    EditText ratingInput;
    Button submitButton;
    int movieId;
    String userId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_movie);

        ratingInput = findViewById(R.id.ratingInput);
        submitButton = findViewById(R.id.submitRating);

        movieId = getIntent().getIntExtra("movie_id", -1);
        userId = getSharedPreferences("MovieMatePrefs", MODE_PRIVATE).getString("user_id", "");

        submitButton.setOnClickListener(v -> {
            String ratingStr = ratingInput.getText().toString();
            if (ratingStr.isEmpty()) {
                Toast.makeText(this, "Please enter a rating", Toast.LENGTH_SHORT).show();
                return;
            }
            submitRating(userId, movieId, Integer.parseInt(ratingStr));
        });
    }

    public void submitRating(String userId, int movieId, int rating) {
        String url = "http://10.0.2.2/MovieMateAPI/add_rating.php";

        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    if (response.trim().contains("success")) {
                        Toast.makeText(this, "Rating submitted", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(this, "Failed to rate: " + response.trim(), Toast.LENGTH_LONG).show();
                    }
                },
                error -> Toast.makeText(this, "Rating error: " + error.toString(), Toast.LENGTH_SHORT).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", userId);
                params.put("movie_id", String.valueOf(movieId));
                params.put("rating", String.valueOf(rating));
                return params;
            }
        };

        Volley.newRequestQueue(this).add(postRequest);
    }
}
