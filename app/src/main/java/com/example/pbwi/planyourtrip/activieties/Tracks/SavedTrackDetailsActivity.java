package com.example.pbwi.planyourtrip.activieties.Tracks;

import android.app.Dialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pbwi.planyourtrip.R;
import com.example.pbwi.planyourtrip.activieties.SavedPlaces.SavedPlaceEntry;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SavedTrackDetailsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mGoogleMap;
    private String savedTrackName;
    private HashMap<String, LocationEntry> locationsMap;
    private ArrayList<LocationEntry> locationsList;
    private FirebaseDatabase database;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.saved_track_details_layout);



        Bundle bundle = getIntent().getExtras();

        TextView descriptionTextView = findViewById(R.id.track_detail_description_value_TextView);
        TextView startTimeTextView = findViewById(R.id.track_detail_startTime_value_TextView);
        TextView endTimeTextView = findViewById(R.id.track_detail_endTime_value_TextView);
        TextView savedPointsNumberTextView = findViewById(R.id.track_detail_saved_points_number_value_TextView);


        savedTrackName = Objects.requireNonNull(bundle).getString("name");
        Serializable serializable = bundle.getSerializable("locationsMap");
        locationsMap = (HashMap<String, LocationEntry>) serializable;
        String description = Objects.requireNonNull(bundle).getString("description");
        if(description == null || description.equals(""))
            descriptionTextView.setText(R.string.no_description);
        else
            descriptionTextView.setText(description);
        startTimeTextView.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Long.valueOf(Objects.requireNonNull(bundle.getString("startTime")))));
        endTimeTextView.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Long.valueOf(Objects.requireNonNull(bundle.getString("endTime")))));
        savedPointsNumberTextView.setText(bundle.getString("savedPointsNumber"));

        Log.i("locationsMap.size()", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(System.currentTimeMillis()) + " : " + locationsMap.size());


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        Objects.requireNonNull(actionBar).setTitle(savedTrackName);
        actionBar.setDisplayHomeAsUpEnabled(true);

        if (googleServicesAvailable()) {
            initMap();
        }

    }
    public void initLocationsPath(){
        locationsList = new ArrayList<>(locationsMap.size());
        final ArrayList<String> savedPlacesIdiesList = new ArrayList<>();
        LocationEntry locationEntry = null;



        for(int i = 0; i < locationsMap.size(); i++)
            locationsList.add(locationEntry);
        for(Map.Entry<String, LocationEntry> entryDay: locationsMap.entrySet()) {
            String key = entryDay.getKey();

            if(key.contains("_s")) {
                key = key.replace("ID_", "");
                key = key.replace("_s", "");
                String savedPlaceId = entryDay.getValue().getSavedPlaceID();
                if(savedPlaceId != null)
                    savedPlacesIdiesList.add(savedPlaceId);
            }
            else
                key = key.replace("ID_", "");

            int prefix = Integer.valueOf(key.replace("ID_",""));

            locationsList.set(prefix, entryDay.getValue());

        }

        final DatabaseReference databaseReference =  database.getReference("UsersObjects/SavedPlaces/" + Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
        final int savedPlacesIdiesListSize = savedPlacesIdiesList.size();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(int i = 0; i < savedPlacesIdiesListSize; i++) {
                    String childID = savedPlacesIdiesList.get(i);
                    if(dataSnapshot.hasChild(childID)){
                        SavedPlaceEntry placeEntry = dataSnapshot.child(childID).getValue(SavedPlaceEntry.class);
                        if(placeEntry != null) {
                            MarkerOptions options = new MarkerOptions()
                                    .position(new LatLng(placeEntry.getLat(),placeEntry.getLng()))
                                    .title(placeEntry.getName())
                                    .snippet(placeEntry.getDescription());
                            mGoogleMap.addMarker(options);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    public boolean googleServicesAvailable() {
        GoogleApiAvailability api = GoogleApiAvailability.getInstance();
        int isAvailable = api.isGooglePlayServicesAvailable(this);
        if (isAvailable == ConnectionResult.SUCCESS) {
            return true;
        } else if (api.isUserResolvableError(isAvailable)) {
            Dialog dialog = api.getErrorDialog(this, isAvailable, 0);
            dialog.show();
        } else {
            Toast.makeText(this, "Can't connect to play services", Toast.LENGTH_LONG).show();
        }
        return false;
    }

    private void initMap() {
        MapFragment mSupportMapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.trip_details_map_Fragment);
        if (mSupportMapFragment == null) {
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            mSupportMapFragment = MapFragment.newInstance();
            fragmentTransaction.replace(R.id.trip_details_map_Fragment, mSupportMapFragment).commit();
        }

        mSupportMapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        database = FirebaseDatabase.getInstance();
        initLocationsPath();
        drawMap();
    }

    private void drawMap() {
        LatLng latLng1, latLng2, startLatLng = null;
        String markerTitle = "";
        for(int i = 0; i < locationsList.size(); i++){
            LocationEntry locationEntry = locationsList.get(i);

            if(i == 0){
                markerTitle = getString(R.string.start_point_title);
                startLatLng = new LatLng(locationEntry.getLatitude(), locationEntry.getLongitude());
                MarkerOptions options = new MarkerOptions()
                        .position(startLatLng)
                        .title(markerTitle);
                mGoogleMap.addMarker(options);
                Log.i("drawMap", "test znaczników");
            }
            else {
                latLng1 = startLatLng;
                latLng2 = new LatLng(locationEntry.getLatitude(), locationEntry.getLongitude());
                PolylineOptions polylineOptions = new PolylineOptions()
                        .add(latLng1, latLng2);
                mGoogleMap.addPolyline(polylineOptions);
                startLatLng = latLng2;
            }
        }
        markerTitle = getString(R.string.end_point_title);
        MarkerOptions options = new MarkerOptions()
                .position(startLatLng)
                .title(markerTitle);
        mGoogleMap.addMarker(options);

            goToLocation(startLatLng);

    }

    private void goToLocation(LatLng latLng) {
        CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(15).build();
        mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

}
