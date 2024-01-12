package com.example.qrscan;

import static androidx.constraintlayout.widget.ConstraintLayoutStates.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class login extends AppCompatActivity {
    EditText email,password;
    Button click;
    private FirebaseAuth mAuth;
    public String inputEmail,inputPassword;
// ...
// Initialize Firebase Auth

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        email = (EditText) findViewById(R.id.email);
        password = (EditText)findViewById(R.id.password);
        click = findViewById(R.id.btnlogin);
        click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               funLogin();
            }
        });


    }
public void funLogin(){
        inputEmail = String.valueOf(email.getText());
        inputPassword = String.valueOf(password.getText());
    mAuth.signInWithEmailAndPassword(inputEmail, inputPassword)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithEmail:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        Intent send = new Intent(login.this, MainActivity2.class);
                        send.putExtra("KEY_SENDER",inputEmail);
                        startActivity(send);
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                        Toast.makeText(login.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
}
}