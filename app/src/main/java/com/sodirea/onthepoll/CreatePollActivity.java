package com.sodirea.onthepoll;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class CreatePollActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_poll);

        final Button addOptionBtn = (Button) findViewById(R.id.add_option);
        addOptionBtn.setOnClickListener(new View.OnClickListener() {
            // this variable keeps track of the number of options there are. initial value of counter is 3 because by default, there are already 2 options available
            int counter = 3;
            @Override
            public void onClick(View view) {
                // creating the EditText view for a new option entry
                LinearLayout layout = (LinearLayout) findViewById(R.id.option_container);
                EditText newOption = new EditText(CreatePollActivity.this);
                newOption.setHint("Option " + counter);
                newOption.setId(counter);
                newOption.setSingleLine();
                layout.addView(newOption);
                layout.removeView(addOptionBtn); // puts addOption button back to the bottom of the scrollview
                layout.addView(addOptionBtn);
                counter++;
            }
        });

        // cancel button takes them back to the MainActivity
        Button cancel = (Button) findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CreatePollActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        Button create = (Button) findViewById(R.id.create);
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // map containing values that we will store into the database (specifically, the name of the poll, as well as a nested map containing all the options and number of votes each option has)
                Map<String, Object> poll = new HashMap<>();
                FirebaseFirestore db = FirebaseFirestore.getInstance();

                // storing the name of the poll into the map, under the key "name"
                String name = ((EditText) findViewById(R.id.name)).getText().toString();
                poll.put("name", name);

                // map containing the option names (key) and the number of votes (value) each option has
                Map<String, Integer> optionsAndVotes = new HashMap<>();
                // storing the first two options into this map (if the EditText field is not an empty string)
                if (!((EditText) findViewById(R.id.option1)).getText().toString().matches("")) {
                    optionsAndVotes.put(((EditText) findViewById(R.id.option1)).getText().toString(), 0);
                }
                if (!((EditText) findViewById(R.id.option2)).getText().toString().matches("")) {
                    optionsAndVotes.put(((EditText) findViewById(R.id.option2)).getText().toString(), 0);
                }
                // storing any additional options that they created into the map (if not empty string)
                int counter = 3;
                while (findViewById(counter) != null) {
                    if (!((EditText) findViewById(counter)).getText().toString().matches("")) {
                        optionsAndVotes.put(((EditText) findViewById(counter)).getText().toString(), 0);
                    }
                    counter++;
                }
                // storing the nested map with option names and votes, under the key "options"
                poll.put("options", optionsAndVotes);

                if (!name.matches("")) { // ensure that there is a name
                    if (optionsAndVotes.size() > 1) { // ensure that there is more than one option
                        db.collection("polls")
                                .add(poll)
                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        // tell user that the poll was successfully created
                                        Toast toast = Toast.makeText(getApplicationContext(), "Poll created.", Toast.LENGTH_SHORT);
                                        toast.show();
                                        // add document id to user's shared preferences of polls they have created
                                        // add document id to user's shared preferences to list it in "your polls"
                                        String pollID = documentReference.getId();
                                        SharedPreferences prefs = getSharedPreferences("created", Context.MODE_PRIVATE);
                                        SharedPreferences.Editor editor = prefs.edit();
                                        editor.putBoolean(pollID, true);
                                        editor.apply();
                                        // transition to view the newly created poll
                                        Intent intent = new Intent(CreatePollActivity.this, ViewPollActivity.class);
                                        intent.putExtra("pollID", pollID);
                                        startActivity(intent);
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // tell user that the poll was not created
                                        Toast toast = Toast.makeText(getApplicationContext(), "Failed to create poll.", Toast.LENGTH_SHORT);
                                        toast.show();
                                    }
                                });
                    } else {
                        // tell user there is only one choice
                        Toast toast = Toast.makeText(getApplicationContext(), "You need more than one option!", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                } else {
                    // tell user to input a name
                    Toast toast = Toast.makeText(getApplicationContext(), "You need to input a name.", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });
    }
}
