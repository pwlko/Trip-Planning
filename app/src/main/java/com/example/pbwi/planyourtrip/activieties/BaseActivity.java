package com.example.pbwi.planyourtrip.activieties;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.pbwi.planyourtrip.R;
import com.example.pbwi.planyourtrip.activieties.PlannedTrip.TripPlanningActivity;
import com.example.pbwi.planyourtrip.activieties.PlannedTrip.SavedPlannedTripsActivity;
import com.example.pbwi.planyourtrip.activieties.SavedPlaces.SavedPlacesActivity;
import com.example.pbwi.planyourtrip.activieties.Tracks.TrackingActivity;
import com.example.pbwi.planyourtrip.activieties.Settings.SettingsActivity;
import com.example.pbwi.planyourtrip.activieties.Tracks.SavedTracksActivity;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;


public class BaseActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public DrawerLayout drawer;
    public FloatingActionButton fab;
    public NavigationView navigationView;
    private FirebaseAuth  firebaseAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


//        if (emailTextView != null)
//            emailTextView.setText(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail());



        setContentView(R.layout.drawer_layout);
        Toolbar toolbar = findViewById(R.id.toolbar_toolbar);
        setSupportActionBar(toolbar);

//        fab = findViewById(R.id.fab);


        drawer =  findViewById(R.id.drawer_layout_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);




        TextView emailTextView = navigationView.inflateHeaderView(R.layout.header_layout).findViewById(R.id.header_email_TextView);

        try {
            emailTextView.setText(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail());
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch(item.getItemId()) {
            case R.id.nav_home:
                startActivity(new Intent(getApplicationContext(), UserActivity.class));
                break;

            case R.id.nav_saved_planned_trips:
                startActivity(new Intent(getApplicationContext(), SavedPlannedTripsActivity.class));
                break;

            case R.id.nav_saved_places:
                startActivity(new Intent(getApplicationContext(), SavedPlacesActivity.class));
                break;

            case R.id.nav_saved_tracks:
                startActivity(new Intent(getApplicationContext(), SavedTracksActivity.class));
                break;

            case R.id.nav_tracking:
                new Thread(new Runnable() {


                    public void run() {


                        try {
                            // Sleep for 200 milliseconds.
                            // Just to display the progress slowly
                            Thread.sleep(500);

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        startActivity(new Intent(getApplicationContext(), TrackingActivity.class));
                    }

                }).start();

                break;

            case R.id.nav_trip_planning:
                new Thread(new Runnable() {


                public void run() {


                    try {
                        // Sleep for 200 milliseconds.
                        // Just to display the progress slowly
                        Thread.sleep(500);

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    startActivity(new Intent(getApplicationContext(), TripPlanningActivity.class));
                }

            }).start();

            break;

            case R.id.nav_account:
                startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                break;

            case R.id.nav_logout:
                Intent intent6 = new Intent(getApplicationContext(), MainActivity.class);
                FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                firebaseAuth.signOut();
                intent6.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent6.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent6);
                finish();

                startActivity(intent6);
                break;

        }

        drawer = findViewById(R.id.drawer_layout_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}