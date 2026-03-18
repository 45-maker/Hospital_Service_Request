package com.hospital.serviceapp.activities;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.hospital.serviceapp.R;
import com.hospital.serviceapp.adapters.RequestAdapter;
import com.hospital.serviceapp.adapters.ServiceAdapter;
import com.hospital.serviceapp.adapters.UserAdapter;
import com.hospital.serviceapp.database.DatabaseHelper;
import com.hospital.serviceapp.models.Request;
import com.hospital.serviceapp.models.Service;
import com.hospital.serviceapp.models.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.List;

public class AdminDashboardActivity extends AppCompatActivity {
    private TabHost tabHost;
    private RecyclerView recyclerView;
    private DatabaseHelper dbHelper;
    private ImageButton btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        dbHelper = new DatabaseHelper(this);
        initViews();
        setupTabs();
    }

    private void initViews() {
        tabHost = findViewById(R.id.tabHost);
        tabHost.setup();
        btnLogout = findViewById(R.id.btnLogout);

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void setupTabs() {
        // Requests Tab
        TabHost.TabSpec spec = tabHost.newTabSpec("Requests");
        spec.setContent(R.id.tabRequests);
        spec.setIndicator("Requests");
        tabHost.addTab(spec);

        // Services Tab
        spec = tabHost.newTabSpec("Services");
        spec.setContent(R.id.tabServices);
        spec.setIndicator("Services");
        tabHost.addTab(spec);

        // Users Tab
        spec = tabHost.newTabSpec("Users");
        spec.setContent(R.id.tabUsers);
        spec.setIndicator("Users");
        tabHost.addTab(spec);

        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                switch (tabId) {
                    case "Requests":
                        loadRequests();
                        break;
                    case "Services":
                        loadServices();
                        break;
                    case "Users":
                        loadUsers();
                        break;
                }
            }
        });

        // Load initial tab
        loadRequests();
    }

    private void loadRequests() {
        recyclerView = findViewById(R.id.recyclerViewRequests);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<Request> requestList = dbHelper.getAllRequests();
        RequestAdapter adapter = new RequestAdapter(requestList, new RequestAdapter.OnRequestListener() {
            @Override
            public void onDelete(Request request) {
                deleteRequest(request);
            }
        });
        recyclerView.setAdapter(adapter);
    }

    private void loadServices() {
        recyclerView = findViewById(R.id.recyclerViewServices);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<Service> serviceList = dbHelper.getAllServices();
        ServiceAdapter adapter = new ServiceAdapter(serviceList, new ServiceAdapter.OnServiceListener() {
            @Override
            public void onDelete(Service service) {
                deleteService(service);
            }
        });
        recyclerView.setAdapter(adapter);

        FloatingActionButton btnAddService = findViewById(R.id.btnAddService);
        btnAddService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddServiceDialog();
            }
        });
    }

    private void loadUsers() {
        recyclerView = findViewById(R.id.recyclerViewUsers);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<User> userList = dbHelper.getAllUsers();
        UserAdapter adapter = new UserAdapter(userList, new UserAdapter.OnUserListener() {
            @Override
            public void onDelete(User user) {
                deleteUser(user);
            }
        });
        recyclerView.setAdapter(adapter);
    }

    private void showAddServiceDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_service, null);
        builder.setView(dialogView);

        EditText etServiceCode = dialogView.findViewById(R.id.etServiceCode);
        EditText etServiceName = dialogView.findViewById(R.id.etServiceName);

        builder.setTitle("Add Service")
                .setPositiveButton("Add", null)
                .setNegativeButton("Cancel", null);

        AlertDialog dialog = builder.create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = etServiceCode.getText().toString().trim();
                String name = etServiceName.getText().toString().trim();

                if (code.isEmpty()) {
                    etServiceCode.setError("Service code required");
                    return;
                }

                if (name.isEmpty()) {
                    etServiceName.setError("Service name required");
                    return;
                }

                boolean added = dbHelper.addService(code, name);
                if (added) {
                    Toast.makeText(AdminDashboardActivity.this, "Service added", Toast.LENGTH_SHORT).show();
                    loadServices();
                    dialog.dismiss();
                } else {
                    Toast.makeText(AdminDashboardActivity.this, "Service code already exists", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void deleteService(Service service) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Service")
                .setMessage("Are you sure you want to delete " + service.getName() + "?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    boolean deleted = dbHelper.removeService(service.getId());
                    if (deleted) {
                        Toast.makeText(this, "Service deleted", Toast.LENGTH_SHORT).show();
                        loadServices();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void deleteRequest(Request request) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Request")
                .setMessage("Are you sure you want to delete this request?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    boolean deleted = dbHelper.deleteRequest(request.getId());
                    if (deleted) {
                        Toast.makeText(this, "Request deleted", Toast.LENGTH_SHORT).show();
                        loadRequests();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void deleteUser(User user) {
        if (user.getRole().equals("admin")) {
            Toast.makeText(this, "Cannot delete admin user", Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("Delete User")
                .setMessage("Are you sure you want to delete " + user.getUsername() + "?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    boolean deleted = dbHelper.deleteUser(user.getId());
                    if (deleted) {
                        Toast.makeText(this, "User deleted", Toast.LENGTH_SHORT).show();
                        loadUsers();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }
}