package com.example.pbwi.planyourtrip.activieties.Attractions;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.pbwi.planyourtrip.R;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Objects;

public class NearbyGoogleAttractionListAdapter extends ArrayAdapter<GoogleAttractionEntry> {


    public NearbyGoogleAttractionListAdapter(@NonNull Context context, int resource, @NonNull List<GoogleAttractionEntry> objects) {
        super(context, resource, objects);
    }

    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public View getView(final int position, View convertView, @NonNull ViewGroup parent) {

        View view = convertView;
        GoogleAttractionEntry object = getItem(position);


        final CheckBox checkBox;
        TextView nameTextView;
        TextView ratingTextView;
        TextView locationTextView = null;
        ImageButton searchBrowserImageButton;

        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.google_attraction_item_layout, parent, false);

            nameTextView = view.findViewById(R.id.google_attraction_name_value_TextView);
            ratingTextView = view.findViewById(R.id.google_attraction_rate_value_TextView);
            locationTextView = view.findViewById(R.id.google_attraction_latLng_value_TextView);
            checkBox = view.findViewById(R.id.google_attraction_checkBox_TextView);
            searchBrowserImageButton = view.findViewById(R.id.search_browser_ImageButton);
//            searchBrowserImageButton.setMovementMethod(LinkMovementMethod.getInstance());

            view.setTag(new GoogleAttractionViewHolder( checkBox, nameTextView, ratingTextView, locationTextView, searchBrowserImageButton));


            checkBox.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    CheckBox cb = (CheckBox) v;
                    GoogleAttractionEntry attraction = (GoogleAttractionEntry) cb.getTag();
                    attraction.setChecked(cb.isChecked());
                }
            });


            searchBrowserImageButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {

                    String url = "http://www.google.pl/search?q=";
                    String convertedAttName = Objects.requireNonNull(getItem(position))
                            .getName().replace(" ", "%20");
                    url += convertedAttName;
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    getContext().startActivity(browserIntent);

                }
            });

        } else {
            GoogleAttractionViewHolder viewHolder = (GoogleAttractionViewHolder) view.getTag();

            nameTextView = viewHolder.getNameTextView();
            ratingTextView = viewHolder.getRatingTextView();
            locationTextView = viewHolder.getLocationTextView();
            checkBox = viewHolder.getCheckBox();
            searchBrowserImageButton = viewHolder.getSearchBrowserImageButton();

        }


        checkBox.setTag(object);
        checkBox.setChecked(Objects.requireNonNull(object).isChecked());
        checkBox.setEnabled(!object.isAddedToVisit());
        searchBrowserImageButton.setTag(object);
        nameTextView.setText(object.getName());
        ratingTextView.setText(object.getRate() + " /" + object.getNumberOfRatings());
        locationTextView.setText(new DecimalFormat("#.##").format(object.getLat()) + ", " + new DecimalFormat("#.##").format(object.getLng()));




        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Objects.requireNonNull(getItem(position)).toggleChecked();
            }
        });


        searchBrowserImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "http://www.google.pl/search?q=";
                String convertedAttName = Objects.requireNonNull(getItem(position)).getName().replace(" ", "%20");

                url += convertedAttName;
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                getContext().startActivity(browserIntent);
            }
        });

        return view;
    }


    static class GoogleAttractionViewHolder {
        private CheckBox checkBox;
        private TextView nameTextView;
        private TextView ratingTextView;
        private TextView locationTextView;
        private ImageButton searchBrowserImageButton;

        private GoogleAttractionViewHolder(CheckBox checkBox, TextView nameTextView, TextView ratingTextView, TextView locationTextView, ImageButton searchBrowserImageButton) {
            this.checkBox = checkBox;
            this.nameTextView = nameTextView;
            this.ratingTextView = ratingTextView;
            this.locationTextView = locationTextView;
            this.searchBrowserImageButton = searchBrowserImageButton;
        }


        private CheckBox getCheckBox() {
            return checkBox;
        }

        private void setCheckBox(CheckBox checkBox) {
            this.checkBox = checkBox;
        }

        private TextView getNameTextView() {
            return nameTextView;
        }

        public void setNameTextView(TextView nameTextView) {
            this.nameTextView = nameTextView;
        }

        private TextView getRatingTextView() {
            return ratingTextView;
        }

        private void setRatingTextView(TextView ratingTextView) {
            this.ratingTextView = ratingTextView;
        }

        private TextView getLocationTextView() {
            return locationTextView;
        }

        private void setLocationTextView(TextView locationTextView) {
            this.locationTextView = locationTextView;
        }

        private ImageButton getSearchBrowserImageButton() {
            return searchBrowserImageButton;
        }

        private void setSearchBrowserImageButton(ImageButton searchBrowserImageButton) {
            this.searchBrowserImageButton = searchBrowserImageButton;
        }
    }

}
