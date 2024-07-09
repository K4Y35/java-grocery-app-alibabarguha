package com.example.alibabarguha;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Signup extends AppCompatActivity {

    private EditText signupUsernameInput, signupEmailInput, signupPasswordInput;
    private Button signUPBTN;
    private DatabaseReference mDatabase;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");


        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Signing up...");
        progressDialog.setCancelable(false);

        TextView gotoLoginPageBtn = findViewById(R.id.gotoLoginPageBtn);
        signupUsernameInput = findViewById(R.id.signupUsernameInput);
        signupEmailInput = findViewById(R.id.signupEmailInput);
        signupPasswordInput = findViewById(R.id.signupPasswordInput);
        signUPBTN = findViewById(R.id.signUPBTN);

        gotoLoginPageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Signup.this, Login.class);
                startActivity(intent);
                finish();
            }
        });

        signUPBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String signupEmailValue = signupEmailInput.getText().toString().trim();
                String signupPasswordValue = signupPasswordInput.getText().toString().trim();
                String signupUsernameValue = signupUsernameInput.getText().toString().trim();

                if (!signupEmailValue.isEmpty() && !signupPasswordValue.isEmpty() && !signupUsernameValue.isEmpty()) {
                    progressDialog.show();

                    User user = new User(signupUsernameValue, signupEmailValue, signupPasswordValue);
                    mDatabase.push().setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            progressDialog.dismiss();
                            if (task.isSuccessful()) {
                                Toast.makeText(Signup.this, "Sign Up Successful", Toast.LENGTH_SHORT).show();
                                Toast.makeText(Signup.this, "Login to continue", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(Signup.this, Login.class));
                                finish();
                            } else {
                                Toast.makeText(Signup.this, "Sign Up Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                } else {
                    Toast.makeText(Signup.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public static class User {
        public String username;
        public String email;
        public String password;
        public int role = 0;
        public User(String username, String email, String password) {
            this.username = username;
            this.email = email;
            this.password = password;
        }
    }
}
