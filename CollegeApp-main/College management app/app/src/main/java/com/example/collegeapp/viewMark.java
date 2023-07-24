package com.example.collegeapp;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.bson.Document;

import java.util.ArrayList;
import java.util.Set;

import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;
import io.realm.mongodb.RealmResultTask;
import io.realm.mongodb.User;
import io.realm.mongodb.mongo.MongoClient;
import io.realm.mongodb.mongo.MongoCollection;
import io.realm.mongodb.mongo.MongoDatabase;
import io.realm.mongodb.mongo.iterable.MongoCursor;

public class viewMark extends AppCompatActivity implements View.OnClickListener {
    String appId = "application-0-pbtso";
    MongoClient mongoClient;
    MongoDatabase mongoDatabase;
    User user;
    App app;
    MongoCollection<Document> mongoCollection;

    Document marks;
    String gpa, sem_num;

    TextView getTitle1, getTitle2, getTitle3;
    TextView getSem1, getSem2, getSem3, getSem4, getSem5, getSem6;
    TextView getSub1, getSub2, getSub3, getSub4, getSub5, getSub6;
    TextView getGrade1, getGrade2, getGrade3, getGrade4, getGrade5, getGrade6;
    TextView getGPA;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_mark);

        getTitle1 = findViewById(R.id.title1);
        getTitle1 = findViewById(R.id.title2);
        getTitle1 = findViewById(R.id.title3);

        getSem1 = findViewById(R.id.sem1);
        getSem2 = findViewById(R.id.sem2);
        getSem3 = findViewById(R.id.sem3);
        getSem4 = findViewById(R.id.sem4);
        getSem5 = findViewById(R.id.sem5);
        getSem6 = findViewById(R.id.sem6);

        getSub1 = findViewById(R.id.sub1);
        getSub2 = findViewById(R.id.sub2);
        getSub3 = findViewById(R.id.sub3);
        getSub4 = findViewById(R.id.sub4);
        getSub5 = findViewById(R.id.sub5);
        getSub6 = findViewById(R.id.sub6);

        getGrade1 = findViewById(R.id.grade1);
        getGrade2 = findViewById(R.id.grade2);
        getGrade3 = findViewById(R.id.grade3);
        getGrade4 = findViewById(R.id.grade4);
        getGrade5 = findViewById(R.id.grade5);
        getGrade6 = findViewById(R.id.grade6);

        getGPA = findViewById(R.id.gpa);

        // connect to MongoDB
        app = new App(new AppConfiguration.Builder(appId).build());

        user = app.currentUser();
        mongoClient = user.getMongoClient("mongodb-atlas");
        mongoDatabase = mongoClient.getDatabase("CollegeData");
        mongoCollection = mongoDatabase.getCollection("Marks");

        // create a document for find query
        Document queryFilter = new Document("_id", user.getProfile().getEmail());
        // init a mongo collection find task
        RealmResultTask<MongoCursor<Document>> findTask = mongoCollection.find(queryFilter).iterator();
        findTask.getAsync(task -> {
                    if (task.isSuccess()) {
                        MongoCursor<Document> results = task.get();
                        // check if the document already exists
                        if (results.hasNext()) {
                            Log.v("Find", "Found something");
                            Document result = results.next();
                            marks = (Document) result.get("marks");
                            gpa = (String) result.get("gpa");
                            sem_num = (String) result.get("sem_num");
                            Log.v("doc", "" + gpa + " " + sem_num + " " + marks);
                            addData();
                        } else {
                            Log.v("ERROR", "no data found");
                            Toast.makeText(getApplicationContext(), "Sorry, no data found :(", Toast.LENGTH_LONG).show();
                        }
                    }
                    else {
                        Log.v("Find", "Found nothing");
                    }
                });
    }

    private void setTextView(TextView tv, String title, int color, int typeface) {
        Log.v("text",  " title " + title);

        tv.setText(title.toUpperCase());
        tv.setTextColor(color);
        tv.setPadding(10, 10, 10, 10);
        tv.setTypeface(Typeface.DEFAULT, typeface);
    }



    public void addData() {
        Log.v("data", "" + marks);
        Set<String> keysSet = marks.keySet();
        ArrayList<String> keysList = new ArrayList<>(keysSet);


        setTextView(getSem1, sem_num, Color.BLACK, Typeface.NORMAL);
        setTextView(getSub1, keysList.get(0), Color.BLACK, Typeface.NORMAL);
        setTextView(getGrade1, (String) marks.get(keysList.get(0)), Color.BLACK, Typeface.NORMAL);

        setTextView(getSem2, sem_num, Color.BLACK, Typeface.NORMAL);
        setTextView(getSub2, keysList.get(1), Color.BLACK, Typeface.NORMAL);
        setTextView(getGrade2, (String) marks.get(keysList.get(1)), Color.BLACK, Typeface.NORMAL);

        setTextView(getSem3, sem_num, Color.BLACK, Typeface.NORMAL);
        setTextView(getSub3, keysList.get(2), Color.BLACK, Typeface.NORMAL);
        setTextView(getGrade3, (String) marks.get(keysList.get(2)), Color.BLACK, Typeface.NORMAL);

        setTextView(getSem4, sem_num, Color.BLACK, Typeface.NORMAL);
        setTextView(getSub4, keysList.get(3), Color.BLACK, Typeface.NORMAL);
        setTextView(getGrade4, (String) marks.get(keysList.get(3)), Color.BLACK, Typeface.NORMAL);

        setTextView(getSem5, sem_num, Color.BLACK, Typeface.NORMAL);
        setTextView(getSub5, keysList.get(4), Color.BLACK, Typeface.NORMAL);
        setTextView(getGrade5, (String) marks.get(keysList.get(4)), Color.BLACK, Typeface.NORMAL);

        setTextView(getSem6, sem_num, Color.BLACK, Typeface.NORMAL);
        setTextView(getSub6, keysList.get(5), Color.BLACK, Typeface.NORMAL);
        setTextView(getGrade6, (String) marks.get(keysList.get(5)), Color.BLACK, Typeface.NORMAL);


        setTextView(getGPA, gpa, Color.BLACK, Typeface.NORMAL);
    }
    @Override
    public void onClick(View v) {
        int id = v.getId();
        TextView tv = findViewById(id);
        if (null != tv) {
            Log.i("onClick", "Clicked on row :: " + id);
//            Toast.makeText(this, "Clicked on row :: " + id + ", Text :: " + tv.getText(), Toast.LENGTH_SHORT).show();
        }
    }
}
