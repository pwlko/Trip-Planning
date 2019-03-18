package com.example.pbwi.planyourtrip.activieties;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.pbwi.planyourtrip.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;


public class UserActivity extends BaseActivity{

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase database;
    private DatabaseReference mUserReference;
    private TextView userStatsSavedPlacesTextView, userStatsSavedTracksTextView, userStatsSavedTripsTextView;

    @SuppressLint({"SetTextI18n", "InflateParams"})
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = getLayoutInflater();
        LinearLayout container = findViewById(R.id.content_frame);
        inflater.inflate(R.layout.user_home_layout, container);

        firebaseAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        mUserReference = FirebaseDatabase.getInstance().getReference().child("UsersObjects");
        mUserReference.keepSynced(true);

        LinearLayout logoutButton = findViewById(R.id.logoutButton);
        TextView welcomeTextView = findViewById(R.id.titleLoginUser);
        userStatsSavedPlacesTextView = findViewById(R.id.user_stats_saved_places_value_TextView);
        userStatsSavedTracksTextView = findViewById(R.id.user_stats_saved_tracks_value_TextView);
        userStatsSavedTripsTextView = findViewById(R.id.user_stats_saved_trips_value_TextView);


        welcomeTextView.setText(Objects.requireNonNull(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getEmail()) + " !");
        Log.i("UserID", firebaseAuth.getUid());
        setUserStats();

        logoutButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view){
                Intent logoutIntent = new Intent(getApplicationContext(), MainActivity.class);
                FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                firebaseAuth.signOut();
                logoutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                logoutIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(logoutIntent);
                finish();


                startActivity(logoutIntent);

            }



        });
    }

    private void setUserStats(){
        DatabaseReference mPlacesReference = mUserReference.child("SavedPlaces");
        mPlacesReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(Objects.requireNonNull(firebaseAuth.getUid()))) {
                    DatabaseReference mPlacesUserReference = mUserReference.child("SavedPlaces").child(Objects.requireNonNull(firebaseAuth.getUid()));
                    mPlacesUserReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            long numOfChild = dataSnapshot.getChildrenCount();
                            userStatsSavedPlacesTextView.setText(String.valueOf(numOfChild));
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
                }else
                    userStatsSavedPlacesTextView.setText(String.valueOf(0));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        DatabaseReference mTracksReference = mUserReference.child("SavedTracks");
        mTracksReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(Objects.requireNonNull(firebaseAuth.getUid()))) {
                    DatabaseReference mTracksUserReference = mUserReference.child("SavedTracks").child(Objects.requireNonNull(firebaseAuth.getUid()));
                    mTracksUserReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            long numOfChild = dataSnapshot.getChildrenCount();
//                            Log.i("numOfChild", "" + numOfChild);
                            userStatsSavedTracksTextView.setText(String.valueOf(numOfChild));
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
//
                } else
                    userStatsSavedTracksTextView.setText(String.valueOf(0));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        DatabaseReference mTripsReference = mUserReference.child("SavedPlannedTrips");
        mTripsReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(Objects.requireNonNull(firebaseAuth.getUid()))){
                    DatabaseReference mTripsUserReference = mUserReference.child("SavedPlannedTrips").child(Objects.requireNonNull(firebaseAuth.getUid()));
                    mTripsUserReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            long numOfChild = dataSnapshot.getChildrenCount();
                            userStatsSavedTripsTextView.setText(String.valueOf(numOfChild));
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
                else
                    userStatsSavedTripsTextView.setText(String.valueOf(0));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        this.finish();
    }
}
