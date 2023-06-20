package com.example.crimerepo;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class ReportAdapter extends ArrayAdapter<Crime> {

    private List<Crime> reportList;

    public ReportAdapter(Context context, List<Crime> reports) {
        super(context, 0, reports);
        reportList = reports;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.my_report_crime_list, parent, false);
            holder = new ViewHolder();
            holder.reportImageView = convertView.findViewById(R.id.report_image_imageview);
            holder.titleTextView = convertView.findViewById(R.id.report_title_textview);
            holder.deleteButton = convertView.findViewById(R.id.delete_report_button);
            holder.editButton = convertView.findViewById(R.id.edit_report_button);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Crime report = reportList.get(position);

        // Set the report image, title, and delete button click listener
        Glide.with(getContext())
                .load(report.getImageUri())
                .apply(RequestOptions.centerCropTransform())
                .into(holder.reportImageView);
        holder.titleTextView.setText(report.getTitle());
        holder.deleteButton.setTag(position);
        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = (int) v.getTag();

                // Get the report object at the clicked position
                Crime report = reportList.get(position);

                // Get the reportId of the clicked report
                String reportId = report.getReportId();
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Confirm Delete")
                        .setMessage("Are you sure you want to delete this report?")
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Remove the report from the database using its reportId
                                DatabaseReference reportsRef = FirebaseDatabase.getInstance().getReference("crime_reports");
                                reportsRef.child(reportId).removeValue();

                                // Remove the report from the list
                                reportList.remove(position);

                                // Notify the adapter that the data has changed
                                notifyDataSetChanged();

                                // Show a delete success message
                                Toast.makeText(getContext(), "Report deleted successfully", Toast.LENGTH_SHORT).show();

                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            }
        });
        holder.editButton.setTag(position);
        holder.editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = (int) v.getTag();

                // Get the report object at the clicked position
                Crime report = reportList.get(position);

                // Pass the report ID or relevant data to the edit activity using Intent
                Intent intent = new Intent(getContext(), EditReportActivity.class);
                intent.putExtra("reportId", report.getReportId());
                // Add any other relevant data you want to pass to the EditReportActivity

                // Start the EditReportActivity
                getContext().startActivity(intent);
            }
        });


        return convertView;
    }



    private static class ViewHolder {
        ImageView reportImageView;
        TextView titleTextView;
        ImageButton deleteButton;
        ImageButton editButton;
    }
}
