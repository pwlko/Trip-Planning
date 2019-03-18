package com.example.pbwi.planyourtrip.activieties.Tracks;

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


public class SavedTracksActivity extends BaseActivity {
    private List<SavedTrackEntry> tracksList = new ArrayList<>();


    private SavedTracksListAdapter tracksAdapter;
    private boolean isFirstDataChange = true;
    private ValueEventListener tracksListener;
    private DatabaseReference mPlacesReference;
    private ListView listView;

    private  String[] menuCharArray = null;

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase database;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = getLayoutInflater();
        LinearLayout container = findViewById(R.id.content_frame);
        inflater.inflate(R.layout.saved_tracks_list_layout, container);

        ActionBar actionBar = getSupportActionBar();
        Objects.requireNonNull(actionBar).setTitle(R.string.saved_tracks_actionBar);

        listView = findViewById(R.id.trips_ListView);
        listView.setClickable(true);
        firebaseAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        menuCharArray = new String[]{
                getString(R.string.change_name),
                getString(R.string.change_description),
                getString(R.string.delete)
        };

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        mPlacesReference = FirebaseDatabase.getInstance().getReference().child("UsersObjects").child("SavedTracks").child(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid());
        mPlacesReference.keepSynced(true);

         tracksAdapter = new SavedTracksListAdapter(SavedTracksActivity.this,
                0, tracksList);
        ListView listView = findViewById(R.id.trips_ListView);
        listView.setAdapter(tracksAdapter);

        initTracksListener();
        mPlacesReference.addValueEventListener(tracksListener);



        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

//                Toast.makeText(SavedPlacesActivity.this, placesList.get(position).getPlaceID(), Toast.LENGTH_LONG).show();
                dialog(tracksList.get(position).getTrackID(), tracksList.get(position).getName());
                return true;
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                Intent i = new Intent(view.getContext(), SavedTrackDetailsActivity.class);

                i.putExtra("name", tracksList.get(position).getName());
                i.putExtra("startTime", String.valueOf(tracksList.get(position).getTimeStart()));
                i.putExtra("description", tracksList.get(position).getDescription());
                i.putExtra("endTime", String.valueOf(tracksList.get(position).getTimeEnd()));
                i.putExtra("savedPointsNumber", String.valueOf(tracksList.get(position).getSavedPointsNumber()));

                HashMap<String, LocationEntry> locationsMap = tracksList.get(position).getLocations();

                bundle.putSerializable("locationsMap", locationsMap);

                i.putExtras(bundle);

                startActivity(i);

            }
        });

    }



    private void initTracksListener() {


        tracksListener = new ValueEventListener() {
            @SuppressLint("SimpleDateFormat")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                placesList.clear();
                if(isFirstDataChange) {

                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        SavedTrackEntry trackEntry = dataSnapshot1.getValue(SavedTrackEntry.class);
                        Objects.requireNonNull(trackEntry).setTrackID(Objects.requireNonNull(dataSnapshot1.getKey()));

                        tracksList.add(trackEntry);
                        tracksList.get(tracksList.size() - 1).setTrackID(dataSnapshot1.getKey());
                    }
                    isFirstDataChange = false;
                    Log.d("onDataChange",new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(System.currentTimeMillis()) + " : " + "Number of places: " + dataSnapshot.getChildrenCount());
                    if(tracksList.size()==0){
                        finish();
                        Toast.makeText(SavedTracksActivity.this, R.string.no_saved_tracks_alert, Toast.LENGTH_SHORT).show();
                    }else
                        tracksAdapter.notifyDataSetChanged();
                }

            }

            @SuppressLint("SimpleDateFormat")
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("TAG", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(System.currentTimeMillis()) + " : " + "loadPost:onCancelled", databaseError.toException());
                Toast.makeText(SavedTracksActivity.this, databaseError.toException().getMessage(), Toast.LENGTH_LONG).show();
            }
        };
        mPlacesReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(tracksAdapter != null)
                    tracksAdapter.notifyDataSetChanged();
            }
            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                if(tracksAdapter != null)
                    tracksAdapter.notifyDataSetChanged();
            }
            @SuppressLint("SimpleDateFormat")
            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                SavedTrackEntry savedTrackEntry = dataSnapshot.getValue(SavedTrackEntry.class);
                String key = dataSnapshot.getKey();
                for (int i = 0; i < tracksList.size(); i++) {
                    if (tracksList.get(i).getTrackID().equals(key)) {
                        tracksList.set(i, savedTrackEntry);
                        tracksList.get(i).setTrackID(Objects.requireNonNull(dataSnapshot.getKey()));
                        break;
                    }
                }
                tracksAdapter.notifyDataSetChanged();
                Toast.makeText(SavedTracksActivity.this, "Data changed", Toast.LENGTH_SHORT).show();
                Log.d("onChildChanged", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(System.currentTimeMillis()) + " : " + s + " " + "child key: " + dataSnapshot.getKey());
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

//        if(item.getTitle().toString().toLowerCase().contains(getString(R.string.delete)))
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

        AlertDialog.Builder alert = new AlertDialog.Builder(SavedTracksActivity.this);
        alert.setTitle(R.string.track_name);
        alert.setMessage(R.string.enter_new_name);
        // Create TextView
        final EditText input = new EditText(SavedTracksActivity.this);
        input.setMaxLines(2);
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
                    updateTrackName(id, name);
                    alertDialog.dismiss();
                } else {
                    input.setError(getString(R.string.empty_name_alert));
                }
            }
        });
    }

    public void createDescriptionChangingAlert(final String id) {

        AlertDialog.Builder alert = new AlertDialog.Builder(SavedTracksActivity.this);


        alert.setTitle(R.string.new_place_description_text);
        alert.setMessage(R.string.enter_new_description_text);
        // Create TextView
        final EditText input = new EditText(SavedTracksActivity.this);
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
                    updateTrackDescription(id, description);
                    alertDialog.dismiss();
                } else {
                    input.setError(getString(R.string.empty_description_alert));
                }
            }
        });

    }

    public void updateTrackName(final String id, String newName) {

        if (id != null) {
            DatabaseReference ref = database.getReference("UsersObjects/SavedTracks/" + Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid()).child(id);

            Map<String, Object> updates = new HashMap<>();

            updates.put("name", newName);


            ref.updateChildren(updates)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(SavedTracksActivity.this, R.string.name_update_success, Toast.LENGTH_SHORT).show();
                            Log.i("updateTrackName", String.valueOf(R.string.name_update_success));
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(SavedTracksActivity.this, R.string.name_updating_fail, Toast.LENGTH_SHORT).show();
                            Log.e("updateTrackName", String.valueOf(R.string.name_updating_fail));
                        }
                    });
        } else {
            Toast.makeText(SavedTracksActivity.this, R.string.something_wrong_message, Toast.LENGTH_SHORT).show();
            Log.i("updateTrackName", String.valueOf(R.string.something_wrong_message));
        }
    }

    public void updateTrackDescription(final String id, String newDescription) {

        if (id != null) {
        DatabaseReference ref = database.getReference("UsersObjects/SavedTracks/" + Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid()).child(id);

        Map<String, Object> updates = new HashMap<>();

        updates.put("description", newDescription);

        ref.updateChildren(updates)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(SavedTracksActivity.this, R.string.description_update_success, Toast.LENGTH_SHORT).show();
                        Log.i("updateTrackName", String.valueOf(R.string.description_update_success));

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SavedTracksActivity.this, R.string.description_update_fail, Toast.LENGTH_SHORT).show();
                        Log.e("updateTrackName", String.valueOf(R.string.description_update_fail));
                    }
                });

        } else {
            Toast.makeText(SavedTracksActivity.this, R.string.something_wrong_message, Toast.LENGTH_SHORT).show();
            Log.i("updateTrackName", String.valueOf(R.string.something_wrong_message));
        }

    }

    public void makeDeleteAlertDialog(final String id){

        if(id != null) {
            AlertDialog.Builder alert = new AlertDialog.Builder(SavedTracksActivity.this);

            alert.setTitle(R.string.delete_title_message);
            alert.setMessage(R.string.delete_track_message);

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
            Toast.makeText(SavedTracksActivity.this, getString(R.string.cant_delete_tracks_of) + "\"null\" id", Toast.LENGTH_SHORT).show();

    }

    public void deleteSavedTrack(final String id) {

        DatabaseReference ref = database.getReference("UsersObjects/SavedTracks/" + Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid());


        ref.child(id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(SavedTracksActivity.this, R.string.track_successfully_deleted, Toast.LENGTH_SHORT).show();
                    tracksAdapter.notifyDataSetChanged();
                }
                else
                    Toast.makeText(SavedTracksActivity.this, R.string.cant_delete_track, Toast.LENGTH_SHORT).show();
            }
        });


    }

    public void makeDeleteSellectedAlertDialog(){



            AlertDialog.Builder alert = new AlertDialog.Builder(SavedTracksActivity.this);

            alert.setTitle(R.string.delete_title_message);
            alert.setMessage(R.string.delete_track_question);

            alert.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    deleteSelectedPlaces();
                    tracksAdapter.notifyDataSetChanged();
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

        for (SavedTrackEntry track : tracksList) {
            if (track.isChecked()) {
                deleteSavedTrack(track.getTrackID());
            }
        }

    }
}