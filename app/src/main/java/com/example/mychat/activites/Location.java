package com.example.mychat.activites;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.example.mychat.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.Locale;

public class Location extends AppCompatActivity implements LocationListener {

    private LocationManager locationManager;
    private TextView tvCity, tvState, tvCountry, tvPin, tvLocality;

    // Define your Firebase database reference.
    private DatabaseReference databaseReference;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        // Initialize Firebase database reference.
        databaseReference = FirebaseDatabase.getInstance().getReference("serviceAvailability");

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getApplicationContext(),
                android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
        }
        tvCity = findViewById(R.id.city);
        tvState = findViewById(R.id.state);
        tvCountry = findViewById(R.id.country);
        tvPin = findViewById(R.id.pin);
        tvLocality = findViewById(R.id.locality);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationEnabled();
        getLocation();

        Button checkStatusButton = findViewById(R.id.verifyLocation);
        checkStatusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String pincode = tvPin.getText().toString();
                if (!pincode.isEmpty()) {
                    checkServiceAvailability(pincode);
                }
            }
        });
    }

    private void checkServiceAvailability(final String pincode) {
        databaseReference.child(pincode).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getValue(Boolean.class)) {
                    Toast.makeText(Location.this, "Service is available for Pincode: " + pincode, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(Location.this, "Service is not available for Pincode: " + pincode, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle database error.
                Toast.makeText(Location.this, "Service is available for pincode 641014", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void locationEnabled() {
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;
        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!gps_enabled && !network_enabled) {
            new AlertDialog.Builder(Location.this)
                    .setTitle("Enable GPS Service")
                    .setMessage("We need your GPS location to show Near Places around you.")
                    .setCancelable(false)
                    .setPositiveButton("Enable", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        }
    }

    void getLocation() {
        try {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 500, 5, (LocationListener) this);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLocationChanged(android.location.Location location) {
        try {
            Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

            if (addresses != null && !addresses.isEmpty()) {
                tvCity.setText(addresses.get(0).getLocality());
                tvState.setText(addresses.get(0).getAdminArea());
                tvCountry.setText(addresses.get(0).getCountryName());
                tvPin.setText(addresses.get(0).getPostalCode());
                tvLocality.setText(addresses.get(0).getAddressLine(0));

                // Display a toast message indicating that location information is available.
                Toast.makeText(this, "Location information available", Toast.LENGTH_SHORT).show();
            } else {
                // Display a toast message indicating that location information is not available.
                Toast.makeText(this, "Location information not available", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            // Handle exceptions or errors here.
            Toast.makeText(this, "Error occurred while getting location information", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }
}
