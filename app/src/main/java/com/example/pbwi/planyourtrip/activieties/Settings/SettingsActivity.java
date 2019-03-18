package com.example.pbwi.planyourtrip.activieties.Settings;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TableRow;

import com.example.pbwi.planyourtrip.R;
import com.example.pbwi.planyourtrip.activieties.BaseActivity;
import com.example.pbwi.planyourtrip.activieties.UserActivity;

public class SettingsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = getLayoutInflater();
        LinearLayout container = findViewById(R.id.content_frame);
        inflater.inflate(R.layout.user_settings_layout, container);

        initVariables();
    }


    private void initVariables(){
        TableRow mailRow = findViewById(R.id.mail_row);
        TableRow passwordRow = findViewById(R.id.password_row);

        mailRow.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), ChangeEmailActivity.class);
                startActivity(intent);
            }
        });

        passwordRow.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), ChangePasswordActivity.class);
                startActivity(intent);
            }
        });





    }



    @Override
    public void onBackPressed() {
        Intent backIntent = new Intent(getApplicationContext(), UserActivity.class);
        backIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_TASK_ON_HOME);
        startActivity(backIntent);

        this.finish();

    }

}
