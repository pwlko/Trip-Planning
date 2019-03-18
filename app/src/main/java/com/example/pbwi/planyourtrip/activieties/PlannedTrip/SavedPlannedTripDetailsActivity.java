package com.example.pbwi.planyourtrip.activieties.PlannedTrip;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pbwi.planyourtrip.R;
import com.example.pbwi.planyourtrip.activieties.Attractions.GoogleAttractionEntry;
import com.example.pbwi.planyourtrip.activieties.Attractions.PlannedTrackItemEntry;
import com.example.pbwi.planyourtrip.activieties.Attractions.PlannedTrackListAdapter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class SavedPlannedTripDetailsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap mGoogleMap;
    private String savedTrackName;
    private Map<String,HashMap<String,String>> tripMap;
    private int numberOfPlaces = 0;
    private String targetID = null;
    private static final int GOOGLE_API_CLIENT_ID = 0;
    private GoogleApiClient mGoogleApiClient;
    private ArrayList<ArrayList<Place>> placesTripList;
    private ArrayList<ArrayList<Place>> placesTripToListList;

    private Place targetPlace;

    private boolean optionsShowed = true;
    private TableLayout optionsTableLayout;
    private LinearLayout mapFragmentLinearLayout;
    private TextView hideTextView;
    private View viewDivider;

    private ArrayList<List<PlannedTrackItemEntry>> plannedTracksList;
    private List<PlannedTrackItemEntry> trackItemsList;


    @SuppressLint("SimpleDateFormat")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.saved_trip_details_layout);



        Bundle bundle = getIntent().getExtras();

        TextView targetLocationValueTextView = findViewById(R.id.trip_target_location_value_TextView);
        TextView creationDateValueTextView = findViewById(R.id.trip_creation_date_value_TextView);
        TextView numberOfDaysTextView = findViewById(R.id.trip_number_of_days_value_TextView);
        TextView placesToVisitNumberTextView = findViewById(R.id.trip_places_to_visit_value_TextView);
        optionsTableLayout = findViewById(R.id.parametersTableLayout);
        mapFragmentLinearLayout = findViewById(R.id.trip_details_map_fragment_LinearLayout);
        hideTextView = findViewById(R.id.saved_trip_details_hide_show_TextView);
        viewDivider = findViewById(R.id.saved_trip_details_divider_View);

        savedTrackName = Objects.requireNonNull(bundle).getString("name");
        Serializable serializable = bundle.getSerializable("tripMap");
        tripMap = (Map<String,HashMap<String,String>>) serializable;

        placesTripList = new ArrayList<>();

//        for(int i = 0; i < tripMap.size(); i ++){
//            placesTripList.add(new ArrayList<Place>());
//        }

//        numberOfPlaces = Integer.valueOf(Objects.requireNonNull(bundle.getString("placesToVisitNumber")));
        Long unixCreationDate = Long.valueOf(Objects.requireNonNull(Objects.requireNonNull(bundle).getString("creationDay")));
        Date creationDate = new Date(unixCreationDate);
        @SuppressLint("SimpleDateFormat") String formattedStartDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(creationDate);
        creationDateValueTextView.setText(formattedStartDate);
        numberOfDaysTextView.setText(bundle.getString("numberOfDays"));
        placesToVisitNumberTextView.setText(bundle.getString("placesToVisitNumber"));
        targetLocationValueTextView.setText(bundle.getString("targetAddress"));
        targetID = bundle.getString("targetLocation");

        Log.i("tripMap.size()", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(System.currentTimeMillis()) + " : " + tripMap.size());


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        Objects.requireNonNull(actionBar).setTitle(savedTrackName);
        actionBar.setDisplayHomeAsUpEnabled(true);

        if (googleServicesAvailable()) {
            initMap();
        }

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, GOOGLE_API_CLIENT_ID /* clientId */, this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();
        countNumberOfPlaces();
        getName();

    }

    private void countNumberOfPlaces(){

       int counter = 0;

        for(Map.Entry<String, HashMap<String, String>> entryDay: tripMap.entrySet()) {
            String key = entryDay.getKey();

            HashMap<String, String> dayList = entryDay.getValue();
            for (Map.Entry<String, String> entryPlace : dayList.entrySet()) {
                if(!entryPlace.getValue().equals(targetID))
                    counter++;
            }
        }
        numberOfPlaces = counter;

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
        //drawMap();
    }

    private void drawMap() {
        int[] colors = {Color.RED, Color.BLUE, Color.BLACK, Color.GREEN, Color.GRAY, Color.CYAN, Color.YELLOW};
        MarkerOptions targetOptions = new MarkerOptions()
                .position(targetPlace.getLatLng())
                .title(targetPlace.getName().toString());
        mGoogleMap.addMarker(targetOptions);

        for(int i = 0; i < placesTripList.size(); i++){
            if(placesTripList.size() == 2)
                continue;
            for(int j = 1; j < placesTripList.get(i).size(); j++) {

                Place place1 = placesTripList.get(i).get(j-1),
                        place2 = placesTripList.get(i).get(j);

                    PolylineOptions polylineOptions = new PolylineOptions()
                            .add(place1.getLatLng(), place2.getLatLng())
                            .color(colors[i]);
                    mGoogleMap.addPolyline(polylineOptions);

                    if(j < placesTripList.get(i).size() - 1) {
                        MarkerOptions options = new MarkerOptions()
                                .position(place2.getLatLng())
                                .title(place2.getName().toString());
                        mGoogleMap.addMarker(options);
                    }
                }
            }



            goToLocation(targetPlace.getLatLng());

    }

    private void goToLocation(LatLng latLng) {
        CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(15).build();
        mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    public void getName(){
        // Log.i("getName", "TUOSHOFHSDOHSODFd");

        String[] placesIDTable = new String[numberOfPlaces + 1];
        String[] tracksTable = new String[numberOfPlaces + tripMap.size() + tripMap.size()*2];
        placesIDTable[0] = targetID;
        final Map<String,String> tripsMap = new HashMap<>();
        final Map<String,String> placesSequenceMap = new HashMap<>();
        int[] daySize = new int[tripMap.size()];


        Toast.makeText(this, " " + placesTripList.size(), Toast.LENGTH_SHORT).show();


        String idies ="", track = "";
        int i = 1; //for placesIDTable
        int j = 0; //for tracksTable


        for(Map.Entry<String, HashMap<String, String>> entryDay: tripMap.entrySet()) {
            String key = entryDay.getKey();
            String keyIndex = key.replace("Day_", "");
            tracksTable[j++] = key;
            tracksTable[j++] = targetID;
            track += "   " + key + " " + targetID;



            if(i == 1){
                idies += targetID;
            }
            HashMap<String, String> dayList = entryDay.getValue();
            daySize[Integer.valueOf(keyIndex)] = dayList.size();

            if(dayList.size() == 2){
                track += " " + targetID;
                tracksTable[j++] = targetID;
                continue;
            }


            String lastKey = (dayList.size()-1) + "_";
            for (Map.Entry<String, String> entryPlace : dayList.entrySet()) {

                if(entryPlace.getKey().startsWith(lastKey) || entryPlace.getKey().startsWith("0_")){
                    continue;
                }
                placesSequenceMap.put(entryPlace.getValue(),entryPlace.getKey().replace("_" + entryPlace.getValue(),""));
                tripsMap.put(entryPlace.getValue(), keyIndex);
                placesIDTable[i] = entryPlace.getValue();
                tracksTable[j] = entryPlace.getValue();
                track += " " + entryPlace.getValue();
                idies += " " + entryPlace.getValue();
                i++;
                j++;
            }
            tracksTable[j++] = targetID;
            track += " " + targetID;

        }

        Log.i("id's table", idies);
        Log.i("id's table", track);

        for(int n = 0; n < tripMap.size(); n ++){
            int size = daySize[n];
            placesTripList.add(new ArrayList<Place>());
            for(int k = 0; k < size; k ++){
                Place place = targetPlace;
                placesTripList.get(n).add(place);

            }
        }


        Places.GeoDataApi.getPlaceById(mGoogleApiClient, placesIDTable)
                .setResultCallback(new ResultCallback<PlaceBuffer>() {

                    @Override
                    public void onResult(@NonNull PlaceBuffer places) {
                        if (places.getStatus().isSuccess()) {

                            for(int i = 0; i < places.getCount(); i++){
                                if(places.get(i).getId().equals(targetID)){
                                    targetPlace = places.get(i);
                                    continue;
                                }
                                //int index = Integer.valueOf(Objects.requireNonNull(tripsMap.get(Objects.requireNonNull(places).get(i))));
                                final Place place = places.get(i);
                                int index = Integer.valueOf(Objects.requireNonNull(placesSequenceMap.get(place.getId())).replace("_ID",""));
                                placesTripList.get(Integer.valueOf(Objects.requireNonNull(tripsMap.get(place.getId())))).remove(index);
                                placesTripList.get(Integer.valueOf(Objects.requireNonNull(tripsMap.get(place.getId())))).add(index, place);
                                Log.i("Place", place.getId() + " " + Integer.valueOf(Objects.requireNonNull(tripsMap.get(place.getId()))));
                            }
                            normalizeTrip();
                            makePlannedRouteLists();
                            drawMap();


                        } else {
                            Toast.makeText(SavedPlannedTripDetailsActivity.this, "Can't get place name", Toast.LENGTH_SHORT).show();
                        }
                        places.release();
                    }
                });
    }

    private void normalizeTrip(){

        for(int i = 0; i < placesTripList.size(); i++)
            for(int j = 0; j < placesTripList.get(i).size(); j++)
                if (placesTripList.get(i).get(j) == null) {
                    placesTripList.get(i).add(j,targetPlace);
                    placesTripList.get(i).remove(j+1);
                }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public void hideHandler(View view) {
        hideMenu();
    }

    private void hideMenu() {
        if (optionsShowed) {
            optionsTableLayout.animate()
                    .setDuration(500)
                    .translationY(0)
                    .alpha(0.0f)
                    .setListener(new AnimatorListenerAdapter() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            optionsTableLayout.setVisibility(View.GONE);
                            mapFragmentLinearLayout.animate()
                                    .alpha(0.0f)
                                    .setDuration(200)
                                    .translationY(0)
                                    .setListener(new AnimatorListenerAdapter() {
                                        @SuppressLint("SetTextI18n")
                                        @Override
                                        public void onAnimationEnd(Animator animation) {
                                            super.onAnimationEnd(animation);
                                            mapFragmentLinearLayout.setAlpha(1);
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

            optionsTableLayout.animate()
                    .setDuration(500)
                    .translationY(0)
                    .alpha(1.0f)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            optionsTableLayout.setVisibility(View.VISIBLE);
                            mapFragmentLinearLayout.animate()
                                    .alpha(0.0f)
                                    .setDuration(200)
                                    .translationY(0)
                                    .setListener(new AnimatorListenerAdapter() {
                                        @SuppressLint("SetTextI18n")
                                        @Override
                                        public void onAnimationEnd(Animator animation) {
                                            super.onAnimationEnd(animation);
                                            mapFragmentLinearLayout.setAlpha(1);
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

    public void seeDesignatedListHandler(View view) {
        buildPlannedTrackDialog();
    }

    private void buildPlannedTrackDialog() {

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(SavedPlannedTripDetailsActivity.this);

        LayoutInflater layoutInflater = this.getLayoutInflater();
        View mView = layoutInflater.inflate(R.layout.planned_route_list_layout, null);

        trackItemsList = new ArrayList<>();
        trackItemsList.addAll(plannedTracksList.get(0));
        final PlannedTrackListAdapter plannedTrackListAdapter = new PlannedTrackListAdapter(SavedPlannedTripDetailsActivity.this, 0, trackItemsList);

        ListView listView = mView.findViewById(R.id.planned_route_ListView);
        listView.setAdapter(plannedTrackListAdapter);


        Spinner dayNumberSpinner = mView.findViewById(R.id.day_number_Spinner);
        Button cancelButton = mView.findViewById(R.id.planned_route_cancel_Button);

        String[] daysTable = new String[placesTripList.size()];
        for (int i = 0; i < placesTripList.size(); i++)
            daysTable[i] = "Day " + (i + 1);


        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(SavedPlannedTripDetailsActivity.this, android.R.layout.simple_spinner_item, daysTable);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dayNumberSpinner.setAdapter(spinnerAdapter);

        mBuilder.setView(mView);
        final Dialog dialog = mBuilder.create();
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        dayNumberSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                trackItemsList.clear();
                trackItemsList.addAll(plannedTracksList.get(position));
                plannedTrackListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();

    }

    public void makePlannedRouteLists() {
        plannedTracksList = new ArrayList<>();
        for (int j = 0; j < placesTripList.size(); j++) {
            plannedTracksList.add(new ArrayList<PlannedTrackItemEntry>());
            for (int i = 1; i < placesTripList.get(j).size(); i++) {
                Place place1 = placesTripList.get(j).get(i - 1),
                        place2 = placesTripList.get(j).get(i);
                GoogleAttractionEntry googleAttractionEntry1 = new GoogleAttractionEntry(place1.getName().toString(), place1.getLatLng().latitude, place1.getLatLng().longitude, place1.getRating(), place1.getId(), Objects.requireNonNull(place1.getAddress()).toString(), -1),
                        googleAttractionEntry2 = new GoogleAttractionEntry(place2.getName().toString(), place2.getLatLng().latitude, place2.getLatLng().longitude, place2.getRating(), place2.getId(), Objects.requireNonNull(place2.getAddress()).toString(), -1);
                PlannedTrackItemEntry plannedTrackItemEntry = new PlannedTrackItemEntry(googleAttractionEntry1, googleAttractionEntry2);

                plannedTracksList.get(j).add(plannedTrackItemEntry);
            }
        }
    }


}
