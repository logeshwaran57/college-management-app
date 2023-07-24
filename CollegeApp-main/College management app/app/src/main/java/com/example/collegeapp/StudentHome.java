package com.example.collegeapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.material.card.MaterialCardView;

import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;
import io.realm.mongodb.User;


public class StudentHome extends AppCompatActivity {
    String appId = "application-0-pbtso";
    User user;
    App app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_home);

        TextView NameView = (TextView) findViewById(R.id.username);
        MaterialCardView Mark = (MaterialCardView) findViewById(R.id.Mark);
        MaterialCardView ViewNotice = (MaterialCardView) findViewById(R.id.Notice);
        MaterialCardView ViewAttendance = (MaterialCardView) findViewById(R.id.ViewAttendance);

        app = new App(new AppConfiguration.Builder(appId).build());
        user = app.currentUser();
        String reg_no = (String) user.getProfile().getEmail();
        NameView.setText("Welcome " + reg_no + "!");

        Mark.setOnClickListener(view -> openMark());
        ViewNotice.setOnClickListener(view -> openNotice());
        ViewAttendance.setOnClickListener(view -> openAttendance());
    }
    public void openMark() {
        Intent intent = new Intent(this, viewMark.class);
        startActivity(intent);
    }
    public void openAttendance() {
        Intent intent = new Intent(this, ViewAttendance.class);
        startActivity(intent);
    }
    public void openNotice() {
        Intent intent = new Intent(this, ViewNotice.class);
        startActivity(intent);
    }
}

