package com.example.pbwi.planyourtrip.activieties.Settings;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.Objects;

public class ChangeEmailActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private EditText newEmailEditText, newEmailRepetitionEditText, passwordEditText;
    private Button button;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mail_changing_layout);
        firebaseAuth = FirebaseAuth.getInstance();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        Objects.requireNonNull(actionBar).setTitle(R.string.change_email_address);
        actionBar.setDisplayHomeAsUpEnabled(true);




        initVariables();

    }

    public void initVariables(){

        newEmailEditText = findViewById(R.id.new_email_EditText);
        newEmailRepetitionEditText = findViewById(R.id.new_email_repetition_EditText);
        passwordEditText = findViewById(R.id.user_current_password_EditText);
        button = findViewById(R.id.submit_email_change_Button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(isAddressCorrectly()){
                AuthCredential credential = EmailAuthProvider
                        .getCredential(Objects.requireNonNull(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getEmail()), passwordEditText.getText().toString()); // Current Login Credentials \\


                    Objects.requireNonNull(user).reauthenticate(credential)
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getApplicationContext(), R.string.authentication_failed, Toast.LENGTH_LONG).show();
                                }
                            });
                    Objects.requireNonNull(user).reauthenticate(credential)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                    Objects.requireNonNull(user).updateEmail(newEmailEditText.getText().toString())
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {

                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(getApplicationContext(), getString(R.string.user_email_update_message) + newEmailEditText.getText().toString(), Toast.LENGTH_SHORT).show();
                                                        ChangeEmailActivity.super.onBackPressed();
                                                    }
                                                    else{
                                                        Toast.makeText(getApplicationContext(), Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
                                                    }

                                                }
                                            });
                                }
                            });

            }
            }
        });
    }

    public boolean isAddressCorrectly(){
        if(newEmailEditText.getText() != null && newEmailRepetitionEditText.getText() != null) {
            if (!Patterns.EMAIL_ADDRESS.matcher(newEmailEditText.getText()).matches()) {
                newEmailEditText.setError(getString(R.string.error_message_email));
                return false;
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(newEmailRepetitionEditText.getText()).matches()) {
                newEmailRepetitionEditText.setError(getString(R.string.error_message_email));
                return false;
            }

            if (newEmailEditText.getText().toString().equals(newEmailRepetitionEditText.getText().toString())) {
                return true;
            }
        }

        Toast.makeText(this, R.string.not_same_emails_alert, Toast.LENGTH_SHORT).show();

        return false;

    }

}
