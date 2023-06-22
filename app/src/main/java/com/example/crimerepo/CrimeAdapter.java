package com.example.crimerepo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class CrimeAdapter extends ArrayAdapter<Crime> {
    private
    Context context;


    private List<Crime> crimeList;

    public CrimeAdapter(Context context, List<Crime> crimeList) {
        super(context, 0, crimeList);
        this.context = context;
        this.crimeList = crimeList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        Crime crime = crimeList.get(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_crime, parent, false);

            holder = new ViewHolder();
            holder.crimeTitleTextView = convertView.findViewById(R.id.crime_title_textview);
            holder.crimeImageView = convertView.findViewById(R.id.crime_image_imageview);
            holder.Status = convertView.findViewById(R.id.display_status);
            holder.dateTime=convertView.findViewById(R.id.date);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.crimeTitleTextView.setText(crime.getTitle());
        holder.dateTime.setText(crime.getDate());
        holder.Status.setText(crime.getStatus());

        // Load the image using Glide
        if (crime.getImageUri() != null && !crime.getImageUri().isEmpty()) {
            holder.crimeImageView.setVisibility(View.VISIBLE); // Set the visibility to visible
            Glide.with(context)
                    .load(crime.getImageUri())
                    .placeholder(R.drawable.crime_image_1) // Placeholder image while loading
                    .error(R.drawable.crime_image_2) // Error image if loading fails
                    .into(holder.crimeImageView);
        } else {
            holder.crimeImageView.setVisibility(View.GONE); // Set the visibility to gone
        }



        return convertView;
    }


    private static class ViewHolder {
        public ImageView deleteIcon;
        TextView crimeTitleTextView;
        TextView crimeLocationTextView;
        ImageView crimeImageView;
        TextView Status;
        TextView dateTime;
    }

    public interface CrimeDeleteListener {
        void onDeleteCrime(Crime crime);
    }
    public void addCrime(Crime crime) {
        crimeList.add(crime);
        notifyDataSetChanged();
    }
    public interface crimeDeleteListener {
        void onDeleteCrime(Crime crime);
    }
    public void clearCrimeList() {
        crimeList.clear();
        notifyDataSetChanged();
    }
}
