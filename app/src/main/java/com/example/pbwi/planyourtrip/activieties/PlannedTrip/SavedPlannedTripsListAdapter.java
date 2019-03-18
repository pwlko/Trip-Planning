package com.example.pbwi.planyourtrip.activieties.PlannedTrip;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.pbwi.planyourtrip.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * Created by komputer-prywatny on 16.01.2018.
 */

public class SavedPlannedTripsListAdapter extends ArrayAdapter<SavedPlannedTripEntry>  implements GoogleApiClient.OnConnectionFailedListener {
    private Context context;
    private static final int GOOGLE_API_CLIENT_ID = 0;
    public SavedPlannedTripsListAdapter(Context context, int resource, List<SavedPlannedTripEntry> tripsList) {
        super(context, resource, tripsList);
        this.context = context;
    }

    @SuppressLint("DefaultLocale")
    @NonNull
    @Override
    public View getView(final int position, View convertView, @NonNull ViewGroup parent) {

        View listItemView = convertView;

        final SavedPlannedTripEntry object = getItem(position);

        final CheckBox checkBox;
        TextView nameTextView;
        TextView targetTextView;
        TextView creationDateTextView;
        TextView numberOfDaysTextView;
        TextView placesToVisitTextView;


        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.saved_trip_item_layout, parent, false);

            nameTextView = listItemView.findViewById(R.id.trip_name_value_TextView);
            targetTextView = listItemView.findViewById(R.id.trip_target_location_value_TextView);
            checkBox = listItemView.findViewById(R.id.trip_selection_CheckBox);
            creationDateTextView = listItemView.findViewById(R.id.trip_creation_date_value_TextView);
            numberOfDaysTextView = listItemView.findViewById(R.id.trip_number_of_days_value_TextView);
            placesToVisitTextView = listItemView.findViewById(R.id.trip_places_to_visit_value_TextView);


            listItemView.setTag(new TripsViewHolder(checkBox, nameTextView, targetTextView, creationDateTextView, numberOfDaysTextView, placesToVisitTextView));

            checkBox.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    CheckBox cb = (CheckBox) v;
                    SavedPlannedTripEntry track = (SavedPlannedTripEntry) cb.getTag();
                    track.setChecked(cb.isChecked());
                }
            });
        } else {
            TripsViewHolder viewHolder = (TripsViewHolder) listItemView.getTag();

            nameTextView = viewHolder.getNameTextView();
            targetTextView = viewHolder.getTargetTextView();
            creationDateTextView = viewHolder.getCreationDateTextView();
            numberOfDaysTextView = viewHolder.getNumberOfDaysTextView();
            placesToVisitTextView = viewHolder.getPlacesToVisitTextView();
            checkBox = viewHolder.getCheckBox();
        }

        Date creationDate = new Date(Objects.requireNonNull(object).getCreationDate());
//        Date startEndValue = new Date(Objects.requireNonNull(object).getTimeEnd());
        @SuppressLint("SimpleDateFormat") String formattedStartDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(creationDate);
//        @SuppressLint("SimpleDateFormat") String formattedEndDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(startEndValue);



        checkBox.setTag(object);
        checkBox.setChecked(object.isChecked());
        nameTextView.setText(object.getName());
        targetTextView.setText(object.getTargetAddress());
        creationDateTextView.setText(formattedStartDate);
        numberOfDaysTextView.setText(String.valueOf(object.getNumberOfDays()));
        placesToVisitTextView.setText(String.valueOf(object.getNumberOfPlacesToVistit()));


        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Objects.requireNonNull(getItem(position)).toggleChecked();
            }
        });

        return listItemView;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    static class TripsViewHolder {
        private CheckBox checkBox;
        private TextView nameTextView;
        private TextView targetTextView;
        private TextView creationDateTextView;
        private TextView numberOfDaysTextView;
        private TextView placesToVisitTextView;

        public TripsViewHolder(CheckBox checkBox, TextView nameTextView, TextView targetTextView, TextView creationDateTextView, TextView numberOfDaysTextView, TextView placesToVisitTextView) {
            this.checkBox = checkBox;
            this.nameTextView = nameTextView;
            this.targetTextView = targetTextView;
            this.creationDateTextView = creationDateTextView;
            this.numberOfDaysTextView = numberOfDaysTextView;
            this.placesToVisitTextView = placesToVisitTextView;
        }

        public CheckBox getCheckBox() {
            return checkBox;
        }

        public void setCheckBox(CheckBox checkBox) {
            this.checkBox = checkBox;
        }

        public TextView getNameTextView() {
            return nameTextView;
        }

        public void setNameTextView(TextView nameTextView) {
            this.nameTextView = nameTextView;
        }

        public TextView getTargetTextView() {
            return targetTextView;
        }

        public void setTargetTextView(TextView targetTextView) {
            this.targetTextView = targetTextView;
        }

        public TextView getCreationDateTextView() {
            return creationDateTextView;
        }

        public void setCreationDateTextView(TextView creationDateTextView) {
            this.creationDateTextView = creationDateTextView;
        }

        public TextView getNumberOfDaysTextView() {
            return numberOfDaysTextView;
        }

        public void setNumberOfDaysTextView(TextView numberOfDaysTextView) {
            this.numberOfDaysTextView = numberOfDaysTextView;
        }

        public TextView getPlacesToVisitTextView() {
            return placesToVisitTextView;
        }

        public void setPlacesToVisitTextView(TextView placesToVisitTextView) {
            this.placesToVisitTextView = placesToVisitTextView;
        }
    }
}