package com.sodirea.onthepoll;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class VotePollActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vote_poll);

        final LinearLayout mainLayout = findViewById(R.id.main_layout);

        // we are guarenteed to always have a pollID in our intent (it is the only way to access this activity)
        Intent intent = getIntent();
        final String pollID = intent.getStringExtra("pollID");

        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("polls").document(pollID);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    final DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // map containing information about the poll under the given pollID in our database
                        Map<String, Object> data = document.getData();

                        // creating a TextView for the name of the poll
                        TextView name = new TextView(VotePollActivity.this);
                        name.setText((String) data.get("name"));
                        name.setTextSize(22);
                        name.setGravity(Gravity.CENTER);
                        mainLayout.addView(name);

                        // creating a Button for each option
                        Map<String, Object> options = (Map) data.get("options");
                        for (Map.Entry<String, Object> option : options.entrySet()) {
                            final int optionVotes = (int) (long) option.getValue();
                            final Button optionBtn = new Button(VotePollActivity.this);
                            optionBtn.setText(option.getKey());
                            optionBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    // when the button of an option is clicked, add one to the number of votes for that option in the database
                                    DocumentReference docRef2 = db.collection("polls").document(pollID);
                                    docRef2.update("options." + optionBtn.getText(), optionVotes + 1);
                                    // add this pollID to the list of polls that the user has voted on
                                    SharedPreferences prefs = getSharedPreferences("voted", Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = prefs.edit();
                                    editor.putBoolean(pollID, true);
                                    editor.apply();
                                    // transition into the ViewPollActivity to see the results of the poll the user just voted on
                                    Intent intent = new Intent(VotePollActivity.this, ViewPollActivity.class);
                                    intent.putExtra("pollID", pollID);
                                    startActivity(intent);
                                }
                            });
                            mainLayout.addView(optionBtn);
                        }
                    } else { // if they ID they provided could not be found in our database, then tell the user the poll could not be found and send them back to MainActivity
                        Toast toast = Toast.makeText(getApplicationContext(), "Poll not found.", Toast.LENGTH_LONG);
                        toast.show();
                        Intent intent = new Intent(VotePollActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                }
            }
        });
    }
}
