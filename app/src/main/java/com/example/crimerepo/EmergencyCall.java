package com.example.crimerepo;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class EmergencyCall extends AppCompatActivity {

    private ListView emergencyNumbersListView;
    private String[] emergencyNames = {"Police", "Ambulance","Fire Department"};
    private String[] emergencyNumbers = {"100", "108","101"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);

        emergencyNumbersListView = findViewById(R.id.emergency_numbers_listview);

        CustomAdapter adapter = new CustomAdapter(this, emergencyNames, emergencyNumbers);
        emergencyNumbersListView.setAdapter(adapter);

        emergencyNumbersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedNumber = emergencyNumbers[position];
                // Code to initiate the call using the selected number
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + selectedNumber));
                startActivity(intent);
            }
        });
    }

    private class CustomAdapter extends ArrayAdapter<String> {

        private Context context;
        private String[] names;
        private String[] numbers;

        public CustomAdapter(Context context, String[] names, String[] numbers) {
            super(context, R.layout.list_item_emergency_number, names);
            this.context = context;
            this.names = names;
            this.numbers = numbers;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.list_item_emergency_number, parent, false);

            TextView emergencyNameTextView = view.findViewById(R.id.emergency_name);
            TextView emergencyNumberTextView = view.findViewById(R.id.emergency_number);

            emergencyNameTextView.setText(names[position]);
            emergencyNumberTextView.setText(numbers[position]);

            return view;
        }
    }
}
