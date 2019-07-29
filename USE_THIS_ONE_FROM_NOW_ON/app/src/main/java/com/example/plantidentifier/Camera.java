package com.example.plantidentifier;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
<<<<<<< HEAD
=======
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

import static android.app.Activity.RESULT_OK;

import static android.app.Activity.RESULT_OK;
import static com.example.plantidentifier.ClassifyImage.*;
>>>>>>> Bronte's-Branch


public class Camera extends AppCompatActivity {

    private static final int PERMISSION_CODE = 1000;
    private static final int IMAGE_PICK_CODE = 1001;

    private ClassifyImage classify;
    private TextView textView;
    Bitmap chosenImageBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

<<<<<<< HEAD
        Button goToResults = (Button) findViewById(R.id.selectButton);
        goToResults.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Camera.this, DisplayPlantTypes.class);
                startActivity(intent);
            }

        });

=======
>>>>>>> Bronte's-Branch
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

<<<<<<< HEAD
=======
        Button goToResults = (Button) findViewById(R.id.selectButton);
        goToResults.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (classify == null) {
                    textView.setText("classifier problem");
                    return;
                }
                String result = classify.classifyFrame(chosenImageBitmap);
>>>>>>> Bronte's-Branch

                textView.setText(result);
            }
        });
    }

<<<<<<< HEAD
    }
    Bitmap chosenImageBitmap;
=======
>>>>>>> Bronte's-Branch
    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
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
        ImageView imageView = findViewById(R.id.image_view);
<<<<<<< HEAD
        if (resultCode == RESULT_OK && requestCode == IMAGE_PICK_CODE) {
            Uri imageUri = data.getData();
            //imageView.setImageURI(imageUri);
            //imageView.setImageBitmap(getBitmap(imageUri));;
=======

        if (resultCode == RESULT_OK && requestCode == IMAGE_PICK_CODE){
>>>>>>> Bronte's-Branch
            //imageView has the image in it. What we need to do is alter the image somehow.
            //this could mean altering a copy or altering R.id.image_view
            imageView.setImageURI(data.getData());
            //okay we may want to change the bitmap.config argument, otherwise maybe this works?
            chosenImageBitmap = ProcessImage.getBitmapFromView(imageView, imageView.getWidth(), imageView.getHeight());
            chosenImageBitmap = ProcessImage.resizeBitmap(chosenImageBitmap);
<<<<<<< HEAD
=======
            //chosenImageBitmap = ProcessImage.grayscaleBitmap(chosenImageBitmap);
>>>>>>> Bronte's-Branch
            imageView.setImageBitmap(chosenImageBitmap);
        }
    }


    protected Bitmap getBitmap(Uri imageUri) {
        Bitmap bitmap = BitmapFactory.decodeFile(getUriPath(getApplicationContext(), imageUri));
        return bitmap;
    }


    protected String getUriPath(Context context, Uri uri) {
        String path = "";
        if(context != null && uri != null && isFileUri(uri)){
                path = uri.getPath();
        }
        return path;
    }

    protected boolean isFileUri(Uri uri) {
        boolean isFile = false;
        if(uri != null) {
            String uriScheme = uri.getScheme();
            if(uriScheme.equals("file"))
                isFile = true;
        }
        return isFile;
    }


}