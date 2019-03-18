package com.example.pbwi.planyourtrip.activieties.Tracks;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pbwi.planyourtrip.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * Created by komputer-prywatny on 16.01.2018.
 */

public class SavedTracksListAdapter extends ArrayAdapter<SavedTrackEntry> {

    public SavedTracksListAdapter(Context context, int resource, List<SavedTrackEntry> tracksList) {
        super(context, resource, tracksList);
    }

    @SuppressLint("DefaultLocale")
    @NonNull
    @Override
    public View getView(final int position, View convertView, @NonNull ViewGroup parent) {

        final SavedTrackEntry object = getItem(position);

        final CheckBox checkBox;
        TextView nameTextView;
        TextView descriptionTextView;
        TextView timeStartTextView;
        TextView timeEndTextView;
        TextView savedPointsTextView;


        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.saved_track_item_layout, parent, false);

            nameTextView = convertView.findViewById(R.id.track_name_value_TextView);
            descriptionTextView = convertView.findViewById(R.id.track_description_value_TextView);
            checkBox = convertView.findViewById(R.id.track_selection_CheckBox);
            timeStartTextView = convertView.findViewById(R.id.start_time_value_TextView);
            timeEndTextView = convertView.findViewById(R.id.end_time_value_TextView);
            savedPointsTextView = convertView.findViewById(R.id.track_saved_points_value_TextView);


            convertView.setTag(new TracksViewHolder(checkBox, nameTextView, descriptionTextView, timeStartTextView, timeEndTextView, savedPointsTextView));

            checkBox.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    CheckBox cb = (CheckBox) v;
                    SavedTrackEntry track = (SavedTrackEntry) cb.getTag();
                    track.setChecked(cb.isChecked());
                }
            });
        } else {
            TracksViewHolder viewHolder = (TracksViewHolder) convertView.getTag();

            nameTextView = viewHolder.getNameTextView();
            descriptionTextView = viewHolder.getDescriptionTextView();
            timeStartTextView = viewHolder.getTimeStartTextView();
            timeEndTextView = viewHolder.getTimeEndTextView();
            savedPointsTextView = viewHolder.getSavedPointsTextView();
            checkBox = viewHolder.getCheckBox();
        }

        Date startTimeValue = new Date(Objects.requireNonNull(object).getTimeStart());
        Date startEndValue = new Date(Objects.requireNonNull(object).getTimeEnd());
        @SuppressLint("SimpleDateFormat") String formattedStartDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(startTimeValue);
        @SuppressLint("SimpleDateFormat") String formattedEndDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(startEndValue);

        checkBox.setTag(object);
        checkBox.setChecked(object.isChecked());
        nameTextView.setText(object.getName());
        if(object.getDescription() == null || object.getDescription().equals(""))
            descriptionTextView.setText(R.string.no_description);
        else
            descriptionTextView.setText(object.getDescription());
        timeStartTextView.setText(formattedStartDate);
        timeEndTextView.setText(formattedEndDate);
        savedPointsTextView.setText(String.valueOf(object.getSavedPointsNumber()));


        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Objects.requireNonNull(getItem(position)).toggleChecked();
            }
        });

        return convertView;
    }



    static class TracksViewHolder {
        private CheckBox checkBox;
        private TextView nameTextView;
        private TextView descriptionTextView;
        private TextView timeStartTextView;
        private TextView timeEndTextView;
        private TextView savedPointsTextView;

        public TracksViewHolder(CheckBox checkBox, TextView nameTextView, TextView descriptionTextView, TextView timeStartTextView, TextView timeEndTextView, TextView savedPointsTextView) {
            this.checkBox = checkBox;
            this.nameTextView = nameTextView;
            this.descriptionTextView = descriptionTextView;
            this.timeStartTextView = timeStartTextView;
            this.timeEndTextView = timeEndTextView;
            this.savedPointsTextView = savedPointsTextView;
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

        public TextView getDescriptionTextView() {
            return descriptionTextView;
        }

        public void setDescriptionTextView(TextView descriptionTextView) {
            this.descriptionTextView = descriptionTextView;
        }

        public TextView getTimeStartTextView() {
            return timeStartTextView;
        }

        public void setTimeStartTextView(TextView timeStartTextView) {
            this.timeStartTextView = timeStartTextView;
        }

        public TextView getTimeEndTextView() {
            return timeEndTextView;
        }

        public void setTimeEndTextView(TextView timeEndTextView) {
            this.timeEndTextView = timeEndTextView;
        }

        public TextView getSavedPointsTextView() {
            return savedPointsTextView;
        }

        public void setSavedPointsTextView(TextView savedPointsTextView) {
            this.savedPointsTextView = savedPointsTextView;
        }
    }
}