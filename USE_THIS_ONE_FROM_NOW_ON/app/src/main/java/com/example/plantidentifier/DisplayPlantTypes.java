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

        /**Do note that there is a cleaner way to do this, but I don't quite understand
         * how it works so I'm leaving the buttons like this for now*/
        //button goes to camera screen
        Button goToCamera = (Button) findViewById(R.id.toCamera);
        goToCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DisplayPlantTypes.this, Camera.class);
                startActivity(intent);
            }

        });

        //button goes to main screen
        Button goToMenu = (Button) findViewById(R.id.toMenu);
        goToMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DisplayPlantTypes.this, MainActivity.class);
                startActivity(intent);
            }

        });

    }
}
