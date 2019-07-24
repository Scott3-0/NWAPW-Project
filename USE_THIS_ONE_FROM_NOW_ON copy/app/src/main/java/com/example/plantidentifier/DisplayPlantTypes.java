package com.example.plantidentifier;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class DisplayPlantTypes extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_plant_types);

        //Does this work???
        Button goToCamera = (Button) findViewById(R.id.toCamera);
        goToCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DisplayPlantTypes.this, Camera.class);
                startActivity(intent);
            }

        });
        //Does this work too?? And menu = mainActivity.java
        Button goToMenu = (Button) findViewById(R.id.toMenu);
        goToCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DisplayPlantTypes.this, MainActivity.class);
                startActivity(intent);
            }

        });
    }
}
