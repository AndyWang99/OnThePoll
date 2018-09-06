package com.sodirea.onthepoll;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button createBtn = (Button) findViewById(R.id.create);
        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, CreatePollActivity.class);
                startActivity(intent);
            }
        });

        final LinearLayout yourPolls = findViewById(R.id.your_polls);
        final LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 20, 0, 20);
        SharedPreferences prefs = getSharedPreferences("created", Context.MODE_PRIVATE);
        for (Map.Entry entry : prefs.getAll().entrySet()) {
            final String pollID = (String) entry.getKey();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("polls").document(pollID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            TextView pollName = new TextView(MainActivity.this);
                            pollName.setText((String) document.get("name"));
                            pollName.setTextSize(18);
                            pollName.setLayoutParams(params);
                            pollName.setClickable(true);
                            pollName.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(MainActivity.this, ViewPollActivity.class);
                                    intent.putExtra("pollID", pollID);
                                    startActivity(intent);
                                }
                            });
                            yourPolls.addView(pollName);
                        }
                    }
                }
            });
        }

        Button voteBtn = (Button) findViewById(R.id.vote);
        voteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Vote On A Poll");
                builder.setMessage("Please enter a poll ID:");
                final EditText inputVoteID = new EditText(MainActivity.this);
                inputVoteID.setId((Integer) 2);
                builder.setView(inputVoteID);
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        EditText input = (EditText) ((AlertDialog) dialogInterface).findViewById((Integer) 2);
                        // checking if they already voted on this poll via shared preferences
                        SharedPreferences prefs = getSharedPreferences("voted", Context.MODE_PRIVATE);
                        String pollID = input.getText().toString();
                        if (prefs.contains(pollID)) { // display message if they have
                            Toast toast = Toast.makeText(MainActivity.this, "You have already voted on this poll!", Toast.LENGTH_LONG);
                            toast.show();
                        } else {
                            Intent intent = new Intent(MainActivity.this, VotePollActivity.class);
                            intent.putExtra("pollID", pollID);
                            startActivity(intent);
                        }
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                AlertDialog voteDialog = builder.create();
                voteDialog.show();
            }
        });

        Button checkBtn = (Button) findViewById(R.id.check);
        checkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Check Poll Results");
                builder.setMessage("Please enter a poll ID:");
                final EditText inputCheckID = new EditText(MainActivity.this);
                inputCheckID.setId((Integer) 3);
                builder.setView(inputCheckID);
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        EditText input = (EditText) ((AlertDialog) dialogInterface).findViewById((Integer) 3);
                        Intent intent = new Intent(MainActivity.this, ViewPollActivity.class);
                        intent.putExtra("pollID", input.getText().toString());
                        startActivity(intent);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                AlertDialog checkDialog = builder.create();
                checkDialog.show();
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBackPressed() {
        this.finishAffinity();
    }
}
