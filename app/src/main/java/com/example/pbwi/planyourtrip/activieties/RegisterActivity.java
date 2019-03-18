package com.example.pbwi.planyourtrip.activieties;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.pbwi.planyourtrip.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

/**
 * Created by komputer-prywatny on 15.01.2018.
 */

public class RegisterActivity extends AppCompatActivity{



    private EditText userPasswordConfirmationEditText, userPasswordEditText, userEmailEditText, userEmailConfirmationEditText;
    private EditText editTextConfirmPassword;

    private Button regButton;



    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration_layout);
        setupUIViews();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(getString(R.string.registration));
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        firebaseAuth = FirebaseAuth.getInstance();

        regButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validate()){
                    //Upload data to the database
                    String user_email = userEmailEditText.getText().toString().trim();
                    String user_password = userPasswordEditText.getText().toString().trim();



                    firebaseAuth.createUserWithEmailAndPassword(user_email, user_password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if(task.isSuccessful()){
                                Toast.makeText(RegisterActivity.this, getString(R.string.registration_success_message), Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                            }else{
                                Toast.makeText(RegisterActivity.this, Objects.requireNonNull(task.getException())
                                        .getMessage(), Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
                }

            }
        });

    }

    private void setupUIViews(){
        userPasswordConfirmationEditText = findViewById(R.id.user_password_repetition_EditText);
        userEmailConfirmationEditText = findViewById(R.id.user_email_repetition_EditText);
        userPasswordEditText = findViewById(R.id.etUserPassword);
        userEmailEditText = findViewById(R.id.user_email_EditText);
        regButton = findViewById(R.id.btnRegister);
    }

    private Boolean validate(){
        boolean result = false;

        String password = userPasswordEditText.getText().toString();
        String email = userEmailEditText.getText().toString();
        String passwordRepetition = userPasswordConfirmationEditText.getText().toString();
        String emailRepetition = userEmailConfirmationEditText.getText().toString();

        if(password.isEmpty() || email.isEmpty() || passwordRepetition.isEmpty() || emailRepetition.isEmpty()){
            Toast.makeText(this, getString(R.string.enter_all_details_message), Toast.LENGTH_SHORT).show();

        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            userEmailEditText.setError(getString(R.string.error_message_email));
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(emailRepetition).matches()){
            userEmailConfirmationEditText.setError(getString(R.string.error_message_email));
        }
        else if (!email.equals(emailRepetition)){
            Toast.makeText(this, R.string.error_message_emails, Toast.LENGTH_SHORT).show();
        }
        else if (password.length() < 6){
            userPasswordEditText.setError(getString(R.string.error_message_password));
        }
        else if (!password.equals(passwordRepetition)){
            Toast.makeText(this,R.string.error_message_passwords, Toast.LENGTH_SHORT).show();
        }
        else
        {result = true;}


        return result;
    }

}
