package com.sodirea.onthepoll;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
        // layout param for creating spacing between each voting option
        final LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 40, 0, 0);

        // we are guarenteed to always have a pollID in our intent (it is the only way to access this activity)
        Intent intent = getIntent();
        final String pollID = intent.getStringExtra("pollID");

        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("polls").document(pollID);
        // retrieving information about the poll from the database given the poll ID
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // creating the view for the name of the poll
                        Map<String, Object> data = document.getData();
                        TextView name = new TextView(ViewPollActivity.this);
                        name.setText((String) data.get("name"));
                        name.setTextSize(22);
                        name.setGravity(Gravity.CENTER);
                        mainLayout.addView(name);

                        // creating the view for the ID of the poll
                        TextView pollIDView = new TextView(ViewPollActivity.this);
                        pollIDView.setText("Poll ID: " + pollID + "    (Click to copy)");
                        pollIDView.setGravity(Gravity.CENTER);
                        pollIDView.setClickable(true);
                        pollIDView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // when they click on the pollID, copy the pollID to their clipboard
                                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                                ClipData clip = ClipData.newPlainText(pollID, pollID);
                                clipboard.setPrimaryClip(clip);
                                Toast toast = Toast.makeText(ViewPollActivity.this, "Copied to clipboard!", Toast.LENGTH_SHORT);
                                toast.show();
                            }
                        });
                        mainLayout.addView(pollIDView);

                        // creating the view for each option of the poll
                        Map<String, Object> options = (Map) data.get("options");
                        for (Map.Entry<String, Object> option : options.entrySet()) {
                            TextView optionView = new TextView(ViewPollActivity.this);
                            optionView.setText(option.getKey() + ": " + option.getValue());
                            optionView.setTextSize(18);
                            optionView.setLayoutParams(params);
                            mainLayout.addView(optionView);
                        }

                        // check if they are the owner of the poll. if they are, then give them the option to delete the poll
                        final SharedPreferences prefs = getSharedPreferences("created", Context.MODE_PRIVATE);
                        if (prefs.getBoolean(pollID, false)) {
                            Button delete = new Button(ViewPollActivity.this);
                            delete.setText("Delete Poll");
                            delete.setLayoutParams(params);
                            delete.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    // clicking the button will bring up an alert dialog to confirm if they really want to delete the poll
                                    AlertDialog.Builder builder = new AlertDialog.Builder(ViewPollActivity.this);
                                    builder.setTitle("Confirmation");
                                    builder.setMessage("Are you sure you want to delete this poll?");
                                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            // once they have confirmed that they want to delete the poll, delete the poll from the database, their shared preferences, and display a confirmation message that it was successfully deleted
                                            db.collection("polls").document(pollID).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    SharedPreferences.Editor editor = prefs.edit();
                                                    editor.remove(pollID);
                                                    Toast toast = Toast.makeText(ViewPollActivity.this, "Poll deleted.", Toast.LENGTH_LONG);
                                                    toast.show();
                                                    Intent intent = new Intent(ViewPollActivity.this, MainActivity.class);
                                                    startActivity(intent);
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast toast = Toast.makeText(ViewPollActivity.this, "Failed to delete poll.", Toast.LENGTH_LONG);
                                                    toast.show();
                                                }
                                            });
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
                            mainLayout.addView(delete);
                        }
                    } else {
                        // document doesn't exist
                        Toast toast = Toast.makeText(getApplicationContext(), "Poll not found.", Toast.LENGTH_LONG);
                        toast.show();
                        Intent intent = new Intent(ViewPollActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        // always go back to the home screen; don't want to go back to vote or create activities from here
        Intent intent = new Intent(ViewPollActivity.this, MainActivity.class);
        startActivity(intent);
    }
}
