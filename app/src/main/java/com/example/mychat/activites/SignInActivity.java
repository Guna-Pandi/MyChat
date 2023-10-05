package com.example.mychat.activites;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mychat.R;
import com.example.mychat.databinding.ActivitySignInBinding;
import com.example.mychat.utilities.Constants;
import com.example.mychat.utilities.PreferenceManager;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Random;

public class SignInActivity extends AppCompatActivity {
    private ActivitySignInBinding binding;
    private PreferenceManager preferenceManager;
    private static final int PERMISSION_RQST_SEND=0;

    EditText email,mobile,password;
    EditText phoneNo;
    String phoneN;
    String mes;
    int y;
    EditText otp;
    String str;

    Button registerbtn;
    @Override


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferenceManager = new PreferenceManager(getApplicationContext());
        binding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize EditText fields
        email = findViewById(R.id.inputEmail);
        mobile = findViewById(R.id.inputMobile);
        password = findViewById(R.id.inputPassword);
        phoneNo = findViewById(R.id.inputMobile);
        registerbtn = findViewById(R.id.buttonSend);

        registerbtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                // When the "Register" button is clicked, send SMS
                sendSMSMessage();
            }
        });

        otp = findViewById(R.id.inputOtp);
        str = getIntent().getStringExtra("message_keyy");

        if (preferenceManager.getBoolean(Constants.KEY_IS_SIGNED_IN)) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivities(new Intent[]{intent});
            finish();
        }

        setListeners();
    }


    private void setListeners() {
        Intent in=getIntent();
        String sem=in.getStringExtra("msg1");
        String smb=in.getStringExtra("msg2");
        String spa=in.getStringExtra("msg3");
         email.setText(sem);
         mobile.setText(smb);
         password.setText(spa);
        binding.textCreateNewAccount.setOnClickListener(v ->
                startActivities(new Intent[]{new Intent(getApplicationContext(), SignUpActivity.class)}));
        binding.buttonSignIn.setOnClickListener(v -> {
            if (isValidSignInDetails()) {
                signIn();
            }
        });
    }

    private void signIn() {
        loading(true);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTION_USERS)
                .whereEqualTo(Constants.KEY_EMAIL, binding.inputEmail.getText().toString())
                .whereEqualTo(Constants.KEY_PASSWORD, binding.inputPassword.getText().toString())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && task.getResult().getDocuments().size() > 0 && otp.getText().toString().equals(str)) {
                        DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                        preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, true);
                        preferenceManager.putString(Constants.KEY_USER_ID, documentSnapshot.getId());
                        preferenceManager.putString(Constants.KEY_NAME, documentSnapshot.getString(Constants.KEY_NAME));
                        preferenceManager.putString(Constants.KEY_IMAGE, documentSnapshot.getString(Constants.KEY_IMAGE));

                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivities(new Intent[]{intent});
                    } else {
                        loading(false);
                        showToast("Unable to SignIn!");
                    }
                });
    }

    private void loading(Boolean isLoading) {
        if (isLoading) {
            binding.buttonSignIn.setVisibility(View.INVISIBLE);
            binding.progressBar.setVisibility(View.VISIBLE);
        } else {
            binding.progressBar.setVisibility(View.INVISIBLE);
            binding.buttonSignIn.setVisibility(View.VISIBLE);
        }
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private Boolean isValidSignInDetails() {
        if (binding.inputEmail.getText().toString().trim().isEmpty()) {
            showToast("Enter Email");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(binding.inputEmail.getText().toString()).matches()) {
            showToast("Enter Valid Email");
        } else if (binding.inputPassword.getText().toString().trim().isEmpty()) {
            showToast("Enter Password");
            return false;
        } else {
            return true;
        }
        return null;
    }

    protected void sendSMSMessage() {
        Random random = new Random();
         y = random.nextInt(1000);
        phoneN = phoneNo.getText().toString();
        mes = Integer.toString(y);

        // Request SMS sending permission each time the button is clicked
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, PERMISSION_RQST_SEND);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_RQST_SEND:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted, send the SMS
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(phoneN, null, mes, null, null);
                    Toast.makeText(getApplicationContext(), "SMS sent.", Toast.LENGTH_LONG).show();
                    email=findViewById(R.id.inputEmail);
                    mobile=findViewById(R.id.inputMobile);
                    password=findViewById(R.id.inputPassword);
                    String semail=email.getText().toString();
                    String sm=mobile.getText().toString();
                    String sp=password.getText().toString();
                    Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
                    intent.putExtra("msg1", semail);
                    intent.putExtra("msg2", sm);
                    intent.putExtra("msg3", sp);
                    intent.putExtra("message_keyy", mes);
                    startActivity(intent);
                } else {
                    // Permission denied, show a message
                    Toast.makeText(getApplicationContext(), "SMS permission denied. You can try again.", Toast.LENGTH_LONG).show();
                }
                break;
        }

    }
}