package com.example.pbwi.planyourtrip.activieties.SavedPlaces;

import android.app.Dialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pbwi.planyourtrip.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.SimpleDateFormat;
import java.util.Objects;

public class SavedPlaceDetailsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private TextView descriptionTextView  = null;
    private TextView latTextView = null;
    private TextView lngTextView = null;
    private TextView dateTextView  = null;

    private GoogleMap mGoogleMap;
    private LatLng savedPlaceLatLng= new LatLng(52,25);
    private String savedPlaceName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.saved_place_details_layout);



        Bundle bundle = getIntent().getExtras();

        descriptionTextView  = findViewById(R.id.place_description_value_TextView);
        latTextView = findViewById(R.id.saved_place_lat_value_TextView);
        lngTextView = findViewById(R.id.saved_place_lng_value_TextView);
        dateTextView  = findViewById(R.id.date_value_TextView);

        savedPlaceName = Objects.requireNonNull(bundle).getString("name");
        String description = Objects.requireNonNull(bundle).getString("description");
        if(description == null || description.equals(""))
            descriptionTextView.setText(R.string.no_description);
        else
            descriptionTextView.setText(description);
        latTextView.setText(bundle.getString("lat"));
        lngTextView.setText(bundle.getString("lng"));
        dateTextView.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Long.valueOf(Objects.requireNonNull(bundle.getString("date")))));
        savedPlaceLatLng = new LatLng(Double.valueOf(Objects.requireNonNull(bundle.getString("lat"))), Double.valueOf(Objects.requireNonNull(bundle.getString("lng"))));


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        Objects.requireNonNull(actionBar).setTitle(savedPlaceName);
        actionBar.setDisplayHomeAsUpEnabled(true);

        if (googleServicesAvailable()) {
            initMap();
        }

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
        MapFragment mSupportMapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.saved_place_details_map_Fragment);
        if (mSupportMapFragment == null) {
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            mSupportMapFragment = MapFragment.newInstance();
            fragmentTransaction.replace(R.id.saved_place_details_map_Fragment, mSupportMapFragment).commit();
        }

        mSupportMapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        drawMap();
    }

    private void drawMap() {

            MarkerOptions options = new MarkerOptions()
                    .position(savedPlaceLatLng)
                    .title(savedPlaceName);
            mGoogleMap.addMarker(options);

            goToLocation(savedPlaceLatLng);

    }

    private void goToLocation(LatLng latLng) {
        CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(15).build();
        mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

}
