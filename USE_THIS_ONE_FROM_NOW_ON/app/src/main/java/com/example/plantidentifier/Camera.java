package com.example.plantidentifier;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.TextView;

import java.io.IOException;
import java.nio.ByteBuffer;

public class Camera extends AppCompatActivity {

    //image in byte buffer form
    static protected ByteBuffer chosenImageByteBuffer;

    private static final int IMAGE_PICK_CODE = 1000;
    private static final int PERMISSION_CODE = 1001;

    private TextView textView;
    Bitmap chosenImageBitmap;

    private String flower = "";

    ClassifyImage classifier;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        try {
            Log.e("Camera", "About to initialized classifyImage");
            classifier = new ClassifyImage(this);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("Camera", "did not initialize classifyImage");
        }

        Button chooseButton = (Button) findViewById(R.id.choose_image_btn);
        chooseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //check runtime permission
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                        String [] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
                        requestPermissions(permissions, PERMISSION_CODE);
                    }
                    else {
                        pickImageFromGallery();
                    }
                }
                else {
                    pickImageFromGallery();
                }
            }
        });

        Button goToResults = (Button) findViewById(R.id.selectButton);
        goToResults.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    Log.e("Camera", "about to run instance of classifier.classifyPlantType");
                    flower = classifier.classifyPlantType();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("Camera", "Can't run instance of classifier.classifyPlantType");
                }

                //textView.setText(flower);

                Intent intent = new Intent(Camera.this, DisplayPlantTypes.class);
                startActivity(intent);
            }
        });
    }

    private void pickImageFromGallery() {
        Log.e("Camera", "About to execute pickImageFromGallery");
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == PERMISSION_CODE) {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    pickImageFromGallery();
                }
                else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e("Camera", "Running onActivityResult");

        ImageView imageView = findViewById(R.id.image_view);

        if (resultCode == RESULT_OK && requestCode == IMAGE_PICK_CODE){
            //imageView has the image in it. What we need to do is alter the image somehow.
            //this could mean altering a copy or altering R.id.image_view
            Log.e("Camera", "About to set image URI?? In onActivityResult");
            getImageView().setImageURI(data.getData());
            //okay we may want to change the bitmap.config argument, otherwise maybe this works?
            Log.e("Camera", "setting chosenImageBitmap to chosen photo");
            chosenImageBitmap = ProcessImage.getBitmapFromView(getImageView(), getImageView().getWidth(), getImageView().getHeight());
            Log.e("Camera", "about to resize bitmap");
            chosenImageBitmap = ProcessImage.resizeBitmap(chosenImageBitmap);
            //chosenImageBitmap = ProcessImage.grayscaleBitmap(chosenImageBitmap);
            imageView.setImageBitmap(chosenImageBitmap);

            Log.e("Camera", "about to initialize chosenImageByteBuffer");
            //set chosenImageBytebuffer to a bytebuffer of the processed image
            chosenImageByteBuffer = ProcessImage.preprocessImage(imageView, imageView.getWidth(), imageView.getHeight());
            Log.e("Camera", "numBytes: " + Integer.toString(chosenImageByteBuffer.remaining()));
            if (chosenImageByteBuffer == null)
            {
                Log.e("Camera", "chosenImageByteBuffer == null because problem is in processImage.java");
            }
        }
    }

    protected ImageView getImageView() {
        ImageView imageView = findViewById(R.id.image_view);
        return imageView;
    }

}
