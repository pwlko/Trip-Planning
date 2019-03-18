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
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.pbwi.planyourtrip.R;

import java.util.List;
import java.util.Objects;

public class PlannedTrackListAdapter extends ArrayAdapter<PlannedTrackItemEntry> {

    public PlannedTrackListAdapter(@NonNull Context context, int resource, @NonNull List<PlannedTrackItemEntry> objects) {
        super(context, resource, objects);
    }

    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public View getView(final int position, View convertView, @NonNull ViewGroup parent) {

        View view = convertView;
        PlannedTrackItemEntry object = getItem(position);

        TextView startAttractionTextView;
        TextView endAttractionTextView;
        ImageButton navigationImageButton;
        ImageButton moveUpImageButton;
        ImageButton moveDownImageButton;



        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.planned_route_list_item_layout, parent, false);

            startAttractionTextView = view.findViewById(R.id.route_item_start_place_TextView);
            endAttractionTextView = view.findViewById(R.id.route_item_end_place_TextView);
            navigationImageButton = view.findViewById(R.id.route_item_navigation_ImageButton);
            moveUpImageButton = view.findViewById(R.id.route_item_move_up_ImageButton);
            moveDownImageButton = view.findViewById(R.id.route_item_move_down_ImageButton);
//
            navigationImageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!getItem(position).getStartAttraction().getName().equals(getItem(position).getEndAttraction().getName())) {
                        String uri = "http://maps.google.com/maps?f=d&hl=en&saddr=" + Objects.requireNonNull(getItem(position)).getStartAttraction().getLat() + "," + Objects.requireNonNull(getItem(position)).getStartAttraction().getLng() + "&daddr=" + Objects.requireNonNull(getItem(position)).getEndAttraction().getLat() + "," + Objects.requireNonNull(getItem(position)).getEndAttraction().getLng();
                        Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri));
                        getContext().startActivity(Intent.createChooser(intent, "Select an application"));
                    }
                }
            });

            view.setTag(new PlannedTrackListAdapter.PlannedTrackViewHolder( startAttractionTextView, endAttractionTextView, navigationImageButton, moveUpImageButton, moveDownImageButton));

        } else {
            PlannedTrackListAdapter.PlannedTrackViewHolder viewHolder = (PlannedTrackListAdapter.PlannedTrackViewHolder) view.getTag();

            startAttractionTextView = viewHolder.getStartAttractionTextView();
            endAttractionTextView = viewHolder.getEndAttractionTextView();
            navigationImageButton = viewHolder.getNavigationImageButton();
            moveUpImageButton = viewHolder.getMoveUpImageButton();
            moveDownImageButton = viewHolder.getMoveDownImageButton();

        }

        navigationImageButton.setTag(object);
        moveUpImageButton.setTag(object);
        moveDownImageButton.setTag(object);
        startAttractionTextView.setText(Objects.requireNonNull(object).getStartAttraction().getName());
        endAttractionTextView.setText(object.getEndAttraction().getName());

        navigationImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!getItem(position).getStartAttraction().getName().equals(getItem(position).getEndAttraction().getName())) {
                    String uri = "http://maps.google.com/maps?f=d&hl=en&saddr=" + Objects.requireNonNull(getItem(position)).getStartAttraction().getLat() + "," + Objects.requireNonNull(getItem(position)).getStartAttraction().getLng() + "&daddr=" + Objects.requireNonNull(getItem(position)).getEndAttraction().getLat() + "," + Objects.requireNonNull(getItem(position)).getEndAttraction().getLng();
                    Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri));
                    getContext().startActivity(Intent.createChooser(intent, "Select an application"));
                }
            }
        });

        return view;
    }

    static class PlannedTrackViewHolder {

        private TextView startAttractionTextView;
        private TextView endAttractionTextView;
        private ImageButton navigationImageButton;
        private ImageButton moveUpImageButton;
        private ImageButton moveDownImageButton;

        public PlannedTrackViewHolder(TextView startAttractionTextView, TextView endAttractionTextView, ImageButton navigationImageButton, ImageButton moveUpImageButton, ImageButton moveDownImageButton) {
            this.startAttractionTextView = startAttractionTextView;
            this.endAttractionTextView = endAttractionTextView;
            this.navigationImageButton = navigationImageButton;
            this.moveUpImageButton = moveUpImageButton;
            this.moveDownImageButton = moveDownImageButton;
        }

        public TextView getStartAttractionTextView() {
            return startAttractionTextView;
        }

        public void setStartAttractionTextView(TextView startAttractionTextView) {
            this.startAttractionTextView = startAttractionTextView;
        }

        public TextView getEndAttractionTextView() {
            return endAttractionTextView;
        }

        public void setEndAttractionTextView(TextView endAttractionTextView) {
            this.endAttractionTextView = endAttractionTextView;
        }

        public ImageButton getNavigationImageButton() {
            return navigationImageButton;
        }

        public void setNavigationImageButton(ImageButton navigationImageButton) {
            this.navigationImageButton = navigationImageButton;
        }

        public ImageButton getMoveUpImageButton() {
            return moveUpImageButton;
        }

        public void setMoveUpImageButton(ImageButton moveUpImageButton) {
            this.moveUpImageButton = moveUpImageButton;
        }

        public ImageButton getMoveDownImageButton() {
            return moveDownImageButton;
        }

        public void setMoveDownImageButton(ImageButton moveDownImageButton) {
            this.moveDownImageButton = moveDownImageButton;
        }
    }



}
