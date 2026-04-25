package com.example.moviemate;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    EditText usernameInput, passwordInput;
    Button loginButton;
    TextView signupButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameInput = findViewById(R.id.username);
        passwordInput = findViewById(R.id.password);
        loginButton = findViewById(R.id.loginButton);
        signupButton = findViewById(R.id.signupButton);

        signupButton.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
            startActivity(intent);
        });

        loginButton.setOnClickListener(v -> {
            String username = usernameInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            String url = "http://10.0.2.2/MovieMateAPI/login_user.php";

            StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                    response -> {
                        Log.d("LOGIN_RESPONSE", response);
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            String status = jsonResponse.getString("status");

                            if (status.equals("success")) {
                                String userId = jsonResponse.getString("user_id");
                                SharedPreferences prefs = getSharedPreferences("MovieMatePrefs", MODE_PRIVATE);
                                prefs.edit().putString("username", username).apply();
                                prefs.edit().putString("user_id", userId).apply();

                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                String message = jsonResponse.getString("message");
                                Toast.makeText(this, "Login failed: " + message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Log.e("LoginActivity", "JSON Error: " + response);
                            Toast.makeText(this, "Login failed: Invalid server response", Toast.LENGTH_SHORT).show();
                        }
                    },
                    error -> {
                        Log.e("VolleyError", error.toString());
                        Toast.makeText(this, "Login failed. Check your connection.", Toast.LENGTH_SHORT).show();
                    }
            ) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("username", username);
                    params.put("password", password);
                    return params;
                }
            };

            // Use VolleySingleton to manage the session cookie
            VolleySingleton.getInstance(this).addToRequestQueue(postRequest);
        });
    }
}
