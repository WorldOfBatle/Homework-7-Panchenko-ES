package com.example.homework_7_panchenko_es;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.homework_7_panchenko_es.databinding.ActivityAboutBinding;

public class AboutActivity extends AppCompatActivity {

    private ActivityAboutBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAboutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setTitle(getString(R.string.about_title));
    }
}
