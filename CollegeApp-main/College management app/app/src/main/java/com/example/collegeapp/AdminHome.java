package com.example.collegeapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.google.android.material.card.MaterialCardView;

import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;

public class AdminHome extends AppCompatActivity {
    String appId = "application-0-pbtso";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);
        App app = new App(new AppConfiguration.Builder(appId).build());

        MaterialCardView uploadNotice = (MaterialCardView) findViewById(R.id.addNotice);
        MaterialCardView uploadAttendance = (MaterialCardView) findViewById(R.id.addAttendance);
        MaterialCardView uploadMark = (MaterialCardView) findViewById(R.id.addMark);

        uploadNotice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openAddNotice();
            }
        });
        uploadAttendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openAddAttendance();
            }
        });
        uploadMark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { openAddMark(); }
        });
    }
    public void openAddNotice() {
        Intent intent = new Intent(this, uploadNotice.class);
        startActivity(intent);
    }
    public void openAddAttendance() {
        Intent intent = new Intent(this, uploadAttendance.class);
        startActivity(intent);
    }
    public void openAddMark() {
        Intent intent = new Intent(this, uploadMark.class);
        startActivity(intent);
    }
    }