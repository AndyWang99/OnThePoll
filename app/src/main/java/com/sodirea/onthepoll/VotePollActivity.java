package com.sodirea.onthepoll;

import android.content.Intent;
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
                        Map<String, Object> data = document.getData();
                        TextView name = new TextView(VotePollActivity.this);
                        name.setText((String) data.get("name"));
                        name.setTextSize(22);
                        name.setGravity(Gravity.CENTER);
                        mainLayout.addView(name);

                        Map<String, Object> options = (Map) data.get("options");
                        for (Map.Entry<String, Object> option : options.entrySet()) {
                            final int optionVotes = (int) (long) option.getValue();
                            final Button optionBtn = new Button(VotePollActivity.this);
                            optionBtn.setText(option.getKey());
                            optionBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    DocumentReference docRef2 = db.collection("polls").document(pollID);
                                    docRef2.update("options." + optionBtn.getText(), optionVotes + 1);
                                    Intent intent = new Intent(VotePollActivity.this, ViewPollActivity.class);
                                    intent.putExtra("pollID", pollID);
                                    startActivity(intent);
                                }
                            });
                            mainLayout.addView(optionBtn);
                        }
                    } else {
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
