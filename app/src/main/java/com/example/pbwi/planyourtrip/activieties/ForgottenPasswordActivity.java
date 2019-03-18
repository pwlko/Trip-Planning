package com.example.pbwi.planyourtrip.activieties;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.pbwi.planyourtrip.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgottenPasswordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgotten_password_layout);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.password_change);
        actionBar.setDisplayHomeAsUpEnabled(true);

        Button submitButton = findViewById(R.id.submit_password_change_Button);
        final EditText mailEditText = findViewById(R.id.email_EditText);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAddressCorrectly(mailEditText)) {


                    FirebaseAuth.getInstance().sendPasswordResetEmail("user@example.com")
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d("Email status", "Email sent.");
                                        Toast.makeText(ForgottenPasswordActivity.this, R.string.email_sent, Toast.LENGTH_SHORT).show();
                                        finish();
                                    } else
                                        Toast.makeText(ForgottenPasswordActivity.this, R.string.something_wrong_try_again, Toast.LENGTH_SHORT).show();
                                }
                            });
                }
                Toast.makeText(ForgottenPasswordActivity.this, R.string.error_message_email, Toast.LENGTH_SHORT).show();
            }
        });


    }

    private boolean isAddressCorrectly(EditText emailEditText) {
        if (!Patterns.EMAIL_ADDRESS.matcher(emailEditText.getText()).matches()) {
            emailEditText.setError(getString(R.string.error_message_email));
            return false;
        }
        return true;
    }
}
