package com.example.collegeapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import android.content.Context;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import android.content.res.ColorStateList;
import android.widget.LinearLayout.LayoutParams;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;

import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.chip.Chip;

import org.bson.Document;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;
import io.realm.mongodb.RealmResultTask;
import io.realm.mongodb.User;
import io.realm.mongodb.mongo.MongoClient;
import io.realm.mongodb.mongo.MongoCollection;
import io.realm.mongodb.mongo.MongoDatabase;
import io.realm.mongodb.mongo.iterable.MongoCursor;

public class ViewNotice extends AppCompatActivity {

    String appId = "application-0-pbtso";
    MongoClient mongoClient;
    MongoDatabase mongoDatabase;
    User user;
    App app;
    MongoCollection<Document> mongoCollection;

    LinearLayout linearLayout;

    Thread thread = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_notice);
        linearLayout = findViewById(R.id.LinearLayout);

        app = new App(new AppConfiguration.Builder(appId).build());
        user = app.currentUser();
        Log.v("AUTH", user + " " + user.getId());
        mongoClient = user.getMongoClient("mongodb-atlas");
        mongoDatabase = mongoClient.getDatabase("CollegeData");
        mongoCollection = mongoDatabase.getCollection("Notice");

        UpdateUI();
    }

    private void UpdateUI() {
        // create an aggregation pipeline
        List<Document> pipeline = Arrays.asList(new Document("$sort", new Document("date", -1L)));
        // query mongodb using the pipeline
        RealmResultTask<MongoCursor<Document>> aggregationTask =
                mongoCollection.aggregate(pipeline).iterator();
        // handle success or failure of the query
        final int[] i = {0};
        aggregationTask.getAsync(task -> {
            if (task.isSuccess()) {
                MongoCursor<Document> results = task.get();
                // iterate over and print the results to the log
                while (results.hasNext()) {
                    if (i[0] < 10) {
                        Document NoticeData = results.next();
                        thread = new Thread(() -> runOnUiThread(() -> {
                            CardView noticeCard = createCard(
                                    NoticeData.get("title").toString(),
                                    NoticeData.get("notice").toString(),
                                    NoticeData.get("category").toString(),
                                    (Date) NoticeData.get("date")
                            );
                            linearLayout.addView(noticeCard);
                        }));
                        thread.start();
                        thread.setPriority(Thread.MAX_PRIORITY);
                        i[0]++;
                    }
                    else {
                        break;
                    }
                }
            } else {
                Log.e("EXAMPLE", "failed to aggregate documents with: ", task.getError());
            }
        });
    }

    private CardView createCard(String title, String content, String category, Date date) {
        int fillColor = R.color.primary;
        if (category.equals("Placement")) {
            fillColor = R.color.green;
        }
        else if (category.equals("Important")) {
            fillColor = R.color.red;
        }

        // Notice Card
        Context context = getApplicationContext();
        CardView noticeCard = new CardView(context);
        LayoutParams cardParams = new LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT
        );
        cardParams.setMargins(40, 20, 40, 20);
        noticeCard.setLayoutParams(cardParams);
        noticeCard.setCardElevation(15);
        noticeCard.setMaxCardElevation(15);
        noticeCard.setPreventCornerOverlap(true);
        noticeCard.setRadius(30);
        noticeCard.setCardBackgroundColor(Color.WHITE);


        LinearLayout cardLinearLayout = new LinearLayout(context);
        LinearLayout.LayoutParams cardLayoutParams = new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT
        );
        cardLayoutParams.setMargins(20, 20, 20, 20);
        cardLinearLayout.setLayoutParams(cardLayoutParams);
        cardLinearLayout.setOrientation(LinearLayout.VERTICAL);
        // Title of the notice
        TextView titleTV = new TextView(context);
        LayoutParams tv1Params = new LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT
        );
        tv1Params.setMargins(20, 15, 20, 10);
        titleTV.setLayoutParams(tv1Params);
        titleTV.setText(title);
        titleTV.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.primary));
        titleTV.setTextSize(22);
        titleTV.setTypeface(null, Typeface.BOLD);

        // Content of the notice
        TextView dateTV = new TextView(context);
        LayoutParams dateParams = new LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT
        );
        dateParams.setMargins(20, 5, 20, 15);
        dateTV.setLayoutParams(dateParams);
        dateTV.setText(date.toString());
        dateTV.setTextColor(Color.DKGRAY);
        dateTV.setTextSize(12);
        dateTV.setTypeface(null, Typeface.NORMAL);

        // Content of the notice
        TextView contentTV = new TextView(context);
        LayoutParams tv2Params = new LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT
        );
        tv2Params.setMargins(20, 15, 20, 25);
        contentTV.setLayoutParams(tv2Params);
        contentTV.setText(content);
        contentTV.setTextColor(Color.BLACK);
        contentTV.setTextSize(18);
        contentTV.setTypeface(null, Typeface.ITALIC);

        // Chip
        Chip categoryChip = new Chip(this);
        LayoutParams chipParams = new LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT
        );
        chipParams.setMargins(20, 15, 20, 15);
        categoryChip.setLayoutParams(chipParams);
        categoryChip.setChipBackgroundColorResource(fillColor);
        categoryChip.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
        categoryChip.setText(category);


        cardLinearLayout.addView(titleTV);
        cardLinearLayout.addView(dateTV);
        cardLinearLayout.addView(contentTV);
        cardLinearLayout.addView(categoryChip);

        noticeCard.addView(cardLinearLayout);
        return noticeCard;
    }
}