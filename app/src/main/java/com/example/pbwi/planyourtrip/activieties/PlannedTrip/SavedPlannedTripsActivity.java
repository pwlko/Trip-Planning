package com.example.pbwi.planyourtrip.activieties.PlannedTrip;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.pbwi.planyourtrip.R;
import com.example.pbwi.planyourtrip.activieties.BaseActivity;
import com.example.pbwi.planyourtrip.activieties.Tracks.SavedTrackEntry;
import com.example.pbwi.planyourtrip.activieties.Tracks.SavedTracksActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class SavedPlannedTripsActivity extends BaseActivity implements GoogleApiClient.OnConnectionFailedListener {
    private static final int GOOGLE_API_CLIENT_ID = 0;
    private List<SavedPlannedTripEntry> tripsList = new ArrayList<>();
    private Map<String,Integer> mapOfIndexes = new HashMap<>();

    private SavedPlannedTripsListAdapter tripsAdapter;
    private boolean isFirstDataChange = true;
    private ValueEventListener tripsListener;
    private DatabaseReference mPlacesReference;
    private ListView listView;

    private GoogleApiClient mGoogleApiClient;
    private ProgressBar progressBar;


    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase database;

    private String[] menuCharArray = null;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = getLayoutInflater();
        LinearLayout container = findViewById(R.id.content_frame);
        inflater.inflate(R.layout.saved_planned_trips_list_layout, container);

        ActionBar actionBar = getSupportActionBar();
        Objects.requireNonNull(actionBar).setTitle(R.string.saved_trips);

        progressBar = findViewById(R.id.planned_trip_ProgressBar);

        listView = findViewById(R.id.trips_ListView);
        listView.setClickable(true);
        firebaseAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        menuCharArray = new String[]{
                getString(R.string.change_name),
                getString(R.string.delete)
        };

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        mPlacesReference = FirebaseDatabase.getInstance().getReference().child("UsersObjects")
                .child("SavedPlannedTrips").child(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid());
        mPlacesReference.keepSynced(true);

        tripsAdapter = new SavedPlannedTripsListAdapter(SavedPlannedTripsActivity.this, 0, tripsList);
        listView.setAdapter(tripsAdapter);


        initTracksListener();
        mPlacesReference.addListenerForSingleValueEvent(tripsListener);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, GOOGLE_API_CLIENT_ID /* clientId */, this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

//                Toast.makeText(SavedPlacesActivity.this, placesList.get(position).getPlaceID(), Toast.LENGTH_LONG).show();
                dialog(tripsList.get(position).getTripID(), tripsList.get(position).getName());
                return true;
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                final View clickedView = view;
                progressBar.setMax(100);
                new Thread(new Runnable() {


                    public void run() {

                        int progressStatus = 0;
                        Handler handler = new Handler(Looper.getMainLooper());
                        while (progressStatus < 100) {
                            progressStatus += 5;

                            // Update the progress bar and display the
                            // current value in the text view
                            final int finalProgressStatus = progressStatus;
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    progressBar.setProgress(finalProgressStatus);
                                }
                            });


                            try {
                                // Sleep for 200 milliseconds.
                                // Just to display the progress slowly
                                Thread.sleep(50);

                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }

                        startActivityFromMainThread(clickedView, position);
                    }

                }).start();


            }
        });

    }

    public void startActivityFromMainThread(final View view, final int position){

        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                Bundle bundle = new Bundle();
                Intent intent = new Intent(view.getContext(), SavedPlannedTripDetailsActivity.class);
                intent.putExtra("name", tripsList.get(position).getName());
                intent.putExtra("targetAddress", String.valueOf(tripsList.get(position).getTargetAddress()));
                intent.putExtra("targetLocation", String.valueOf(tripsList.get(position).getTargetLocation()));
                intent.putExtra("creationDay", String.valueOf(tripsList.get(position).getCreationDate()));
                intent.putExtra("numberOfDays", String.valueOf(tripsList.get(position).getNumberOfDays()));
                intent.putExtra("placesToVisitNumber", String.valueOf(tripsList.get(position).getNumberOfPlacesToVistit()));

                Map<String,HashMap<String, String>> tripMap = tripsList.get(position).getTrip();

                bundle.putSerializable("tripMap", (Serializable) tripMap);

                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    private void initTracksListener() {

             tripsListener = new ValueEventListener() {
            @SuppressLint("SimpleDateFormat")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    int i = 0;
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        SavedPlannedTripEntry tripEntry = dataSnapshot1.getValue(SavedPlannedTripEntry.class);
                        Objects.requireNonNull(tripEntry).setTripHelpID(Objects.requireNonNull(dataSnapshot1.getKey()));
                        mapOfIndexes.put(tripEntry.getTargetLocation(),i);
                        i++;
                        tripsList.add(tripEntry);
                        tripsList.get(tripsList.size() - 1).setTripHelpID(dataSnapshot1.getKey());
                    }
                    isFirstDataChange = false;
                    Log.d("onDataChange",new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
                            .format(System.currentTimeMillis()) + " : "
                            + "Number of places: " + dataSnapshot.getChildrenCount());
                    if(tripsList.size()>0) {
                        getName();
                        tripsAdapter.notifyDataSetChanged();
                    }
                    else {
                        finish();
                        Toast.makeText(SavedPlannedTripsActivity.this, R.string.no_saved_trips_alert, Toast.LENGTH_SHORT).show();
                    }



            }

            @SuppressLint("SimpleDateFormat")
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("TAG", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(System.currentTimeMillis()) + " : " + "loadPost:onCancelled", databaseError.toException());
                Toast.makeText(SavedPlannedTripsActivity.this, databaseError.toException().getMessage(), Toast.LENGTH_LONG).show();
            }
        };
        mPlacesReference.addChildEventListener(new ChildEventListener() {
            @SuppressLint("SimpleDateFormat")
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                tripsAdapter.notifyDataSetChanged();
//                Toast.makeText(SavedPlacesActivity.this, "Data changed", Toast.LENGTH_SHORT).show();
                Log.d("onChildAdded", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(System.currentTimeMillis()) + " : " + "child key: " + dataSnapshot.getKey());
            }

            @SuppressLint("SimpleDateFormat")
            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                SavedPlannedTripEntry plannedTripEntry = dataSnapshot.getValue(SavedPlannedTripEntry.class);
                String key = dataSnapshot.getKey();
                for (int i = 0; i < tripsList.size(); i++) {
                    if (tripsList.get(i).getTripHelpID().equals(key)) {
                        tripsList.set(i, plannedTripEntry);
//                        Log.d("onChildChanged", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(System.currentTimeMillis()) + " : " + placesList.get(i).getPlaceID() + " " + placesList.get(i).getName()  );
                        tripsList.get(i).setTripHelpID(Objects.requireNonNull(dataSnapshot.getKey()));
//                        Log.d("setAttractionID", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(System.currentTimeMillis()) + " : " + placesList.get(i).getPlaceID() + " " + placesList.get(i).getName() );
                        break;
                    }
                }
                tripsAdapter.notifyDataSetChanged();
                Toast.makeText(SavedPlannedTripsActivity.this, "Data changed", Toast.LENGTH_SHORT).show();
                Log.d("onChildChanged", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(System.currentTimeMillis()) + " : " + s + " " + "child key: " + dataSnapshot.getKey());
            }

            @SuppressLint("SimpleDateFormat")
            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                tripsAdapter.notifyDataSetChanged();
//                Toast.makeText(SavedPlacesActivity.this, "Data changed", Toast.LENGTH_SHORT).show();
                Log.d("onChildRemoved", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(System.currentTimeMillis()) + " : " + "child key: " + dataSnapshot.getKey());
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.delete_snigle_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        makeDeleteSellectedAlertDialog();

        return super.onOptionsItemSelected(item);
    }

    public void getName(){

        String[] table = new String[tripsList.size()];

        for(int i = 0; i < tripsList.size(); i++) {
            String id = tripsList.get(i).getTargetLocation();
            table[i] = id;
            Log.i("id is", "" + id);
        }


            Places.GeoDataApi.getPlaceById(mGoogleApiClient, table)
                    .setResultCallback(new ResultCallback<PlaceBuffer>() {

                        @Override
                        public void onResult(@NonNull PlaceBuffer places) {
                            if (places.getStatus().isSuccess()) {
                                final Place myPlace = places.get(0);
                                for(int i = 0; i < places.getCount(); i++){
                                    int index = mapOfIndexes.get(places.get(i).getId());
                                    tripsList.get(index).setTargetAddress(Objects.requireNonNull(places.get(i).getAddress()).toString());
                                    tripsAdapter.notifyDataSetChanged();
                                }
                                Log.v("Places is", "" + places.getCount());
//                                String address = myPlace.getAddress().toString();
//                                Log.v("Address is", "" + address);


                            } else {
                                Toast.makeText(SavedPlannedTripsActivity.this, "Can't get place name", Toast.LENGTH_SHORT).show();
                            }
                            places.release();
                        }
                    });

    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(SavedPlannedTripsActivity.this,"Can't get place name", Toast.LENGTH_SHORT).show();
    }

    public void dialog(final String id, final String name) {


        AlertDialog.Builder alterMenuBuilder = new AlertDialog.Builder(this);

        alterMenuBuilder.setTitle(name);
        alterMenuBuilder.setSingleChoiceItems(menuCharArray, -1, new DialogInterface
                .OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {

                switch (item) {
                    case 0:
                        createNameChangingDialog(id);
                        break;

                    case 1:
                        makeDeleteAlertDialog(id);
                        break;
                }
                dialog.dismiss();
            }
        });

        alterMenuBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
            }
        });

        AlertDialog alert = alterMenuBuilder.create();
        alert.show();

    }

    public void createNameChangingDialog(final String id) {

        AlertDialog.Builder alert = new AlertDialog.Builder(SavedPlannedTripsActivity.this);
        alert.setTitle(R.string.name_of_trip);
        alert.setMessage(R.string.enter_new_name);
        // Create TextView
        final EditText input = new EditText(SavedPlannedTripsActivity.this);
        input.setMaxLines(2);
        alert.setView(input);


        alert.setPositiveButton("Ok", null);

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
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
                    updateTripName(id, name);
                    alertDialog.dismiss();
                } else {
                    input.setError("Name can't be empty");
                }
            }
        });
    }

    public void updateTripName(final String id, String newName) {

        if (id != null) {
            DatabaseReference ref = database.getReference("UsersObjects/SavedPlannedTrips/" + Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid()).child(id);

            Map<String, Object> updates = new HashMap<>();

            updates.put("name", newName);


            ref.updateChildren(updates)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(SavedPlannedTripsActivity.this, R.string.name_update_success, Toast.LENGTH_SHORT).show();
                            Log.i("updateTripName", String.valueOf(R.string.name_update_success));
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(SavedPlannedTripsActivity.this, R.string.name_updating_fail, Toast.LENGTH_SHORT).show();
                            Log.e("updateTripName", String.valueOf(R.string.name_updating_fail));
                        }
                    });
        } else {
            Toast.makeText(SavedPlannedTripsActivity.this, R.string.something_wrong_message, Toast.LENGTH_SHORT).show();
            Log.i("updateTripName", String.valueOf(R.string.something_wrong_message));
        }
    }


    public void makeDeleteAlertDialog(final String id){

        if(id != null) {
            AlertDialog.Builder alert = new AlertDialog.Builder(SavedPlannedTripsActivity.this);

            alert.setTitle(R.string.delete_title_message);
            alert.setMessage(R.string.delete_trip_message);

            alert.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    deleteSavedTrack(id);
                }
            });

            alert.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    dialog.dismiss();
                }
            });
            final AlertDialog alertDialog = alert.create();
            alertDialog.show();
        }
        else
            Toast.makeText(SavedPlannedTripsActivity.this, getString(R.string.cant_delete_trip_of) + " \"null\" id", Toast.LENGTH_SHORT).show();

    }

    public void deleteSavedTrack(final String id) {

        DatabaseReference ref = database.getReference("UsersObjects/SavedPlannedTrips/" + Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid());


        ref.child(id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(SavedPlannedTripsActivity.this, R.string.trip_successfully_deleted, Toast.LENGTH_SHORT).show();
                    tripsAdapter.notifyDataSetChanged();
                }
                else
                    Toast.makeText(SavedPlannedTripsActivity.this, R.string.cant_delete_trip, Toast.LENGTH_SHORT).show();
            }
        });


    }

    public void makeDeleteSellectedAlertDialog(){



        AlertDialog.Builder alert = new AlertDialog.Builder(SavedPlannedTripsActivity.this);

        alert.setTitle(R.string.delete_title_message);
        alert.setMessage(R.string.delete_track_question);

        alert.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                deleteSelectedPlaces();
                tripsAdapter.notifyDataSetChanged();
            }
        });

        alert.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();

            }
        });
        final AlertDialog alertDialog = alert.create();
        alertDialog.show();

    }

    public void deleteSelectedPlaces() {

        for (SavedPlannedTripEntry track : tripsList) {
            if (track.isChecked()) {
                deleteSavedTrack(track.getTripID());
            }
        }

    }
}
