package com.example.plantidentifier;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private String plant_type="daisy";

    protected void setPlantType(String newPlantType) {
        plant_type=newPlantType;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button goToCamera = (Button) findViewById(R.id.toCamera);
        goToCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Camera.class);
                startActivity(intent);
            }

        });

        Button goToResults = (Button) findViewById(R.id.temporaryButton);
        goToResults.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, DisplayPlantTypes.class);
                startActivity(intent);
            }

        });

    }

}
