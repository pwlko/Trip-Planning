package com.example.pbwi.planyourtrip.activieties.Tracks;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pbwi.planyourtrip.R;
import com.example.pbwi.planyourtrip.activieties.BaseActivity;
import com.example.pbwi.planyourtrip.activieties.UserActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * Created by komputer-prywatny on 17.01.2018.
 */

public class TrackingActivity extends BaseActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private static final int MY_PERMISSION_REQUEST_CODE = 7171;

    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback mLocationCallback;
    private LocationRequest mLocationRequest;
    private List<LatLng> locationList;
    private HashMap<String, Integer> savedPlacesMapIndexes;
    private HashMap<Integer, String> savedPlacesMapIdies;
    private HashMap<String, Long> currentLatLngTimeMap;
    private HashMap<Integer, Boolean> pausesAtPlacesMap;


    private TextView hideTextView;
    private View viewDivider;
    private RelativeLayout mapFragmentRelativeLayout;
    private boolean optionsShowed = true;
    private LinearLayout optionsLinearLayout;

    private Chronometer mChronometer;
    private long timeWhenStopped = 0;
    private boolean isRunning = false;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private Button startButton, stopButton;
    private EditText nameET;
    private EditText descriptionET;
    private Bundle savedInstanceState;
    private int positionCounter = 0;
    private LatLng latLng1, latLng2;
    private Long timeStart, timeEnd;
    private int savedPlaceCounter = 0;

    GoogleMap mGoogleMap;
    @SuppressLint("UseSparseArrays")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = getLayoutInflater();
        LinearLayout container = findViewById(R.id.content_frame);
        inflater.inflate(R.layout.navigation_layout, container);

        ActionBar actionBar = getSupportActionBar();
        Objects.requireNonNull(actionBar).setTitle(R.string.tracking);

        this.savedInstanceState = savedInstanceState;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            }, MY_PERMISSION_REQUEST_CODE);
            return;
        }
        if(googleServicesAvailable()){
            Toast.makeText(this, "Connect to play services", Toast.LENGTH_LONG).show();

        }


        firebaseAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        mChronometer = findViewById(R.id.timer1);
        startButton = findViewById(R.id.startButton1);
        stopButton = findViewById(R.id.stop_Button);
        stopButton.setEnabled(false);
        Button savePlaceButton = findViewById(R.id.save_place_Button);

        nameET = findViewById(R.id.editTextPlaceName1);
        descriptionET = findViewById(R.id.descriptionET1);
        locationList = new ArrayList<>();
        savedPlacesMapIndexes = new HashMap<>();
        savedPlacesMapIdies = new HashMap<>();
        pausesAtPlacesMap = new HashMap<>();
        currentLatLngTimeMap = new HashMap<>();
        hideTextView = findViewById(R.id.textViewHideShow);
        viewDivider = findViewById(R.id.viewDivider);
        optionsLinearLayout = findViewById(R.id.linearLayoutOptions1);
        mapFragmentRelativeLayout = findViewById(R.id.mapFragmentRelativeLayout1);
        myRef = database.getReference("UsersObjects/SavedPlaces/" + Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid());



        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.fragment1);
        mapFragment.getMapAsync(this);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                LatLng result = new LatLng(locationResult.getLastLocation().getLatitude(),
                        locationResult.getLastLocation().getLongitude());
                result.toString();
                currentLatLngTimeMap.put(result.toString(), System.currentTimeMillis());
                locationList.add(result);
                Log.d("onLocationResult",
                        new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
                                .format(System.currentTimeMillis())
                        + " : " + locationResult.getLastLocation());
                makeLocationCallback();
            }
        };



        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isRunning) {
                    timeStart = System.currentTimeMillis();
                    mChronometer.setBase(SystemClock.elapsedRealtime() - timeWhenStopped);
                    stopButton.setEnabled(true);
                    timerStart();
                    startLocationUpdates();
                } else {
                    timerStop();
                    stopLocationUpdates();

                }
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timerStop();
                stopLocationUpdates();
                makeStopTrackingAlertDialog();
                stopButton.setEnabled(false);
            }
        });

        savePlaceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savePlace();

            }
        });

        hideTextView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                hideMenu();
            }
        });


    }



    private void timerStart() {
        mChronometer.start();
        startButton.setText(R.string.pause);
        isRunning = true;

    }

    private void timerStop() {
        timeWhenStopped = SystemClock.elapsedRealtime() - mChronometer.getBase();
        mChronometer.stop();
        pausesAtPlacesMap.put(locationList.size() - 1, true);
        startButton.setText("Start");
        isRunning = false;

    }

    private void timerReset() {
        mChronometer.setBase(SystemClock.elapsedRealtime());
        timeWhenStopped = 0;
        CameraUpdate update = CameraUpdateFactory.zoomTo(2);
        mGoogleMap.moveCamera(update);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, R.string.permission_granted, Toast.LENGTH_SHORT).show();
                    finish();
                    startActivity(getIntent());
                } else {
                    Toast.makeText(this, R.string.permission_denied, Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
        }
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
//        mLocationRequest.setSmallestDisplacement(5);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void startLocationUpdates() throws SecurityException {
        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                mLocationCallback,
                Looper.myLooper());
        mGoogleMap.setMyLocationEnabled(true);
    }


    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
        if (mFusedLocationClient != null) {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        }
    }

    private void stopLocationUpdates() {
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public boolean googleServicesAvailable(){
        GoogleApiAvailability api = GoogleApiAvailability.getInstance();
        int isAvailable = api.isGooglePlayServicesAvailable(this);
        if(isAvailable == ConnectionResult.SUCCESS){
            return true;
        } else if (api.isUserResolvableError(isAvailable)){
            Dialog dialog = api.getErrorDialog(this, isAvailable, 0);
            dialog.show();
        }else{
            Toast.makeText(this, "Can't connect to play services", Toast.LENGTH_LONG).show();
        }
        return false;

    }

    ///Map
    private void initMap() {
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.fragment1);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        createLocationRequest();

    }
    private void goToLocation(double lat, double lng){
        LatLng ll = new LatLng(lat,lng);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, 12);
        mGoogleMap.moveCamera(update);

    }


    @Override
    public void onBackPressed() {

        AlertDialog.Builder alt_bld = new AlertDialog.Builder(this);

        alt_bld.setTitle(R.string.on_cancel_question);

        alt_bld.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                Intent backIntent = new Intent(getApplicationContext(), UserActivity.class);
                backIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_TASK_ON_HOME);
                startActivity(backIntent);

                TrackingActivity.this.finish();


            } });


        alt_bld.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            } });
        AlertDialog alert = alt_bld.create();
        alert.show();

    }

    public void makeStopTrackingAlertDialog(){



        AlertDialog.Builder alert = new AlertDialog.Builder(TrackingActivity.this);

        alert.setTitle(R.string.saving_tracking);
        alert.setMessage(R.string.save_tracking_question);

        alert.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                createNameSetDialog();
                dialog.dismiss();
            }
        });

        alert.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                timerReset();
                locationList.clear();
                savedPlacesMapIdies.clear();
                savedPlacesMapIndexes.clear();
                pausesAtPlacesMap.clear();
                mGoogleMap.clear();
                positionCounter = 0;
            }
        });
        alert.setNeutralButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.cancel();
            }
        });

        final AlertDialog alertDialog = alert.create();
        alertDialog.show();

    }

    public void createNameSetDialog() {

        AlertDialog.Builder alert = new AlertDialog.Builder(TrackingActivity.this);
        alert.setTitle(R.string.track_name);
        alert.setMessage(R.string.set_name);
        // Create TextView
        final EditText input = new EditText(TrackingActivity.this);
        input.setMaxLines(1);
        alert.setView(input);


        alert.setPositiveButton(R.string.save, null);

        alert.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                timerReset();
                clearVariables();
                Toast.makeText(TrackingActivity.this, R.string.track_not_saved, Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
        final AlertDialog alertDialog = alert.create();
        alertDialog.show();

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = input.getText().toString();
                if (!name.isEmpty()) {
                    timeEnd = System.currentTimeMillis();
                    saveTrack(name);
                    clearVariables();
                    Toast.makeText(TrackingActivity.this, R.string.track_saved, Toast.LENGTH_SHORT).show();
                    alertDialog.dismiss();
                } else {
                    input.setError(getString(R.string.empty_name_alert));
                }
            }
        });
    }

    public void saveTrack(final String trackName){
        String trackID = database.getReference("UsersObjects/SavedTracks/" + Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid()).push().getKey();
        String placeID;
        DatabaseReference trackRef = database.getReference("UsersObjects/SavedTracks/" + Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid() + "/" + trackID);
        double longitude, latitude;

        if (locationList.size() > 0) {

            trackRef.child("name").setValue(trackName);
            for (int i = 0; i < locationList.size(); i++) {
                longitude = locationList.get(i).longitude;
                latitude = locationList.get(i).latitude;

                if(savedPlacesMapIdies.containsKey(i)) {
                    Log.d("containsKey", "true" + i);
                    placeID = savedPlacesMapIdies.get(i);
                    trackRef.child("Locations").child("ID_" + i +"_" + "s").child("longitude").setValue(longitude);
                    trackRef.child("Locations").child("ID_" + i +"_" + "s").child("latitude").setValue(latitude);
                    trackRef.child("Locations").child("ID_" + i +"_" + "s").child("time").setValue(currentLatLngTimeMap.get(locationList.get(i).toString()));
                    trackRef.child("Locations").child("ID_" + i +"_" + "s").child("savedPlaceID").setValue(placeID);

                    if(pausesAtPlacesMap.containsKey(i))
                        trackRef.child("Locations").child(placeID).child("pause").setValue(true);

                }else{
                    trackRef.child("Locations").child("ID_" + i).child("longitude").setValue(longitude);
                    trackRef.child("Locations").child("ID_" + i).child("latitude").setValue(latitude);
                    trackRef.child("Locations").child("ID_" + i).child("time").setValue(currentLatLngTimeMap.get(locationList.get(i).toString()));
                    if(pausesAtPlacesMap.containsKey(i))
                        trackRef.child("Locations").child("ID_" + i).child("pause").setValue(true);
                }
            }

            trackRef.child("timeStart").setValue(timeStart);
            trackRef.child("timeEnd").setValue(timeEnd);
            trackRef.child("savedPointsNumber").setValue(savedPlaceCounter);



        }

    }

    public void clearVariables(){
        timerReset();
        locationList.clear();
        savedPlacesMapIdies.clear();
        savedPlacesMapIndexes.clear();
        pausesAtPlacesMap.clear();
        mGoogleMap.clear();
        positionCounter = 0;
        savedPlaceCounter = 0;
    }

    public void savePlace(){
        if (nameET.getText() == null || nameET.getText().length() == 0) {
            nameET.setError(getString(R.string.place_name_alert));
        }
        else  if (isRunning && locationList.size() > 0){

            String attractionID = myRef.push().getKey();
            Log.d("lastLocationIndex", "" + (locationList.size() - 1));
            int lastLocationIndex = (locationList.size() - 1);
            double latitude, longitude;
            latitude = locationList.get(lastLocationIndex).latitude;
            longitude = locationList.get(lastLocationIndex).longitude;
            DatabaseReference databaseReference = myRef.child(Objects.requireNonNull(attractionID));

            databaseReference.child("name").setValue(nameET.getText().toString());
            databaseReference.child("description").setValue(descriptionET.getText().toString());
            databaseReference.child("date").setValue(System.currentTimeMillis());;
            databaseReference.child("lat").setValue(locationList.get(lastLocationIndex).latitude);
            databaseReference.child("lng").setValue(locationList.get(lastLocationIndex).longitude);
            databaseReference.child("placeID").setValue(attractionID);

            savedPlacesMapIndexes.put(attractionID, lastLocationIndex);
            savedPlacesMapIdies.put(lastLocationIndex,attractionID);

            MarkerOptions options = new MarkerOptions()
                    .position(new LatLng(latitude, longitude))
                    .title(nameET.getText().toString());
            mGoogleMap.addMarker(options);

            nameET.setText("");
            descriptionET.setText("");
            savedPlaceCounter++;
            Toast.makeText(TrackingActivity.this, R.string.place_added, Toast.LENGTH_SHORT).show();


        }else{
            Toast.makeText(TrackingActivity.this, R.string.navigation_work_alert, Toast.LENGTH_SHORT).show();
        }
    }

    public void makeLocationCallback(){
        int locationsListSize = locationList.size();


        if(locationsListSize == 1) {
            goToLocation(locationList.get(locationsListSize - 1).latitude,locationList.get(locationsListSize - 1).longitude);
            MarkerOptions options = new MarkerOptions()
                    .position(new LatLng(locationList.get(0).latitude, locationList.get(0).longitude))
                    .title("START POINT");
            mGoogleMap.addMarker(options);
        }
        else if (locationsListSize > 1){ //counting from zero

            latLng1 = new LatLng(locationList.get(locationsListSize - 2).latitude,locationList.get(locationsListSize - 2).longitude);
            latLng2 = new LatLng(locationList.get(locationsListSize-1).latitude,locationList.get(locationsListSize-1).longitude);
            PolylineOptions polylineOptions = new PolylineOptions()
                    .add(latLng1, latLng2);
            mGoogleMap.addPolyline(polylineOptions);
//            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom( latLng2, 12));
        }
        positionCounter++;

    }

    public void hideMenu(){
        if (optionsShowed) {
            optionsLinearLayout.animate()
                    .setDuration(500)
                    .translationY(0)
                    .alpha(0.0f)
                    .setListener(new AnimatorListenerAdapter() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            optionsLinearLayout.setVisibility(View.GONE);
                            mapFragmentRelativeLayout.animate()
                                    .alpha(0.0f)
                                    .setDuration(200)
                                    .translationY(0)
                                    .setListener(new AnimatorListenerAdapter() {
                                        @SuppressLint("SetTextI18n")
                                        @Override
                                        public void onAnimationEnd(Animator animation) {
                                            super.onAnimationEnd(animation);
                                            mapFragmentRelativeLayout.setAlpha(1);
                                        }
                                    });

                        }
                    });


            hideTextView.animate()
                    .alpha(0.0f)
                    .setListener(new AnimatorListenerAdapter() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            hideTextView.setText(R.string.show_options);
                            hideTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_arrow_drop_down_black_24dp, 0, 0, 0);
                            hideTextView.setAlpha(1);
                        }
                    });

            viewDivider.animate()
                    .translationX(0)
                    .alpha(0.0f)
                    .setListener(new AnimatorListenerAdapter() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                        }
                    });

            optionsShowed = false;
        } else {

            optionsLinearLayout.animate()
                    .setDuration(500)
                    .translationY(0)
                    .alpha(1.0f)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            optionsLinearLayout.setVisibility(View.VISIBLE);
                            mapFragmentRelativeLayout.animate()
                                    .alpha(0.0f)
                                    .setDuration(200)
                                    .translationY(0)
                                    .setListener(new AnimatorListenerAdapter() {
                                        @SuppressLint("SetTextI18n")
                                        @Override
                                        public void onAnimationEnd(Animator animation) {
                                            super.onAnimationEnd(animation);
                                            mapFragmentRelativeLayout.setAlpha(1);
                                        }
                                    });

                        }
                    });



            hideTextView.animate()
                    .alpha(0.0f)
                    .setListener(new AnimatorListenerAdapter() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            hideTextView.setText(R.string.hide);
                            hideTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_arrow_drop_up_black_24dp, 0, 0, 0);
                            hideTextView.setAlpha(1);
                        }
                    });

            viewDivider.animate()
                    .setDuration(300)
                    .alpha(1)
                    .translationX(0)
                    .setListener(new AnimatorListenerAdapter() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                        }
                    });

            optionsShowed = true;
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }
}