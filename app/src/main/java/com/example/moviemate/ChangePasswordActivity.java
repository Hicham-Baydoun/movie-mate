package com.example.moviemate;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import java.util.HashMap;
import java.util.Map;

public class ChangePasswordActivity extends AppCompatActivity {

    EditText oldPassword, newPassword;
    Button updatePasswordBtn;
    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        oldPassword = findViewById(R.id.oldPassword);
        newPassword = findViewById(R.id.newPassword);
        updatePasswordBtn = findViewById(R.id.updatePasswordBtn);

        username = getIntent().getStringExtra("username");

        updatePasswordBtn.setOnClickListener(v -> {
            String oldPwd = oldPassword.getText().toString().trim();
            String newPwd = newPassword.getText().toString().trim();

            if (oldPwd.isEmpty() || newPwd.isEmpty()) {
                Toast.makeText(this, "Please fill in both fields", Toast.LENGTH_SHORT).show();
                return;
            }

            String url = "http://10.0.2.2/MovieMateAPI/change_password.php";
            RequestQueue queue = Volley.newRequestQueue(this);

            StringRequest request = new StringRequest(Request.Method.POST, url,
                    response -> {
                        if (response.contains("success")) {
                            Toast.makeText(this, "Password updated successfully!", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(this, "Failed to update password", Toast.LENGTH_SHORT).show();
                        }
                    },
                    error -> Toast.makeText(this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show()
            ) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("username", username);
                    params.put("old_password", oldPwd);
                    params.put("new_password", newPwd);
                    return params;
                }
            };

            queue.add(request);
        });
    }
}
