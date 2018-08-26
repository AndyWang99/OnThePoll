package com.sodirea.onthepoll;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button createBtn = (Button) findViewById(R.id.create);
        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Create New Poll");
                builder.setMessage("Enter a name for your poll:");
                final EditText inputPollName = new EditText(MainActivity.this);
                inputPollName.setId((Integer) 1);
                builder.setView(inputPollName);
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        EditText input = (EditText) ((AlertDialog) dialogInterface).findViewById((Integer) 1);
                        System.out.println(input.getText());
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                AlertDialog createDialog = builder.create();
                createDialog.show();
            }
        });

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
                        System.out.println(input.getText());
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
                        System.out.println(input.getText());
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
}
