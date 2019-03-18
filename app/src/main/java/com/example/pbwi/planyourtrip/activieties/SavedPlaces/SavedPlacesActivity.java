package com.example.pbwi.planyourtrip.activieties.SavedPlaces;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
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
import android.widget.Toast;

import com.example.pbwi.planyourtrip.R;
import com.example.pbwi.planyourtrip.activieties.BaseActivity;
import com.example.pbwi.planyourtrip.activieties.Tracks.SavedTracksActivity;
import com.example.pbwi.planyourtrip.activieties.UserActivity;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


public class SavedPlacesActivity extends BaseActivity {
    private List<SavedPlaceEntry> placesList = new ArrayList<>();


    private SavedPlacesListAdapter placesAdapter;
    private boolean isFirstDataChange = true;
    private ValueEventListener placesListener;
    private DatabaseReference mPlacesReference, userSavedPlacesReference;
    private Integer ready = 0;
    private ListView listView;

    private String[] menuCharArray = null;

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase database;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = getLayoutInflater();
        LinearLayout container = findViewById(R.id.content_frame);
        inflater.inflate(R.layout.saved_places_list_layout, container);

        ActionBar actionBar = getSupportActionBar();
        Objects.requireNonNull(actionBar).setTitle(R.string.saved_places);

        menuCharArray = new String[]{
                getString(R.string.change_name),
                getString(R.string.change_description),
                getString(R.string.delete)
        };

        listView = findViewById(R.id.saved_places_ListView);
        listView.setClickable(true);
        firebaseAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        mPlacesReference = FirebaseDatabase.getInstance().getReference().child("UsersObjects").child("SavedPlaces").child(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid());
        mPlacesReference.keepSynced(true);

        placesAdapter = new SavedPlacesListAdapter(SavedPlacesActivity.this, 0, placesList);
        listView.setAdapter(placesAdapter);

        initPlacesListener();
        mPlacesReference.addValueEventListener(placesListener);



        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

//                Toast.makeText(SavedPlacesActivity.this, placesList.get(position).getPlaceID(), Toast.LENGTH_LONG).show();
                dialog(placesList.get(position).getPlaceID(), placesList.get(position).getName());
                return true;
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent i = new Intent(view.getContext(), SavedPlaceDetailsActivity.class);

                i.putExtra("name", placesList.get(position).getName());
                i.putExtra("date", String.valueOf(placesList.get(position).getDate()));
                i.putExtra("description", placesList.get(position).getDescription());
                i.putExtra("lat", String.valueOf(placesList.get(position).getLat()));
                i.putExtra("lng", String.valueOf(placesList.get(position).getLng()));
                startActivity(i);

            }
        });

    }

    private void initPlacesListener() {


        placesListener = new ValueEventListener() {
            @SuppressLint("SimpleDateFormat")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                placesList.clear();
                if(isFirstDataChange) {
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        SavedPlaceEntry savedPlaceEntry = dataSnapshot1.getValue(SavedPlaceEntry.class);
                        Objects.requireNonNull(savedPlaceEntry).setTrackID(Objects.requireNonNull(dataSnapshot1.getKey()));

                        placesList.add(savedPlaceEntry);
                        placesList.get(placesList.size() - 1).setPlaceID(dataSnapshot1.getKey());
                    }
                    isFirstDataChange = false;
                    int childrenCount = (int) dataSnapshot.getChildrenCount();
                    Log.d("onDataChange",
                            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
                                    .format(System.currentTimeMillis()) + " : "
                                    + "Number of saved places: " + childrenCount);
                    if(placesList.size()==0){
                        finish();
                        Toast.makeText(SavedPlacesActivity.this, R.string.no_saved_places_alert, Toast.LENGTH_SHORT).show();
                    }
                    else
                        placesAdapter.notifyDataSetChanged();
                }

            }

            @SuppressLint("SimpleDateFormat")
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("TAG", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(System.currentTimeMillis()) + " : " + "loadPost:onCancelled", databaseError.toException());
                Toast.makeText(SavedPlacesActivity.this, databaseError.toException().getMessage(), Toast.LENGTH_LONG).show();
            }
        };
        mPlacesReference.addChildEventListener(new ChildEventListener() {
            @SuppressLint("SimpleDateFormat")
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                placesAdapter.notifyDataSetChanged();
//                Toast.makeText(SavedPlacesActivity.this, "Data changed", Toast.LENGTH_SHORT).show();
                Log.d("onChildAdded",
                        new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
                                .format(System.currentTimeMillis())
                                + " : " + "Saved place child key: " + dataSnapshot.getKey());
            }

            @SuppressLint("SimpleDateFormat")
            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                SavedPlaceEntry savedPlaceEntry = dataSnapshot.getValue(SavedPlaceEntry.class);
                String key = dataSnapshot.getKey();
                for (int i = 0; i < placesList.size(); i++) {
                    if (placesList.get(i).getPlaceID().equals(key)) {
                        placesList.set(i, savedPlaceEntry);
//                        Log.d("onChildChanged", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(System.currentTimeMillis()) + " : " + placesList.get(i).getPlaceID() + " " + placesList.get(i).getName()  );
                        placesList.get(i).setPlaceID(Objects.requireNonNull(dataSnapshot.getKey()));
//                        Log.d("setAttractionID", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(System.currentTimeMillis()) + " : " + placesList.get(i).getPlaceID() + " " + placesList.get(i).getName() );
                        break;
                    }
                }
                placesAdapter.notifyDataSetChanged();
                Toast.makeText(SavedPlacesActivity.this, "Data changed", Toast.LENGTH_SHORT).show();
                Log.d("onChildChanged", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(System.currentTimeMillis()) + " : " + s + " " + "child key: " + dataSnapshot.getKey());
            }

            @SuppressLint("SimpleDateFormat")
            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

//                UserSavedPlaceEntry userSavedPlaceEntry = dataSnapshot.getValue(UserSavedPlaceEntry.class);
                String key = dataSnapshot.getKey();
                for (int i = 0; i < placesList.size(); i++) {
                    if (placesList.get(i).getPlaceID().equals(key)) {
                        placesList.remove(i);
                        break;
                    }
                }
                placesAdapter.notifyDataSetChanged();
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

    @Override
    public void onBackPressed() {
        Intent backIntent = new Intent(getApplicationContext(), UserActivity.class);
        backIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_TASK_ON_HOME);
        startActivity(backIntent);

        this.finish();
    }

    public void dialog(final String id, final String name) {


        AlertDialog.Builder alterMenuBuilder = new AlertDialog.Builder(this);

        alterMenuBuilder.setTitle(name);
        alterMenuBuilder.setSingleChoiceItems(menuCharArray, -1, new DialogInterface
                .OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {

                switch (item){
                    case 0:
                        createNameChangingDialog(id);
                        break;

                    case 1:
                        createDescriptionChangingAlert(id);
                        break;

                    case 2:
                        makeDeleteAlertDialog(id);
                        break;
                }

                dialog.dismiss();
            }
        });

        alterMenuBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
            }
        });

        AlertDialog alert = alterMenuBuilder.create();
        alert.show();

    }


    public void createNameChangingDialog(final String id) {

        AlertDialog.Builder alert = new AlertDialog.Builder(SavedPlacesActivity.this);
        alert.setTitle(R.string.new_place_name);
        alert.setMessage(R.string.enter_new_name);
        // Create TextView
        final EditText input = new EditText(SavedPlacesActivity.this);
        input.setMaxLines(1);
        alert.setView(input);


        alert.setPositiveButton(R.string.ok, null);

        alert.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
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
                    updatePlaceName(id, name);
                    alertDialog.dismiss();
                } else {
                    input.setError(getString(R.string.empty_name_alert));
                }
            }
        });
    }

    public void createDescriptionChangingAlert(final String id) {

        AlertDialog.Builder alert = new AlertDialog.Builder(SavedPlacesActivity.this);


        alert.setTitle(R.string.new_place_description_text);
        alert.setMessage(R.string.enter_new_description_text);
        // Create TextView
        final EditText input = new EditText(SavedPlacesActivity.this);
        input.setMaxLines(10);
        alert.setView(input);

        alert.setPositiveButton(R.string.ok, null);

        alert.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();
            }
        });
        final AlertDialog alertDialog = alert.create();
        alertDialog.show();

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String description = input.getText().toString();
                if (!description.isEmpty()) {
                    updatePlaceDescription(id, description);
                    alertDialog.dismiss();
                } else {
                    input.setError(getString(R.string.empty_description_alert));
                }
            }
        });

    }

    public void updatePlaceName(final String id, String newName) {

        if (id != null) {
            DatabaseReference ref = database.getReference("UsersObjects/SavedPlaces/" + Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid()).child(id);

            Map<String, Object> updates = new HashMap<>();

            updates.put("name", newName);


            ref.updateChildren(updates)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(SavedPlacesActivity.this, R.string.name_update_success, Toast.LENGTH_SHORT).show();
//                            SavedPlacesActivity.this.recreate();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(SavedPlacesActivity.this, R.string.name_updating_fail, Toast.LENGTH_SHORT).show();
                        }
                    });
        } else
            Toast.makeText(SavedPlacesActivity.this, R.string.something_wrong_message, Toast.LENGTH_SHORT).show();
    }

    public void updatePlaceDescription(final String id, String newDescription) {


        DatabaseReference ref = database.getReference("UsersObjects/SavedPlaces/" + Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid()).child(id);

        Map<String, Object> updates = new HashMap<>();

        updates.put("description", newDescription);

        ref.updateChildren(updates)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(SavedPlacesActivity.this, R.string.description_update_success, Toast.LENGTH_SHORT).show();
//                        SavedPlacesActivity.this.recreate();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SavedPlacesActivity.this, R.string.description_update_fail, Toast.LENGTH_SHORT).show();
                    }
                });

    }

    public void makeDeleteAlertDialog(final String id){

        if(id != null) {
            AlertDialog.Builder alert = new AlertDialog.Builder(SavedPlacesActivity.this);

            alert.setTitle(R.string.delete_title_message);
            alert.setMessage(R.string.delete_place_message);

            alert.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    deleteSavedPlace(id);
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
            Toast.makeText(SavedPlacesActivity.this, getString(R.string.cant_delete_place_of) + "\"null\" id", Toast.LENGTH_SHORT).show();

    }

    public void deleteSavedPlace(final String id) {

        DatabaseReference ref = database.getReference("UsersObjects/SavedPlaces/" + Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid());


        ref.child(id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(SavedPlacesActivity.this, R.string.place_successfully_deleted, Toast.LENGTH_SHORT).show();
                    placesAdapter.notifyDataSetChanged();
                }
                else
                    Toast.makeText(SavedPlacesActivity.this, R.string.cant_delete_place, Toast.LENGTH_SHORT).show();
            }
        });


    }

    public void makeDeleteSellectedAlertDialog(){



            AlertDialog.Builder alert = new AlertDialog.Builder(SavedPlacesActivity.this);

            alert.setTitle(R.string.delete_title_message);
            alert.setMessage(R.string.delete_selected_place_question);

            alert.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    deleteSelectedPlaces();
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

        for (SavedPlaceEntry place : placesList) {
            if (place.isChecked()) {
                deleteSavedPlace(place.getPlaceID());
            }
        }
    }
}