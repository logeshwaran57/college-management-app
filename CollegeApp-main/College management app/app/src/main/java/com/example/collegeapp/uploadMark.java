package com.example.collegeapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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

public class uploadMark extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    String appId = "application-0-pbtso";
    MongoClient mongoClient;
    MongoDatabase mongoDatabase;
    User user;
    App app;
    MongoCollection<Document> mongoCollection;

    EditText regNo, Sub1, Sub2, Sub3, Sub4, Sub5, Sub6, SemNum, GPA;
    Spinner Grade1, Grade2, Grade3, Grade4, Grade5, Grade6;
    Button uploadBtn;

    String reg_no, sub1, sub2, sub3, sub4, sub5, sub6, inputSemNum, inputGpa;
    String sem_num;
    ArrayList<String> grades;
    ArrayList<String> subject_codes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_mark);
        regNo = (EditText) findViewById(R.id.regno);
        Sub1 = (EditText) findViewById(R.id.sub1);
        Sub2 = (EditText) findViewById(R.id.sub2);
        Sub3 = (EditText) findViewById(R.id.sub3);
        Sub4 = (EditText) findViewById(R.id.sub4);
        Sub5 = (EditText) findViewById(R.id.sub5);
        Sub6 = (EditText) findViewById(R.id.sub6);
        SemNum = (EditText) findViewById(R.id.sem);
        GPA = (EditText) findViewById(R.id.gpa);

        uploadBtn = (Button) findViewById(R.id.submit);

        // connect to MongoDB
        app = new App(new AppConfiguration.Builder(appId).build());

        user = app.currentUser();
        Log.v("AUTH", user + " " + user.getId());
        mongoClient = user.getMongoClient("mongodb-atlas");
        mongoDatabase = mongoClient.getDatabase("CollegeData");
        mongoCollection = mongoDatabase.getCollection("Marks");


        ArrayAdapter<CharSequence> adapter = addItemsOnSpinner();
        addListenerOnButton();
        addListenerOnSpinnerItemSelection(adapter);
    }


    // add items into spinner dynamically
    public ArrayAdapter<CharSequence> addItemsOnSpinner() {

        Grade1 = (Spinner) findViewById(R.id.grade1);
        Grade2 = (Spinner) findViewById(R.id.grade2);
        Grade3 = (Spinner) findViewById(R.id.grade3);
        Grade4 = (Spinner) findViewById(R.id.grade4);
        Grade5 = (Spinner) findViewById(R.id.grade5);
        Grade6 = (Spinner) findViewById(R.id.grade6);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.grade_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        return adapter;
    }

    public void addListenerOnSpinnerItemSelection(ArrayAdapter<CharSequence> adapter) {
        // Apply the adapter to the spinner
        Grade1.setAdapter(adapter);
        Grade2.setAdapter(adapter);
        Grade3.setAdapter(adapter);
        Grade4.setAdapter(adapter);
        Grade5.setAdapter(adapter);
        Grade6.setAdapter(adapter);
    }

    public void addListenerOnButton() {
        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reg_no = regNo.getText().toString();
                sub1 = Sub1.getText().toString();
                sub2 = Sub2.getText().toString();
                sub3 = Sub3.getText().toString();
                sub4 = Sub4.getText().toString();
                sub5 = Sub5.getText().toString();
                sub6 = Sub6.getText().toString();


                Document Marks = new Document(sub1, Grade1.getSelectedItem())
                        .append(sub2, Grade2.getSelectedItem())
                        .append(sub3, Grade2.getSelectedItem())
                        .append(sub4, Grade2.getSelectedItem())
                        .append(sub5, Grade2.getSelectedItem())
                        .append(sub6, Grade2.getSelectedItem());

                inputSemNum = SemNum.getText().toString();
                inputGpa = GPA.getText().toString();
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
                            sem_num = (String) result.get("sem_num");

                            if (sem_num == null) {
                                result.append("sem_num", inputSemNum);
                            } else if (sem_num.equals(inputSemNum)) {
                                Toast.makeText(getApplicationContext(), "Already Exists", Toast.LENGTH_LONG).show();
                            } else {
                                result.append("sem_num", inputSemNum).append("marks", Marks).append("gpa", inputGpa);
                            }
                        }
                        // if not then insert a new document into the mongodb collection
                        else {
                            Log.v("Find", "Found nothing");
                            // insert a document to the collection
                            mongoCollection.insertOne(
                                    new Document("_id", reg_no)
                                            .append("user_id", user.getId())
                                            .append("sem_num", inputSemNum)
                                            .append("marks", Marks)
                                            .append("gpa", inputGpa)
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
                });
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos,long id) {
        Log.v("grade", "" + parent.getItemAtPosition(pos).toString());
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}