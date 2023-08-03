package com.example.mychat.activites;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.mychat.R;
import com.example.mychat.databinding.ActivityChatBinding;
import com.example.mychat.models.User;
import com.example.mychat.utilities.Constants;

public class ChatActivity extends AppCompatActivity {
    private ActivityChatBinding binding;

    private User receivedUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListeners();
        LoadReceivedDetails();
    }

    private void LoadReceivedDetails(){
        receivedUser = (User) getIntent().getSerializableExtra(Constants.KEY_USER);
        binding.textName.setText(receivedUser.name);
    }

    private void setListeners(){
        binding.imageBack.setOnClickListener(v -> onBackPressed());
    }
}