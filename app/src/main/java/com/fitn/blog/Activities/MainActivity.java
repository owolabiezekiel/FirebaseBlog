package com.fitn.blog.Activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.fitn.blog.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser mUser;
    private Button loginButton;
    private Button createAccountButton;
    private EditText loginEmail;
    private EditText loginPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        loginButton = (Button) findViewById(R.id.loginButton);
        createAccountButton = (Button) findViewById(R.id.createAccountButton);
        loginEmail = (EditText) findViewById(R.id.emailEdt);
        loginPassword = (EditText) findViewById(R.id.passwordEdt); 

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                mUser = firebaseAuth.getCurrentUser();

                if(mUser != null){
                    Toast.makeText(MainActivity.this, "A user is signed in", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(MainActivity.this, PostListActivity.class));
                    finish();
                } else {
                    Toast.makeText(MainActivity.this, "No user is signed in", Toast.LENGTH_LONG).show();
                }
            }
        };

        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, CreateAccountActivity.class));
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(loginEmail.getText().toString()) &&
                        !TextUtils.isEmpty(loginPassword.getText().toString())){
                    String emailString = loginEmail.getText().toString();
                    String passwordString = loginPassword.getText().toString();
                    
                    login(emailString, passwordString);
                } else {
                    Toast.makeText(MainActivity.this, "email and password is required", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void login(String emailString, String passwordString) {
        mAuth.signInWithEmailAndPassword(emailString, passwordString)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(MainActivity.this, "Login is Successful", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(MainActivity.this, PostListActivity.class));
                            finish();
                        } else {
                            Toast.makeText(MainActivity.this, "Sign in Failed", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_signout){
            mAuth.signOut();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null){
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}
