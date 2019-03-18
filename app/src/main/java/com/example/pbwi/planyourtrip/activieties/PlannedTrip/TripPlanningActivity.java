package com.example.pbwi.planyourtrip.activieties.PlannedTrip;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pbwi.planyourtrip.R;
import com.example.pbwi.planyourtrip.activieties.BaseActivity;
import com.example.pbwi.planyourtrip.activieties.Attractions.GoogleAttractionsToVisitListAdapter;
import com.example.pbwi.planyourtrip.activieties.Attractions.GoogleAttractionEntry;
import com.example.pbwi.planyourtrip.activieties.Attractions.NearbyGoogleAttractionListAdapter;
import com.example.pbwi.planyourtrip.activieties.Attractions.PlannedTrackItemEntry;
import com.example.pbwi.planyourtrip.activieties.Attractions.PlannedTrackListAdapter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;


public class TripPlanningActivity extends BaseActivity implements OnMapReadyCallback {

    private final int MAX_TO_VISTIT = 50;
    private final int MAX_VISIT_DAYS = 7;
    private final String apiKey = "";
    private ProgressBar progressBar;

    private  JSONObject jsonObject;
    private List<GoogleAttractionEntry> nearbyGooglePlacesList;
    private List<GoogleAttractionEntry> attractionsToVisitList;
    private List<PlannedTrackItemEntry> trackItemsList;
    private List<Polyline> polylineList;
    private Map<String, Integer> addedToVisitListMap;
    private Map<String, Marker> addedMarkersMap;


//    ProgressDialog progressDialog;

    private NearbyGoogleAttractionListAdapter nearbyGooglePlacesListAdapter;
    private GoogleAttractionsToVisitListAdapter googleAttractionsToVisitListAdapter;

    private ValueEventListener googleAttractionListener;

    private JSONArray nearbyPlacesJsonArray;

    private RelativeLayout relativeLayout;

    private ArrayList<List<LatLng>> locationList;
    private ArrayList<List<PlannedTrackItemEntry>> plannedTracksList;
    private ArrayList<List<GoogleAttractionEntry>> plannedHeuristicRoutes;

//    private Place place;
    private GoogleAttractionEntry startPlace;
    //    private PlaceTestModel place = null;
    private HeuristicRoutePlanning heuristic = null;
    private int PLACE_AUTOCOMPLETE_REQUEST_CODE = 0;
    private int PLACE_PICKER_REQUEST = 1;
    private int lastRadius = 0;
    private int numberOfDaysValue = 0, visitPerDay = 50;

    private boolean optionsShowed = true;

    private LatLng latLng;
    private double startPlaceLat = 0, startPlaceLng = 0;


    private Button makeRoadPlanningButton;
    private ImageView nearSearchImageView;
    private EditText numberOfDaysEditText;
    private EditText kmLimitEditText;
    private EditText visitPerDayEditText;


    private TextView hideTextView;
    private TextView startPlaceTextView;
    private TextView foundedNearbyTextView, foundedNearbyValueTextView, placesTooSeeTextView, placesTooSeeValueTextView;

    private GoogleMap mGoogleMap;
    private PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
    private DatabaseReference myRef;
    private FirebaseDatabase database;
    private FirebaseAuth firebaseAuth;


    private View viewDivider;

    private RelativeLayout mapFragmentRelativeLayout;
    private LinearLayout optionsLinearLayout, nearcbyPlacesSearchButton, searchManualyLinearLayout, seeDesignatedListButton, saveTripButton;
    private LinearLayout afterPlanningLinearLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = getLayoutInflater();
        LinearLayout container = findViewById(R.id.content_frame);
        inflater.inflate(R.layout.trip_planning_activity, container);

        ActionBar actionBar = getSupportActionBar();
        Objects.requireNonNull(actionBar).setTitle(R.string.trip_planning_menu);

        makeRoadPlanningButton = findViewById(R.id.road_planning_Button);
        nearcbyPlacesSearchButton = findViewById(R.id.search_nearby_Button);
        nearSearchImageView = findViewById(R.id.search_nearby_ImageView);
        startPlaceTextView = findViewById(R.id.startPlaceTV);
        seeDesignatedListButton = findViewById(R.id.see_designated_list_Button);
        saveTripButton = findViewById(R.id.save_trip_Button);

        foundedNearbyTextView = findViewById(R.id.founded_nearby_TextView);
        foundedNearbyValueTextView = findViewById(R.id.founded_nearby_value_TextView);
        placesTooSeeTextView = findViewById(R.id.places_too_see_TextView);
        placesTooSeeValueTextView = findViewById(R.id.places_too_see_value_TextView);

        afterPlanningLinearLayout = findViewById(R.id.see_designated_list_LinearLayout);
        afterPlanningLinearLayout.setVisibility(View.GONE);

        relativeLayout = findViewById(R.id.mapFragmentRelativeLayout1);
        hideTextView = findViewById(R.id.trip_planning_hide_show_TextView);
        viewDivider = findViewById(R.id.trip_planning_divider_View);
        optionsLinearLayout = findViewById(R.id.trip_planning_parameters_LinearLayout);
        mapFragmentRelativeLayout = findViewById(R.id.trip_planning_divider_maoFragment_RelativeLayout);
        searchManualyLinearLayout = findViewById(R.id.search_manually_LinearLayout);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);

        nearbyGooglePlacesList = new ArrayList<>();
        attractionsToVisitList = new ArrayList<>();
        addedToVisitListMap = new HashMap<>();
        addedMarkersMap = new HashMap<>();
        polylineList = new ArrayList<>();
        googleAttractionsToVisitListAdapter = new GoogleAttractionsToVisitListAdapter(TripPlanningActivity.this, 0, attractionsToVisitList);
        initMap();
//        progressDialog = new ProgressDialog(TripPlanningActivity.this);

        hideTextView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                hideMenu();
            }
        });
        firebaseAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("UsersObjects/SavedPlannedTrips/" + Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid());


        nearcbyPlacesSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                setNearbyPlaces();
                if (startPlace == null)
                    Toast.makeText(TripPlanningActivity.this, R.string.no_chosen_start_place_message, Toast.LENGTH_LONG).show();
                else {
                    nearcbyPlacesSearchButton.setClickable(false);
                            setNearbyPlaces();
                }
            }
        });


        makeRoadPlanningButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (startPlace == null)
                    Toast.makeText(TripPlanningActivity.this, R.string.no_chosen_start_place_message, Toast.LENGTH_LONG).show();
                else if (attractionsToVisitList.size() < 2)
                    Toast.makeText(TripPlanningActivity.this, R.string.not_enough_chosen_places_warning_message, Toast.LENGTH_LONG).show();
                else {

                    buildHeuristicOptionsDialog();
                }
            }


        });

    }

    public void startPlaceHandler(View view) {
        try {
            startActivityForResult(builder.build(TripPlanningActivity.this), PLACE_PICKER_REQUEST);

        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    public void searchManuallyOnClick(View view) {
        try {
            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                    .build(this);
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);

        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place;
                place = PlacePicker.getPlace(this, data);
                startPlace = new GoogleAttractionEntry(place.getName().toString(), place.getLatLng().latitude, place.getLatLng().longitude, place.getRating(), place.getId(), Objects.requireNonNull(place.getAddress()).toString(), -1);

                nearbyGooglePlacesList.clear();

                if(startPlace != null){
                    nearSearchImageView.setBackground(getResources().getDrawable(R.drawable.rounded_buttons));
                    nearcbyPlacesSearchButton.setClickable(true);
                }
                startPlaceTextView.setText(startPlace.getName());
                latLng = new LatLng(startPlace.getLat(),startPlace.getLng());
                startPlaceLat = latLng.latitude;
                startPlaceLng = latLng.longitude;
                goToLocation(latLng, startPlace.getPlaceID());

            }
        } else if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                buildPlaceAutocompleteAddDialog(place);

                Log.i("onActivityResult(RESULT_OK)", "Place: " + place.toString() + " " + place.getRating());
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                Toast.makeText(this, status.getStatusMessage(), Toast.LENGTH_LONG).show();
                // TODO: Handle the error.
                Log.i("onActivityResult(RESULT_ERROR)", status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    private boolean validateEditTexts() {
        int km = 0;
        if (!numberOfDaysEditText.getText().toString().isEmpty() && !kmLimitEditText.getText().toString().isEmpty() && !visitPerDayEditText.getText().toString().isEmpty()) {
            try {
                numberOfDaysValue = Integer.parseInt(numberOfDaysEditText.getText().toString());
                km = Integer.parseInt(kmLimitEditText.getText().toString());
                visitPerDay = Integer.parseInt(visitPerDayEditText.getText().toString());
            } catch (NumberFormatException e) {
                Log.i("", e.getMessage() + " is not a number");
                return false;
            } finally {
                if (numberOfDaysValue >= 1 && numberOfDaysValue < 7 && km >= 5 && km <= 500 && visitPerDay >= 0 && visitPerDay <= 50) {
                    Toast.makeText(getApplicationContext(), "Dane poprawne", Toast.LENGTH_SHORT).show();
                    return true;
                } else if (numberOfDaysValue < 1 || numberOfDaysValue > 7)
                    Toast.makeText(getApplicationContext(), "Number of days must be minimum 1 and maxiumum 7", Toast.LENGTH_SHORT).show();
                else if (km < 5 || km > 700)
                    Toast.makeText(getApplicationContext(), "Number of kilometers must be minimum 5 and maxiumum 500", Toast.LENGTH_SHORT).show();
                else if (visitPerDay < 1 || visitPerDay > 50)
                    Toast.makeText(getApplicationContext(), "Number of visits per day must be minimum 1 and maxiumum 50", Toast.LENGTH_SHORT).show();
            }
        }
        return false;
    }

    @NonNull
    private ArrayList<List<LatLng>> getLocations(ArrayList<List<GoogleAttractionEntry>> listaTras) {
        // Bundle trasa = bundle.getBundle("trasa");
        // int size = atractionEntries.size();

        ArrayList<List<LatLng>> locationList = new ArrayList();
        for (int j = 0; j < listaTras.size(); j++) {
            locationList.add(new ArrayList<LatLng>());
            for (int i = 0; i < listaTras.get(j).size(); i++) {
                double[] locationData = new double[2];
                locationData[0] = listaTras.get(j).get(i).getLat();
                locationData[1] = listaTras.get(j).get(i).getLng();
                locationList.get(j).add(new LatLng(locationData[0], locationData[1]));
            }
        }
        return locationList;
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
        MapFragment mSupportMapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.trip_planning_MapFragment);
        if (mSupportMapFragment == null) {
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            mSupportMapFragment = MapFragment.newInstance();
            fragmentTransaction.replace(R.id.trip_planning_MapFragment, mSupportMapFragment).commit();
        }
//        if (locationList.isEmpty())
//            mSupportMapFragment.getView().setVisibility(View.GONE);
        mSupportMapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
//        drawMap();
    }

    private void drawMap() {
        int[] colors = {Color.RED, Color.BLUE, Color.BLACK, Color.GREEN, Color.GRAY, Color.CYAN, Color.YELLOW};
        if (!locationList.isEmpty()) {
//            MarkerOptions options = new MarkerOptions()
//                    .position(locationList.get(0).get(0))
//                    .title(place.getName());
//            mGoogleMap.addMarker(options);
            for (int i = 0; i < locationList.size(); i++) {
                PolylineOptions polylineOptions = new PolylineOptions().addAll(locationList.get(i));
                polylineOptions.color(colors[i]);
                Polyline polyline = mGoogleMap.addPolyline(polylineOptions);
                polylineList.add(polyline);

            }
            goToLocation(new LatLng(startPlace.getLat(),startPlace.getLng()), null);
        }
    }

    private void goToLocation(final LatLng latLng, final String id) {
        MarkerOptions options = new MarkerOptions()
                .position(latLng)
                .title("START");
        Marker marker = mGoogleMap.addMarker(options);
        if (id != null)
            addedMarkersMap.put(id, marker);
        CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(10).build();
        mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    private void convertJSONAndAddObjectsToList(final ArrayList<JSONArray> nearbyPlacesJsonArray) {

        for(int j = 0; j < nearbyPlacesJsonArray.size(); j++) {
            for (int i = 0; i < nearbyPlacesJsonArray.get(j).length(); i++) {

                try {

                    JSONObject jsonObject = nearbyPlacesJsonArray.get(j).getJSONObject(i);
//                && Double.valueOf(String.valueOf(jsonObject.get("rating")))> 0

                    if (jsonObject.get("rating") != null && jsonObject.get("place_id") != null) {
                        Double rate = Double.valueOf(String.valueOf(jsonObject.get("rating")));
                        String id = String.valueOf(jsonObject.get("place_id"));
//                    Log.i("placeID", "" +String.valueOf(jsonObject.get("place_id")));
                        if (rate == 0 || id == null)
                            continue;

//                        Log.i("rating", jsonObject.get("rating") + " ");
                        Double lat = (Double) jsonObject.getJSONObject("geometry").getJSONObject("location").get("lat");
                        Double lng = (Double) jsonObject.getJSONObject("geometry").getJSONObject("location").get("lng");
                        String address;
                        if (jsonObject.get("formatted_address") != null)
                            address = String.valueOf(jsonObject.get("formatted_address"));
                        else
                            address = String.valueOf(jsonObject.get("vicinity"));
                        String placeID = String.valueOf(jsonObject.get("place_id"));
                        int votesNumber = (int) jsonObject.get("user_ratings_total");
                        String name = String.valueOf(jsonObject.get("name"));

                        GoogleAttractionEntry googleAttractionEntry = new GoogleAttractionEntry(name, lat, lng, rate, placeID, address, votesNumber);
                        nearbyGooglePlacesList.add(googleAttractionEntry);
                        nearbyGooglePlacesListAdapter = new NearbyGoogleAttractionListAdapter(TripPlanningActivity.this, 0, nearbyGooglePlacesList);
                        nearbyGooglePlacesListAdapter.setNotifyOnChange(true);


//                        Log.i("convertJSONAndAddObjectsToList", googleAttractionEntry.getName() + " , " + googleAttractionEntry.getAddress() + " added to list");
                    }
//                Log.i("convertJSONAndAddObjectsToList", "List size: " + nearbyGooglePlacesList.size());
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
        foundedNearbyValueTextView.setText(String.valueOf(nearbyGooglePlacesList.size()));


    }


    private void savePlannedTrip(final String name) {

        DatabaseReference databaseReference = database.getReference("UsersObjects/SavedPlannedTrips/"
                + Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid());
        String planedTripID = databaseReference.push().getKey();
        DatabaseReference planedTripReference = database.getReference("UsersObjects/SavedPlannedTrips/"
                + Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid() + "/" + planedTripID);

        planedTripReference.child("name").setValue(name);
        planedTripReference.child("tripID").setValue(planedTripID);
        planedTripReference.child("targetLocation").setValue(startPlace.getPlaceID());
        planedTripReference.child("creationDate").setValue(System.currentTimeMillis());
        planedTripReference.child("numberOfDays").setValue(numberOfDaysValue);
        planedTripReference.child("numberOfPlacesToVistit").setValue(attractionsToVisitList.size());

        if (!plannedHeuristicRoutes.isEmpty())
            for (int i = 0; i < plannedHeuristicRoutes.size(); i++)
                for (int j = 0; j < plannedHeuristicRoutes.get(i).size(); j++) {
                    String placeID = plannedHeuristicRoutes.get(i).get(j).getPlaceID();
                    planedTripReference.child("Trip").child("Day_" + i)
                            .child(j + "_ID").setValue(placeID);
                }
        Toast.makeText(TripPlanningActivity.this, R.string.trip_saved, Toast.LENGTH_SHORT).show();
    }

    private void buildNearbyAttractionsDialog() {

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(TripPlanningActivity.this);
        LayoutInflater layoutInflater = this.getLayoutInflater();
        View mView = layoutInflater.inflate(R.layout.places_to_visit_list_layout, null);

        ListView listView = mView.findViewById(R.id.places_to_visit_ListView);
        listView.setAdapter(nearbyGooglePlacesListAdapter);
        if (nearbyGooglePlacesListAdapter != null)
            nearbyGooglePlacesListAdapter.notifyDataSetChanged();


//        final ImageButton imageButton = mView.findViewById(R.id.places_to_visit_menu_ImageButton);
        Button okButton = mView.findViewById(R.id.places_to_visit_ok_Button);
        Button cancelButton = mView.findViewById(R.id.places_to_visit_cancel_Button);
        final CheckBox selectAllCheckBox = mView.findViewById(R.id.places_to_visit_all_selection_CheckBox);


        mBuilder.setView(mView);
        final Dialog dialog = mBuilder.create();
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        selectAllCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectAllCheckBox.isChecked())
                    sellectAllNearbyPlaces(true);
                else
                    sellectAllNearbyPlaces(false);
            }
        });

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addAllToVisitList();
                googleAttractionsToVisitListAdapter.notifyDataSetChanged();
                nearbyGooglePlacesListAdapter.notifyDataSetChanged();
                uncheckAll();
                dialog.cancel();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uncheckAll();
                dialog.dismiss();
            }
        });

        dialog.show();

    }

    private void buildSavePlannedTripDialog() {

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(TripPlanningActivity.this);
        LayoutInflater layoutInflater = this.getLayoutInflater();
        View mView = layoutInflater.inflate(R.layout.confirmation_dialog_layout, null);

        final EditText nameEditText = mView.findViewById(R.id.planned_trip_name_EditText);
        Button okButton = mView.findViewById(R.id.planned_trip_ok_Button);
        Button cancelButton = mView.findViewById(R.id.planned_trip_cancel_Button);
        okButton.setText(R.string.save_trip);


        mBuilder.setView(mView);
        final Dialog dialog = mBuilder.create();
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (attractionsToVisitList.size() > 0) {
                    if (nameEditText.getText() == null || nameEditText.getText().length() == 0) {
                        nameEditText.setError(getString(R.string.have_to_name_trip_alert));
                    } else {
                        String tripName = nameEditText.getText().toString();
                        savePlannedTrip(tripName);
                    }


                }
                dialog.cancel();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uncheckAll();
                dialog.dismiss();
            }
        });

        dialog.show();

    }

    private void buildAttractionsToVisitDialog() {

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(TripPlanningActivity.this);
        LayoutInflater layoutInflater = this.getLayoutInflater();
        View mView = layoutInflater.inflate(R.layout.places_to_visit_list_layout, null);

        ListView listView = mView.findViewById(R.id.places_to_visit_ListView);
        listView.setAdapter(googleAttractionsToVisitListAdapter);


//        final ImageButton imageButton = mView.findViewById(R.id.places_to_visit_menu_ImageButton);
        Button okButton = mView.findViewById(R.id.places_to_visit_ok_Button);
        Button cancelButton = mView.findViewById(R.id.places_to_visit_cancel_Button);
        okButton.setText(R.string.delete_selected);


        mBuilder.setView(mView);
        final Dialog dialog = mBuilder.create();
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (attractionsToVisitList.size() > 0)
                    deleteSelectedPlacesToVistit();
                dialog.cancel();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uncheckAll();
                dialog.dismiss();
            }
        });

        dialog.show();

    }

    private void buildAttractionsListDialog() {

        AlertDialog.Builder builderSingle = new AlertDialog.Builder(TripPlanningActivity.this);
        builderSingle.setTitle(R.string.attraction_list);


        builderSingle.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builderSingle.setAdapter(googleAttractionsToVisitListAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String strName = Objects.requireNonNull(nearbyGooglePlacesListAdapter.getItem(which)).getName();
                AlertDialog.Builder builderInner = new AlertDialog.Builder(TripPlanningActivity.this);
                builderInner.setMessage(strName);
                builderInner.setTitle(R.string.your_selected_item_is);
                builderInner.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builderInner.show();
            }
        });
        builderSingle.show();

    }

    private void buildPlannedTrackDialog() {

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(TripPlanningActivity.this);

        LayoutInflater layoutInflater = this.getLayoutInflater();
        View mView = layoutInflater.inflate(R.layout.planned_route_list_layout, null);

        trackItemsList = new ArrayList<>();
        trackItemsList.addAll(plannedTracksList.get(0));
        final PlannedTrackListAdapter plannedTrackListAdapter = new PlannedTrackListAdapter(TripPlanningActivity.this, 0, trackItemsList);

        ListView listView = mView.findViewById(R.id.planned_route_ListView);
        listView.setAdapter(plannedTrackListAdapter);


        Spinner dayNumberSpinner = mView.findViewById(R.id.day_number_Spinner);
        Button cancelButton = mView.findViewById(R.id.planned_route_cancel_Button);

        String[] daysTable = new String[plannedHeuristicRoutes.size()];
        for (int i = 0; i < plannedHeuristicRoutes.size(); i++)
            daysTable[i] = "Day " + (i + 1);


        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(TripPlanningActivity.this, android.R.layout.simple_spinner_item, daysTable);
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


    private void buildHeuristicOptionsDialog() {


        AlertDialog.Builder mBuilder = new AlertDialog.Builder(TripPlanningActivity.this);
        LayoutInflater layoutInflater = this.getLayoutInflater();
        View mView = layoutInflater.inflate(R.layout.heuristic_option_layout, null);


        kmLimitEditText = mView.findViewById(R.id.heu_option_km_per_day_EditText);
        visitPerDayEditText = mView.findViewById(R.id.heu_option_visit_per_day_EditText);
        numberOfDaysEditText = mView.findViewById(R.id.heu_option_number_of_days_EditText);
        Button okButton = mView.findViewById(R.id.heu_options_ok_Button);
        Button cancelButton = mView.findViewById(R.id.heu_options_cancel_Button);

        mBuilder.setView(mView);
        final Dialog dialog = mBuilder.create();
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("buildHeuristicOptionsDialog", kmLimitEditText.getText() + " " +
                        visitPerDayEditText.getText() + " " +
                        numberOfDaysEditText.getText());
                if (polylineList.size() > 0) {
                    for (Polyline polyline : polylineList) {
                        polyline.remove();
                    }
                    polylineList.clear();
                }
                searchTrack();
                dialog.cancel();
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



    @SuppressLint("HandlerLeak")
    public void setNearbyPlaces() {

        if (startPlaceLat != 0 && startPlaceLng != 0) {

//                String urlJSON = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + startPlaceLat + "," + startPlaceLng + "&radius=" + radius * 1000 + "&keyword=attraction" + "&key=" + apiKey;
                String urlJSON = "https://maps.googleapis.com/maps/api/place/textsearch/json?query=atrakcje&location="+ startPlaceLat + "," + startPlaceLng + apiKey;
                ArrayList<JSONArray> JSONArrayPlacesList;
            JSONAsyncTask jsonAsyncTask = new JSONAsyncTask();
//                        JSONArrayPlacesList = (ArrayList<JSONArray>)
            jsonAsyncTask.execute(urlJSON);


        } else
            Toast.makeText(TripPlanningActivity.this, R.string.no_chosen_start_place_message, Toast.LENGTH_SHORT).show();
    }


    private void sellectAllNearbyPlaces(boolean option) {
        for (GoogleAttractionEntry entry : nearbyGooglePlacesList) {
            if (!entry.isAddedToVisit()) {
                entry.setChecked(option);
            }
        }
        nearbyGooglePlacesListAdapter.notifyDataSetChanged();
    }


    private void hideMenu() {
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


    public void searchTrack() {


        if (startPlace != null ){
            if (validateEditTexts()) {
                Log.i("makeRoadPlanningButton", startPlace.getName().toString() + " " + " " + Integer.parseInt(kmLimitEditText.getText().toString()) + " " + Integer.parseInt(numberOfDaysEditText.getText().toString()));
//            if (mGoogleMap != null) mGoogleMap.clear();

                for (int i = 0; i < attractionsToVisitList.size(); i++) {
                    attractionsToVisitList.get(i).setIndex(i);
                }

                heuristic = new HeuristicRoutePlanning(startPlace, Integer.parseInt(kmLimitEditText.getText().toString()), Integer.parseInt(numberOfDaysEditText.getText().toString()), visitPerDay, attractionsToVisitList);

                //heurystka
                heuristic.greedySolution();
                heuristic.localSearch();

                locationList = getLocations(heuristic.getRoute());
                plannedHeuristicRoutes = heuristic.getRoute();
                makePlannedRouteLists();
                buildPlannedTrackDialog();
                afterPlanningLinearLayout.setVisibility(View.VISIBLE);
                if (googleServicesAvailable()) {
                    drawMap();
                }
            }else
                Toast.makeText(getApplicationContext(), R.string.something_wrong_try_again, Toast.LENGTH_LONG).show();
        } else
            Toast.makeText(getApplicationContext(), R.string.no_start_place_chosen, Toast.LENGTH_LONG).show();


    }

    public void makePlannedRouteLists() {
        plannedTracksList = new ArrayList<>();
        for (int j = 0; j < plannedHeuristicRoutes.size(); j++) {
            plannedTracksList.add(new ArrayList<PlannedTrackItemEntry>());
            for (int i = 1; i < plannedHeuristicRoutes.get(j).size(); i++) {
                PlannedTrackItemEntry plannedTrackItemEntry = new PlannedTrackItemEntry(plannedHeuristicRoutes.get(j).get(i - 1), plannedHeuristicRoutes.get(j).get(i));

                plannedTracksList.get(j).add(plannedTrackItemEntry);
            }
        }
    }


    private void addAllToVisitList() {

        int availableSize = 0, counter = attractionsToVisitList.size();
        for (GoogleAttractionEntry item : nearbyGooglePlacesList) {
            if (item.isChecked())
                availableSize++;
        }

        if (availableSize <= MAX_TO_VISTIT - attractionsToVisitList.size()) {
            for (int i = 0; i < nearbyGooglePlacesList.size(); i++) {
                GoogleAttractionEntry item = nearbyGooglePlacesList.get(i);
                if (item.isChecked() && !item.isAddedToVisit()) {
                    GoogleAttractionEntry entry = new GoogleAttractionEntry(item.getName(), item.getLat(), item.getLng(), item.getRate(), item.getPlaceID(), item.getAddress(), item.getNumberOfRatings());
                    attractionsToVisitList.add(entry);
                    addedToVisitListMap.put(item.getPlaceID(), i);
                    item.setAddedToVisit(true);
                    Marker marker = mGoogleMap.addMarker(new MarkerOptions()
                            .position(new LatLng(entry.getLat(), entry.getLng()))
                            .title(entry.getName())) ;
//                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_place_black_24dp)));
                    addedMarkersMap.put(entry.getPlaceID(), marker);

                    counter++;
                }
            }
            placesTooSeeValueTextView.setText(String.valueOf(attractionsToVisitList.size()));

            Toast.makeText(TripPlanningActivity.this, R.string.place_added, Toast.LENGTH_SHORT).show();
        } else
            Toast.makeText(TripPlanningActivity.this, getString(R.string.you_can_add_only) + (MAX_TO_VISTIT - attractionsToVisitList.size()) + getString(R.string.more_places_to_visit), Toast.LENGTH_SHORT).show();
    }

    private void uncheckAll() {
        for (GoogleAttractionEntry item : nearbyGooglePlacesList) {
            item.setChecked(false);
        }
    }

    private void deleteSelectedPlacesToVistit() {

        ListIterator<GoogleAttractionEntry> listIterator = attractionsToVisitList.listIterator();
        while (listIterator.hasNext()) {
            GoogleAttractionEntry entry = listIterator.next();
            // check if result of student is "Fail"
            if (entry.isChecked()) {
                // remove it using iterator
                Log.i("deleteSelectedPlacesToVistit", "entry isChecked " + entry.getName());
                listIterator.remove();
                String id = entry.getPlaceID();
                nearbyGooglePlacesList.get(Objects.requireNonNull(addedToVisitListMap.get(id))).setAddedToVisit(false);
                Objects.requireNonNull(addedMarkersMap.get(id)).remove();
                addedMarkersMap.remove(id);

                Log.i("deleteSelectedPlacesToVistit", nearbyGooglePlacesList.get(addedToVisitListMap.get(entry.getPlaceID())).getName() + " entry isAddedToVisit " + nearbyGooglePlacesList.get(addedToVisitListMap.get(entry.getPlaceID())).isAddedToVisit());
                addedToVisitListMap.remove(id);
            }
        }

        placesTooSeeValueTextView.setText(String.valueOf(attractionsToVisitList.size()));
        googleAttractionsToVisitListAdapter.notifyDataSetChanged();
        nearbyGooglePlacesListAdapter.notifyDataSetChanged();
    }

    public void foundedNearbyOnClick(View view) {
        if (!nearbyGooglePlacesList.isEmpty())
            buildNearbyAttractionsDialog();
        else
            Toast.makeText(TripPlanningActivity.this, R.string.empty_nearby_list_message, Toast.LENGTH_LONG).show();
    }

    public void placesToSeeOnClick(View view) {
        if (!attractionsToVisitList.isEmpty())
            buildAttractionsToVisitDialog();
        else
            Toast.makeText(TripPlanningActivity.this, R.string.places_to_visit_list_empty_message, Toast.LENGTH_LONG).show();
    }



    public void buildPlaceAutocompleteAddDialog(final Place place) {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(TripPlanningActivity.this);
        LayoutInflater layoutInflater = this.getLayoutInflater();
        View mView = layoutInflater.inflate(R.layout.place_autocoplete_add_dialog, null);


        TextView placeName = mView.findViewById(R.id.manually_founded_place_name_TextView);
        placeName.setText(place.getName());

        Button yesButton = mView.findViewById(R.id.places_autocomplete_yes_Button);
        Button noButton = mView.findViewById(R.id.places_autocomplete_no_Button);

        mBuilder.setView(mView);
        final Dialog dialog = mBuilder.create();
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = (String) place.getName(), id = place.getId(),
                        address = (String) place.getAddress();

                double lat = place.getLatLng().latitude, lng = place.getLatLng().longitude;
                double rate = place.getRating();
                GoogleAttractionEntry entry = new GoogleAttractionEntry(name, lat, lng, rate, id, address, 0);
                attractionsToVisitList.add(entry);
                googleAttractionsToVisitListAdapter.notifyDataSetChanged();
                placesTooSeeValueTextView.setText(String.valueOf(attractionsToVisitList.size()));

                Marker marker = mGoogleMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title(entry.getName()));
                addedMarkersMap.put(entry.getPlaceID(), marker);
                goToLocation(new LatLng(lat, lng), entry.getPlaceID());
                dialog.cancel();
            }
        });

        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();


    }

    public void seeDesignatedListHandler(View view) {
        buildPlannedTrackDialog();
    }


    public void saveTripHandler(View view) {
        buildSavePlannedTripDialog();
    }


    private class JSONAsyncTask extends AsyncTask<String, String, List<JSONArray>> {

        private ProgressDialog progressDialog = new ProgressDialog(TripPlanningActivity.this);
        private List<JSONArray> JSONArrayPlacesList = new ArrayList<>();

        @Override
        protected void onPreExecute() {
            progressDialog.setMessage(getString(R.string.data_downloading_message));
            progressDialog.show();
        }

        @Override
        protected List<JSONArray> doInBackground(String... strings) {

            JSONObject object = getJSONObject(strings[0]);
            JSONArray nearbyPlacesJsonArray = null;
            if(object != null) {

                try {
                    nearbyPlacesJsonArray = object.getJSONArray("results");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (nearbyPlacesJsonArray != null) {
                    this.JSONArrayPlacesList.add(nearbyPlacesJsonArray);
                }


                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                for(int i = 0; i < 2; i++) {

                    try {
                        if (Objects.requireNonNull(object).getString("next_page_token") != null) {

                            Log.i("next_page_token", "true" + " " + object.getString("next_page_token"));
                            String nextPageToken = object.getString("next_page_token");
                            String newUrlJSON = "https://maps.googleapis.com/maps/api/place/textsearch/json?" + apiKey + "&pagetoken=" + nextPageToken;
                            object = getJSONObject(newUrlJSON);
                            nearbyPlacesJsonArray = Objects.requireNonNull(object).getJSONArray("results");
                            if (nearbyPlacesJsonArray != null) {
                                this.JSONArrayPlacesList.add(nearbyPlacesJsonArray);
                            }
                        }
                        Thread.sleep(3000);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
                convertJSONAndAddObjectsToList((ArrayList<JSONArray>) this.JSONArrayPlacesList);
                return JSONArrayPlacesList;
            }

            return null;
        }

        @Override
        protected void onPostExecute(List<JSONArray> response) {
            super.onPostExecute(response);

            if (response != null) {
//                progressBar.setVisibility(View.INVISIBLE);
                progressDialog.dismiss();
                nearSearchImageView.setBackground(getResources().getDrawable(R.drawable.rounded_gray_buttons));
                nearcbyPlacesSearchButton.setClickable(false);
//                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                Toast.makeText(TripPlanningActivity.this, R.string.success_message, Toast.LENGTH_SHORT).show();
            }
            else
                Toast.makeText(TripPlanningActivity.this, R.string.something_wrong_message, Toast.LENGTH_SHORT).show();
        }
    }


    private JSONObject getJSONObject(String s){
        HttpURLConnection connection = null;
        BufferedReader reader = null;

        try {
            URL url = new URL(s);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();


            InputStream stream = connection.getInputStream();

            reader = new BufferedReader(new InputStreamReader(stream));

            StringBuilder buffer = new StringBuilder();
            String line = "";

            while ((line = reader.readLine()) != null) {
                buffer.append(line).append("\n");
            }
            try {
                jsonObject = new JSONObject(buffer.toString());


                return jsonObject;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;

    }
}
