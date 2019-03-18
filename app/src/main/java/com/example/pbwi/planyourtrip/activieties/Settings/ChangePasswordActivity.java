package com.example.pbwi.planyourtrip.activieties.Settings;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.pbwi.planyourtrip.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class ChangePasswordActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private EditText newPasswordEditText, newPasswordRepetitionEditText, currentPasswordEditText;
    private Button button;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.password_changing_layout);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.change_password);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        firebaseAuth = FirebaseAuth.getInstance();

        initVariables();
    }


    public void initVariables(){

        newPasswordEditText = findViewById(R.id.new_password_EditText);
        newPasswordRepetitionEditText = findViewById(R.id.new_password_repetition_EditText);
        currentPasswordEditText = findViewById(R.id.user_current_password_EditText);
        button = findViewById(R.id.submit_password_change_Button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(arePasswordsCorrectly()){
//
                    AuthCredential credential = EmailAuthProvider
                            .getCredential(Objects.requireNonNull(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getEmail()), currentPasswordEditText.getText().toString()); // Current Login Credentials \\

                    Objects.requireNonNull(user).reauthenticate(credential)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(getApplicationContext(), R.string.user_authenticated, Toast.LENGTH_SHORT).show();

                                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                    Objects.requireNonNull(user).updatePassword(newPasswordEditText.getText().toString())
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(getApplicationContext(), R.string.password_successfully_updated, Toast.LENGTH_SHORT).show();
                                                        ChangePasswordActivity.super.onBackPressed();
                                                    }
                                                }
                                            });
                                }
                            });
                }
            }
        });
    }

    public boolean arePasswordsCorrectly(){
        if(newPasswordEditText.getText() != null && newPasswordRepetitionEditText.getText() != null) {
            if (newPasswordEditText.getText().toString().equals(newPasswordRepetitionEditText.getText().toString())) {
                return true;
            }
        }

        Toast.makeText(this, R.string.not_same_passwords, Toast.LENGTH_SHORT).show();
        return false;

    }

}
