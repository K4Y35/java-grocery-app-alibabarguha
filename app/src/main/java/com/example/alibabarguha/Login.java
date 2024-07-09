package com.example.alibabarguha;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Login extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Logging in...");
        progressDialog.setCancelable(false);

        Button loginBTN = findViewById(R.id.loginBtn);
        EditText loginEmailInput = findViewById(R.id.loginEmailInput);
        EditText loginPasswordInput = findViewById(R.id.loginPasswordInput);

        TextView gotoSignupPageBtn = findViewById(R.id.gotoSignupPageBtn);

        gotoSignupPageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, Signup.class);
                startActivity(intent);
                finish();
            }
        });

        loginBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String loginEmailValue = loginEmailInput.getText().toString().trim();
                String loginPassValue = loginPasswordInput.getText().toString().trim();

                progressDialog.show();

                mDatabase.orderByChild("email").equalTo(loginEmailValue).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        progressDialog.dismiss();
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                                String passwordFromDatabase = (String) userSnapshot.child("password").getValue();
                                String nameFromDatabase = (String) userSnapshot.child("username").getValue();

                                if (passwordFromDatabase.equals(loginPassValue)) {

                                    Toast.makeText(Login.this, "Login successful!", Toast.LENGTH_SHORT).show();
                                    Integer role = userSnapshot.child("role").getValue(Integer.class);
                                    if (role != null) {
                                        if (role == 0) {

                                            Intent intent = new Intent(Login.this, Home.class);
                                            intent.putExtra("email", loginEmailValue);
                                            intent.putExtra("name", nameFromDatabase);
                                            startActivity(intent);
                                            finish();
                                        } else {

                                            Toast.makeText(Login.this, "Howdy Admin", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(Login.this, AdminHome.class);
                                            intent.putExtra("email", loginEmailValue);
                                            intent.putExtra("name", nameFromDatabase);
                                            startActivity(intent);
                                            finish();
                                        }
                                    } else {
                                        Toast.makeText(Login.this, "Role not found for user.", Toast.LENGTH_SHORT).show();
                                    }
                                    return;
                                }
                            }

                            Toast.makeText(Login.this, "Invalid credentials.", Toast.LENGTH_SHORT).show();
                        } else {

                            Toast.makeText(Login.this, "User does not exist.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        progressDialog.dismiss();
                        Toast.makeText(Login.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
