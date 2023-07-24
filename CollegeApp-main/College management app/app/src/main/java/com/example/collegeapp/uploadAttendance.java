package com.example.collegeapp;


import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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


public class uploadAttendance extends AppCompatActivity implements OnItemSelectedListener {
    String appId = "application-0-pbtso";
    MongoClient mongoClient;
    MongoDatabase mongoDatabase;
    User user;
    App app;
    MongoCollection<Document> mongoCollection;

    EditText regNoInput, dateInput;
    Button uploadBtn;
    Spinner attnDropdown;

    String reg_no, date, attendance;

    ArrayList<String> date_present;
    ArrayList<String> date_absent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_attendance);
        regNoInput = (EditText) findViewById(R.id.reg_no);
        dateInput = (EditText) findViewById(R.id.enterDate);
        uploadBtn = (Button) findViewById(R.id.upload);
        attnDropdown = (Spinner) findViewById(R.id.attn);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.attendance_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        attnDropdown.setAdapter(adapter);
        // Spinner click listener
        attnDropdown.setOnItemSelectedListener(this);
        // connect to MongoDB
        app = new App(new AppConfiguration.Builder(appId).build());

        user = app.currentUser();
        Log.v("AUTH", user + " " + user.getId());
        mongoClient = user.getMongoClient("mongodb-atlas");
        mongoDatabase = mongoClient.getDatabase("CollegeData");
        mongoCollection = mongoDatabase.getCollection("Attendance");
    }

    // method that runs when an item from the dropdown is selected
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        uploadBtn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                reg_no = regNoInput.getText().toString();
                date = dateInput.getText().toString();
                attendance = (String) parent.getItemAtPosition(position).toString();
                Log.v("InputData", "reg no: " + reg_no + "date: " + date + "atten: " + attendance);

                // create a document for find query
                Document queryFilter = new Document("reg_no", reg_no);
                // init a mongo collection find task
                RealmResultTask<MongoCursor<Document>> findTask = mongoCollection.find(queryFilter).iterator();

                findTask.getAsync(task -> {
                    if (task.isSuccess()) {
                        MongoCursor<Document> results = task.get();
                        // check if the document already exists
                        if (results.hasNext()) {
                            Log.v("Find", "Found something");
                            Document result = results.next();
                            date_present = (ArrayList<String>) result.get("date_present");
                            date_absent = (ArrayList<String>) result.get("date_absent");

                            if (date_present == null) {
                                date_present = new ArrayList<>();
                            }
                            if (date_absent == null) {
                                date_present = new ArrayList<>();
                            }
                            // check if the date is already in date_present or date_absent
                            boolean dateExists = checkDuplicates(date_present, date_absent, date, attendance);
                            // if present prompt the user that it already exists
                            if (dateExists) {
                                Toast.makeText(getApplicationContext(), "Already Exists", Toast.LENGTH_LONG).show();
                            }
                            // if not, then update the document
                            else {
                                // determine the array to append the date
                                if (attendance.equals("Present")) {
                                    // append date to present array
                                    date_present.add(date);
                                } else {
                                    // append date to absent array
                                    date_absent.add(date);
                                }
                                // update the result
                                result.append("date_present", date_present).append("date_absent", date_absent);
                                mongoCollection.updateOne(queryFilter, result).getAsync(updateResult -> {
                                    if (updateResult.isSuccess()) {
                                        Log.v("Update", "Update successful");
                                        Toast.makeText(getApplicationContext(), "Update Successful", Toast.LENGTH_LONG).show();
                                    } else {
                                        Log.v("Update", updateResult.getError().toString());
                                    }
                                });
                            }
                        }
                        // if not then insert a new document into the mongodb collection
                        else {
                            date_present = new ArrayList<>();
                            date_absent = new ArrayList<>();
                            Log.v("Find", "Found nothing");
                            // check if the student is present or not
                            if (attendance.equals("Present")) {
                                // append date to present array
                                date_present.add(date);
                            } else {
                                // append date to absent array
                                date_absent.add(date);
                            }
                            // insert a document to the collection
                            mongoCollection.insertOne(
                                    new Document("user_id", user.getId()).append("reg_no", reg_no).append("date_present", date_present).append("date_absent", date_absent)
                            ).getAsync(result -> {
                                if (result.isSuccess()) {
                                    Log.v("Insert", "Insert successful");
                                    Toast.makeText(getApplicationContext(), "Insert Successful", Toast.LENGTH_LONG).show();
                                } else {
                                    Log.v("Insert", result.getError().toString());
                                }
                            });
                        }
                    }
                    else {
                        Log.v("Error", task.getError().toString());
                    }
                });
            }
        });
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public static boolean checkDuplicates(ArrayList<String> date_present, ArrayList<String> date_absent, String date, String attendance) {
        if (date_present.contains(date) && attendance.equals("Absent")) {
            date_present.remove(date);
            return false;
        }
        else if (date_absent.contains(date) && attendance.equals("Present")) {
            date_absent.remove(date);
            return false;
        }
        else return date_present.contains(date) || date_absent.contains(date);
    }
}