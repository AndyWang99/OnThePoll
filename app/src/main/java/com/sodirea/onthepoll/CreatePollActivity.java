package com.sodirea.onthepoll;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.util.ArrayList;

public class CreatePollActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_poll);

        final Button addOptionBtn = (Button) findViewById(R.id.add_option);
        addOptionBtn.setOnClickListener(new View.OnClickListener() {
            int counter = 3;
            @Override
            public void onClick(View view) {
                LinearLayout layout = (LinearLayout) findViewById(R.id.option_container);
                EditText newOption = new EditText(CreatePollActivity.this);
                newOption.setHint("Option " + counter);
                newOption.setId(counter);
                layout.addView(newOption);
                layout.removeView(addOptionBtn); // puts button back to the bottom of the scrollview
                layout.addView(addOptionBtn);
                counter++;
            }
        });

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
                String name = ((EditText) findViewById(R.id.name)).getText().toString();
                ArrayList<String> listOfOptions = new ArrayList<>();
                if (((EditText) findViewById(R.id.option1)).getText().toString() != "") {
                    listOfOptions.add(((EditText) findViewById(R.id.option1)).getText().toString());
                }
                if (((EditText) findViewById(R.id.option2)).getText().toString() != "") {
                    listOfOptions.add(((EditText) findViewById(R.id.option2)).getText().toString());
                }
                int counter = 3;
                while (findViewById(counter) != null) {
                    if (((EditText) findViewById(counter)).getText().toString() != "") {
                        listOfOptions.add(((EditText) findViewById(counter)).getText().toString());
                    }
                    counter++;
                }
                Intent intent = new Intent(CreatePollActivity.this, ViewPollActivity.class);
                intent.putExtra("name", name);
                intent.putStringArrayListExtra("options", listOfOptions);
                startActivity(intent);
            }
        });
    }
}
