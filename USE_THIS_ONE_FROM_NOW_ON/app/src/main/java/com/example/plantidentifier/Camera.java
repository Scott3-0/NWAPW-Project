package com.example.plantidentifier;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import org.tensorflow.lite.Interpreter;
import android.content.res.AssetFileDescriptor;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.EditText;
import android.widget.TextView;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;


public class Camera extends AppCompatActivity {

    private static final int IMAGE_PICK_CODE = 1000;
    private static final int PERMISSION_CODE = 1001;

    //need input as byte map
    EditText inputNumber;
    //need output as TextView
    TextView outputNumber;

    //declare interpreter
    Interpreter tflite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        //figure out how to input image
        inputNumber = (EditText) findViewById(R.id.inputNumber);
        //output TextView
        outputNumber = (TextView) findViewById(R.id.outputNumber);

        //construct interpreter and load tflite file
        try {
            tflite = new Interpreter(loadModelFile());
        }
        catch (Exception ex){
            ex.printStackTrace();
        }

        //do inference???
        Button goToResults = (Button) findViewById(R.id.selectButton);
        goToResults.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                float prediction = doInference(inputNumber.getText().toString());
                outputNumber.setText(Float.toString(prediction));
            }

        });

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

    //makes input and output into arrays and runs the tflite model!!!
    public float doInference(String inputString){
        //"input shape is [1]"
        float[] inputVal = new float[1];
        inputVal[0] = Float.valueOf(inputString);

        //"output shape is [1][1]"
        float[][] outputVal = new float[1][1];

        //run the inference, giving it the input shape and the output is the output shape
        tflite.run(inputVal, outputVal);

        //result
        float inferredValue = outputVal[0][0];
        return inferredValue;
    }

    //"memory-map the model file in assets"
    private MappedByteBuffer loadModelFile() throws IOException {
        //open tflite model and memory map it so it loads
        AssetFileDescriptor fileDescriptor = this.getAssets().openFd("andriodModel.tflite");
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(fileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    private void pickImageFromGallery() {
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
        ImageView imageView = findViewById(R.id.image_view);
        if (resultCode == RESULT_OK && requestCode == IMAGE_PICK_CODE){
            imageView.setImageURI(data.getData());
        }
    }

}
