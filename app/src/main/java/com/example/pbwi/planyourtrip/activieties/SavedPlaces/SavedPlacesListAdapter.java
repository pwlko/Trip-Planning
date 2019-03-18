package com.example.pbwi.planyourtrip.activieties.SavedPlaces;

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

public class SavedPlacesListAdapter extends ArrayAdapter<SavedPlaceEntry> {

    public SavedPlacesListAdapter(Context context, int resource, List<SavedPlaceEntry> places) {
        super(context, resource, places);
    }

    @SuppressLint("DefaultLocale")
    @NonNull
    @Override
    public View getView(final int position, View convertView, @NonNull ViewGroup parent) {

        View listItemView = convertView;

        final SavedPlaceEntry object = getItem(position);

        final CheckBox checkBox;
        TextView name;
        TextView description;
        TextView lx;
        TextView ly;
        TextView date;


        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.saved_place_item_layout, parent, false);

            name = listItemView.findViewById(R.id.placeNameValueTextView);
            description = listItemView.findViewById(R.id.placeDescriptionValueTextView);
            lx = listItemView.findViewById(R.id.latPlaceValueTextView);
            ly = listItemView.findViewById(R.id.lngPlaceValueTextView);
            date = listItemView.findViewById(R.id.dateValueTextView);
            checkBox = listItemView.findViewById(R.id.place_selection_CheckBox);

            listItemView.setTag(new PlaceViewHolder(checkBox, name, description, lx, ly, date));

            checkBox.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    CheckBox cb = (CheckBox) v;
                    SavedPlaceEntry place = (SavedPlaceEntry) cb.getTag();
                    place.setChecked(cb.isChecked());
                }
            });
        } else {
            PlaceViewHolder viewHolder = (PlaceViewHolder) listItemView.getTag();

            name = viewHolder.getName();
            description = viewHolder.getDescription();
            lx = viewHolder.getLx();
            ly = viewHolder.getLy();
            date = viewHolder.getDate();
            checkBox = viewHolder.getCheckBox();
        }

        Date dateValue = new Date(Objects.requireNonNull(object).getDate());
        @SuppressLint("SimpleDateFormat") String formattedDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(dateValue);

        checkBox.setTag(object);
        checkBox.setChecked(object.isChecked());
        name.setText(object.getName());
        if(object.getDescription() == null || object.getDescription().equals(""))
            description.setText(R.string.no_description);
        else
            description.setText(object.getDescription());
        lx.setText(String.format("%.2f", object.getLat()));
        ly.setText(String.format("%.2f", object.getLng()));
        date.setText(formattedDate);

        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Objects.requireNonNull(getItem(position)).toggleChecked();
            }
        });

        return listItemView;
    }


    static class PlaceViewHolder {
        private CheckBox checkBox;
        private TextView name;
        private TextView description;
        private TextView lx;
        private TextView ly;
        private TextView date;


        public PlaceViewHolder(CheckBox checkBox, TextView name, TextView description, TextView lx, TextView ly, TextView date) {
            this.checkBox = checkBox;
            this.name = name;
            this.description = description;
            this.lx = lx;
            this.ly = ly;
            this.date = date;
        }

        public CheckBox getCheckBox() {
            return checkBox;
        }

        public void setCheckBox(CheckBox checkBox) {
            this.checkBox = checkBox;
        }

        public TextView getName() {
            return name;
        }

        public void setName(TextView name) {
            this.name = name;
        }

        public TextView getDescription() {
            return description;
        }

        public void setDescription(TextView description) {
            this.description = description;
        }

        public TextView getLx() {
            return lx;
        }

        public void setLx(TextView lx) {
            this.lx = lx;
        }

        public TextView getLy() {
            return ly;
        }

        public void setLy(TextView ly) {
            this.ly = ly;
        }

        public TextView getDate() {
            return date;
        }

        public void setDate(TextView date) {
            this.date = date;
        }
    }
}