package com.example.collegeapp;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.bson.Document;

import java.util.ArrayList;

import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;
import io.realm.mongodb.RealmResultTask;
import io.realm.mongodb.User;
import io.realm.mongodb.mongo.MongoClient;
import io.realm.mongodb.mongo.MongoCollection;
import io.realm.mongodb.mongo.MongoDatabase;
import io.realm.mongodb.mongo.iterable.MongoCursor;

public class ViewAttendance extends AppCompatActivity {
    String appId = "application-0-pbtso";
    MongoClient mongoClient;
    MongoDatabase mongoDatabase;
    User user;
    App app;
    MongoCollection<Document> mongoCollection;

    ArrayList<String> date_present;
    ArrayList<String> date_absent;

    ProgressBar AttnProgress;
    TextView DisplayPresent, DisplayAbsent, DisplayTotal, ProgressText;
    float progress = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_attendance);
        AttnProgress = findViewById(R.id.progress_bar);
        ProgressText = findViewById(R.id.text_view_progress);
        DisplayPresent = findViewById(R.id.totalPresent);
        DisplayAbsent = findViewById(R.id.totalAbsent);
        DisplayTotal = findViewById(R.id.totalDays);

        // connect to MongoDB
        app = new App(new AppConfiguration.Builder(appId).build());

        user = app.currentUser();
        mongoClient = user.getMongoClient("mongodb-atlas");
        mongoDatabase = mongoClient.getDatabase("CollegeData");
        mongoCollection = mongoDatabase.getCollection("Attendance");

        String reg_no = user.getProfile().getEmail();
        Log.v("name", "" + reg_no);
        // create a document for find query
        Document queryFilter = new Document("reg_no", reg_no);
        // init a mongo collection find task
        RealmResultTask<MongoCursor<Document>> findTask = mongoCollection.find(queryFilter).iterator();

        getAttendance(findTask);
    }

    @SuppressLint("SetTextI18n")
    private void getAttendance(RealmResultTask<MongoCursor<Document>> findTask) {
        findTask.getAsync(task -> {
            if (task.isSuccess()) {
                MongoCursor<Document> results = task.get();
                // check if the document already exists
                if (results.hasNext()) {
                    Log.v("Find", "Found something");
                    Document result = results.next();
                    date_present = (ArrayList<String>) result.get("date_present");
                    date_absent = (ArrayList<String>) result.get("date_absent");

                    if (date_absent == null || date_present == null) {
                        Toast.makeText(getApplicationContext(), "No records found :(", Toast.LENGTH_LONG).show();
                    }
                    int num_absent = date_absent.size();
                    int num_present = date_present.size();
                    float total_days = (float) num_present + num_absent;
                    progress = (float) ((num_present / total_days) * 100);
                    Log.v("present", "" + date_present.toString() + "total " + total_days + num_present + num_absent);
                    Log.v("absent", "" + date_absent.toString() + "progress " + progress);
                    updateProgressBar((int) progress);
                    DisplayTotal.setText("" + (int) total_days);
                    DisplayPresent.setText("" + num_present);
                    DisplayAbsent.setText("" + num_absent);
                }
                else {
                    Toast.makeText(getApplicationContext(), "No records found :(", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    private void updateProgressBar(int progress) {
        AttnProgress.setProgress(progress);
        Log.v("progress", "" + progress);
        ProgressText.setText(progress + "%");
    }
}