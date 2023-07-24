package com.example.collegeapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.concurrent.atomic.AtomicReference;

import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;
import io.realm.mongodb.Credentials;
import io.realm.mongodb.User;


public class AdminLogin extends AppCompatActivity {
    String appId = "application-0-pbtso";
    EditText reg_input, passwd_input;
    Button login;
    ProgressBar loadingProgress;

    Thread thread = null;

    App app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        reg_input = (EditText) findViewById(R.id.editTextNumber);
        passwd_input = (EditText) findViewById(R.id.editTextDate);
        login = (Button) findViewById(R.id.loginbtn);
        loadingProgress = (ProgressBar) findViewById(R.id.LoadingSpinner);
        loadingProgress.setVisibility(View.GONE);

        app = new App(new AppConfiguration.Builder(appId).build());

        login.setOnClickListener(v -> {
            loadingProgress.setVisibility(View.VISIBLE);
            String reg_no = reg_input.getText().toString();
            String passwd = passwd_input.getText().toString();
            AuthenticateUser(app, reg_no, passwd);
        });
    }

    private void AuthenticateUser(App app, String reg_no, String passwd) {
        if (validateData(reg_no, passwd)) {

            Credentials emailPasswordCredentials = Credentials.emailPassword(reg_no, passwd);
            AtomicReference<User> user = new AtomicReference<User>();
            app.loginAsync(emailPasswordCredentials, it -> {
                if (it.isSuccess()) {
                    Log.v("AUTH", "Successfully authenticated using an register number and DOB.");
                    user.set(app.currentUser());
                    openAdminHome();
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(
                                getApplicationContext(), "Please enter valid details!!", Toast.LENGTH_LONG
                        ).show();
                    });
                }
            });
        }
        else {
            runOnUiThread(() -> {
                Toast.makeText(
                        getApplicationContext(), "Please enter valid details!!", Toast.LENGTH_LONG
                ).show();
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sh = getSharedPreferences("MyAuth", MODE_PRIVATE);
        String reg_no = sh.getString("reg_no", "");
        String passwd = sh.getString("passwd", "");
        if (reg_no.equals("123456")) {
            reg_input.setText(reg_no);
            passwd_input.setText(passwd);
            loadingProgress.setVisibility(View.VISIBLE);
            AuthenticateUser(app, reg_no, passwd);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // save login details
        SharedPreferences sharedPreferences = getSharedPreferences("MyAuth", MODE_PRIVATE);
        SharedPreferences.Editor myEdit = sharedPreferences.edit();

        myEdit.putString("reg_no", reg_input.getText().toString());
        myEdit.putString("passwd", passwd_input.getText().toString());
        myEdit.apply();
    }

    public void openAdminHome() {
        Intent intent = new Intent(this, AdminHome.class);
        startActivity(intent);
    }

    public boolean validateData(String reg_no, String passwd) {
        if (reg_no.equals("123456")) {
            return !passwd.isEmpty();
        }
        return false;
    }
}