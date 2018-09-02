package com.sodirea.onthepoll;

import android.content.Intent;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class ViewPollActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_poll);

        final LinearLayout mainLayout = findViewById(R.id.main_layout);

        Intent intent = getIntent();
        String pollID = intent.getStringExtra("pollID");

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("polls").document(pollID);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Map<String, Object> data = document.getData();
                        TextView name = new TextView(ViewPollActivity.this);
                        name.setText((String) data.get("name"));
                        name.setTextSize(22);
                        name.setGravity(Gravity.CENTER);
                        mainLayout.addView(name);

                        Map<String, Object> options = (Map) data.get("options");
                        for (Map.Entry<String, Object> option : options.entrySet()) {
                            TextView optionView = new TextView(ViewPollActivity.this);
                            optionView.setText(option.getKey() + ": " + option.getValue());
                            mainLayout.addView(optionView);
                        }
                    } else {
                        Toast toast = Toast.makeText(getApplicationContext(), "Poll not found.", Toast.LENGTH_LONG);
                        toast.show();
                        Intent intent = new Intent(ViewPollActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                }
            }
        });
        TextView pollIDView = new TextView(ViewPollActivity.this);
        pollIDView.setText("Poll ID: " + pollID);
        mainLayout.addView(pollIDView);
    }
}
