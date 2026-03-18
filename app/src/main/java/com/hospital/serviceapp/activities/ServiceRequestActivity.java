package com.hospital.serviceapp.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.hospital.serviceapp.R;
import com.hospital.serviceapp.database.DatabaseHelper;
import com.hospital.serviceapp.models.Service;
import java.util.List;

public class ServiceRequestActivity extends AppCompatActivity {
    private Spinner spinnerServices;
    private EditText etWardNumber, etBedNumber, etNotes;
    private Button btnSubmit, btnLogout;
    private DatabaseHelper dbHelper;
    private int userId;
    private List<Service> serviceList;
    private int selectedServiceId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_request);

        dbHelper = new DatabaseHelper(this);
        userId = getIntent().getIntExtra("user_id", -1);

        initViews();
        loadServices();
        setupListeners();
    }

    private void initViews() {
        spinnerServices = findViewById(R.id.spinnerServices);
        etWardNumber = findViewById(R.id.etWardNumber);
        etBedNumber = findViewById(R.id.etBedNumber);
        etNotes = findViewById(R.id.etNotes);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnLogout = findViewById(R.id.btnLogout);
    }

    private void loadServices() {
        serviceList = dbHelper.getAllServices();
        String[] serviceNames = new String[serviceList.size()];

        for (int i = 0; i < serviceList.size(); i++) {
            serviceNames[i] = serviceList.get(i).getName();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, serviceNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerServices.setAdapter(adapter);

        spinnerServices.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedServiceId = serviceList.get(position).getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedServiceId = -1;
            }
        });
    }

    private void setupListeners() {
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitRequest();
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void submitRequest() {
        String wardNumber = etWardNumber.getText().toString().trim();
        String bedNumber = etBedNumber.getText().toString().trim();
        String notes = etNotes.getText().toString().trim();

        // Validation
        if (selectedServiceId == -1) {
            Toast.makeText(this, "Please select a service", Toast.LENGTH_SHORT).show();
            return;
        }

        if (wardNumber.isEmpty()) {
            etWardNumber.setError("Ward number is required");
            return;
        }

        if (bedNumber.isEmpty()) {
            etBedNumber.setError("Bed number is required");
            return;
        }

        boolean isSubmitted = dbHelper.submitRequest(userId, selectedServiceId,
                wardNumber, bedNumber, notes);

        if (isSubmitted) {
            Toast.makeText(this, "Request submitted successfully", Toast.LENGTH_SHORT).show();
            etWardNumber.setText("");
            etBedNumber.setText("");
            etNotes.setText("");
        } else {
            Toast.makeText(this, "Failed to submit request", Toast.LENGTH_SHORT).show();
        }
    }
}