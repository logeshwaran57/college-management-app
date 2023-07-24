package com.example.collegeapp;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import org.bson.Document;


import java.util.Date;

import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;
import io.realm.mongodb.RealmResultTask;
import io.realm.mongodb.User;
import io.realm.mongodb.mongo.MongoClient;
import io.realm.mongodb.mongo.MongoCollection;
import io.realm.mongodb.mongo.MongoDatabase;
import io.realm.mongodb.mongo.iterable.MongoCursor;


public class uploadNotice extends AppCompatActivity {

    String appId = "application-0-pbtso";
    MongoClient mongoClient;
    MongoDatabase mongoDatabase;
    User user;
    App app;
    MongoCollection<Document> mongoCollection;

    Button uploadBtn;
    EditText NoticeInput, TitleInput;
    RadioGroup radioGroup;
    String category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_notice);

        uploadBtn = (Button) findViewById(R.id.uploadBtn);
        NoticeInput = (EditText) findViewById(R.id.addNotice);
        TitleInput = (EditText) findViewById(R.id.NoticeTitle);
        radioGroup = (RadioGroup) findViewById(R.id.RadioGroup);

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton radioButton = (RadioButton) group.findViewById(checkedId);
        });


        app = new App(new AppConfiguration.Builder(appId).build());
        user = app.currentUser();
        Log.v("AUTH", user + " " + user.getId());
        mongoClient = user.getMongoClient("mongodb-atlas");
        mongoDatabase = mongoClient.getDatabase("CollegeData");
        mongoCollection = mongoDatabase.getCollection("Notice");

        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String notice = NoticeInput.getText().toString();
                String title = TitleInput.getText().toString();
                Date date = new Date();

                int selectedId = radioGroup.getCheckedRadioButtonId();
                if (selectedId == -1) {
                    category = "casual";
                }
                else {
                    RadioButton radioButton = (RadioButton) radioGroup.findViewById(selectedId);
                    category = radioButton.getText().toString();
                }
                // create a document for find query
                Document queryFilter = new Document("notice", notice);
                // init a mongo collection find task
                RealmResultTask<MongoCursor<Document>> findTask = mongoCollection.find(queryFilter).iterator();
                findTask.getAsync(task -> {
                    if (task.isSuccess()) {
                        MongoCursor<Document> results = task.get();
                        // check if the document already exists
                        if (results.hasNext()) {
                            Log.v("Find", "Found something");
                            Document result = results.next();
                            Toast.makeText(getApplicationContext(), "Already Exists", Toast.LENGTH_LONG).show();
                            // update the result
                            result.append("title", title).append("notice", notice).append("category", category).append("date", date);
                            mongoCollection.updateOne(queryFilter, result).getAsync(updateResult -> {
                                if (updateResult.isSuccess()) {
                                    Log.v("Update", "Update successful");
                                    Toast.makeText(getApplicationContext(), "Update Successful", Toast.LENGTH_LONG).show();
                                } else {
                                    Log.v("Update", updateResult.getError().toString());
                                }
                            });
                        }
                        // if not then insert a new document into the mongodb collection
                        else {
                            // insert a document to the collection
                            mongoCollection.insertOne(
                                    new Document("user_id", user.getId()).append("title", title).append("notice", notice).append("category", category).append("date", date)
                            ).getAsync(result -> {
                                if (result.isSuccess()) {
                                    Log.v("Insert", "Insert successful");
                                    Toast.makeText(getApplicationContext(), "Insert Successful", Toast.LENGTH_LONG).show();
                                } else {
                                    Log.v("Insert", result.getError().toString());
                                }
                            });
                        }
                    } else {
                        Log.v("Error", task.getError().toString());
                    }
                });
            }
        });
    }
}