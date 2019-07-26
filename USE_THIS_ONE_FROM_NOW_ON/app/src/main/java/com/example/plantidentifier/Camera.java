package com.example.plantidentifier;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
<<<<<<< HEAD
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
=======
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.media.Image;
>>>>>>> Elaine's-Branch
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;


public class Camera extends AppCompatActivity {

<<<<<<< HEAD
    private static final int PERMISSION_CODE = 0;
=======
    private static final int IMAGE_PICK_CODE = 1000;
    private static final int PERMISSION_CODE = 1001;
>>>>>>> Elaine's-Branch

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
>>>>>>> Elaine's-Branch

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



    }
    Bitmap chosenImageBitmap;
    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 2);
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
        if (resultCode == RESULT_OK && requestCode == 2) {
            Uri imageUri = data.getData();
            //imageView.setImageURI(imageUri);
            imageView.setImageBitmap(getBitmap(imageUri));
        }
    }

    protected Bitmap getBitmap(Uri imageUri) {
        Bitmap bitmap = BitmapFactory.decodeFile(getUriPath(getApplicationContext(), imageUri));
        return bitmap;
    }

<<<<<<< Updated upstream
    protected String getUriPath(Context context, Uri uri) {
        String path = "";
        if(context != null && uri != null && isFileUri(uri)){
                path = uri.getPath();
=======
            ImageView second = findViewById(R.id.imageView);
            second.setImageBitmap(chosenImageBitmap);
>>>>>>> Stashed changes
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
=======
        if (resultCode == RESULT_OK && requestCode == IMAGE_PICK_CODE){
            //imageView has the image in it. What we need to do is alter the image somehow.
            //this could mean altering a copy or altering R.id.image_view
            imageView.setImageURI(data.getData());
            //okay we may want to change the bitmap.config argument, otherwise maybe this works?
            chosenImageBitmap = ProcessImage.getBitmapFromView(imageView, imageView.getWidth(), imageView.getHeight());
            chosenImageBitmap = ProcessImage.resizeBitmap(chosenImageBitmap);
            imageView.setImageBitmap(chosenImageBitmap);

        }
    }


>>>>>>> Elaine's-Branch

}
