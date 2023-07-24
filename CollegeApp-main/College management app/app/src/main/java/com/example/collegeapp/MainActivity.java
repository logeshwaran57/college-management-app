package com.example.collegeapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import io.realm.Realm;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);
        /* init realm */
        Realm.init(this);

        Button teacher_btn = (Button) findViewById(R.id.teacher_btn);
        Button student_btn = (Button) findViewById(R.id.student_btn);
        teacher_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openAdminLogin();
            }
        });
        student_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openStudentLogin();
            }
        });

    }
    public void openAdminLogin() {
        Intent intent = new Intent(this, AdminLogin.class);
        startActivity(intent);
    }
    public void openStudentLogin() {
        Intent intent = new Intent(this, StudentLogin.class);
        startActivity(intent);
    }
}