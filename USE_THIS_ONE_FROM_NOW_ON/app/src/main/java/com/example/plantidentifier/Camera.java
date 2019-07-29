package com.example.plantidentifier;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.TextView;



public class Camera extends AppCompatActivity {

    private static final int PERMISSION_CODE = 1000;
    private static final int IMAGE_PICK_CODE = 1001;

    private Bitmap chosenImageBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        Button goToResults = (Button) findViewById(R.id.selectButton);
        goToResults.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Camera.this, DisplayPlantTypes.class);
                startActivity(intent);
            }

        });

        Button chooseButton = (Button) findViewById(R.id.choose_image_btn);
        chooseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //check runtime permission
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
                        requestPermissions(permissions, PERMISSION_CODE);
                    } else {
                        pickImageFromGallery();
                    }
                } else {
                    pickImageFromGallery();
                }
            }
        });
    }

    private void pickImageFromGallery () {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMAGE_PICK_CODE);
    }

    @Override
    public void onRequestPermissionsResult ( int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        if (requestCode == PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pickImageFromGallery();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data){
        if (resultCode == RESULT_OK && requestCode == IMAGE_PICK_CODE) {
                ImageView imageView = findViewById(R.id.image_view);
                //Uri imageUri = data.getData();
                //imageView.setImageURI(imageUri);
                chosenImageBitmap = ProcessImage.getBitmapFromView(imageView, imageView.getWidth(), imageView.getHeight());
                chosenImageBitmap = ProcessImage.resizeBitmap(chosenImageBitmap);
                //chosenImageBitmap = ProcessImage.grayscaleBitmap(chosenImageBitmap);
                imageView.setImageBitmap(chosenImageBitmap);

        }
    }

    protected Bitmap getChosenImageBitmap() {
        return chosenImageBitmap;
    }
}


/*
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
        */