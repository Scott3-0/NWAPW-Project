package com.example.plantidentifier;
import org.tensorflow.lite.Interpreter;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.TextView;

public class ClassifyImage {
    /** Tag for the {@link Log}. */
    private static final String TAG = "TfLiteCameraDemo";

    //labels that allow you to classify the output
    private List<String> labelList;

    //byteBuffer holds image data for the tflite file
    private ByteBuffer imgData = null;

    //image dimensions and size
    private static final int batchSize = 1;

    private static final int pixelSize = 3;

    static final int imageSizeX = 32;
    static final int imageSizeY = 32;

    private static final int IMAGE_MEAN = 128;
    private static final float IMAGE_STD = 128.0f;

    //array holds results from inference
    private float[][] labelProbArray = null;

    /** multi-stage low pass filter **/
    private float[][] filterLabelProbArray = null;
    private static final int FILTER_STAGES = 3;
    private static final float FILTER_FACTOR = 0.4f;

    //array to store image dimensions
    private int[] intValues = new int[imageSizeX * imageSizeY];

    private PriorityQueue<Map.Entry<String, Float>> sortedLabels =
            new PriorityQueue<>(
                    RESULTS_TO_SHOW,
                    new Comparator<Map.Entry<String, Float>>() {
                        @Override
                        public int compare(Map.Entry<String, Float> o1, Map.Entry<String, Float> o2) {
                            return (o1.getValue()).compareTo(o2.getValue());
                        }
                    });

    //need output as TextView
    TextView outputNumber;

    //declare interpreter
    Interpreter tflite;

    //constructor
    ClassifyImage(Activity activity) throws IOException {
        tflite = new Interpreter(loadModelFile(activity));
        labelList = loadLabelList(activity);
        imgData =
                ByteBuffer.allocateDirect(
                        4 * batchSize * imageSizeX * imageSizeY * pixelSize);
        imgData.order(ByteOrder.nativeOrder());
        labelProbArray = new float[1][labelList.size()];
        filterLabelProbArray = new float[FILTER_STAGES][labelList.size()];
        Log.d(TAG, "Created a Tensorflow Lite Image Classifier.");

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
                float prediction = doInference();
                outputNumber.setText(Float.toString(prediction));
            }

        });
    }
    /*
    //makes input and output into arrays and runs the tflite model!!!
    public float doInference(){
        "input shape is [1]"
        float[] inputVal = new float[1];
        inputVal[0] = Float.valueOf(inputString);


        //"output shape is [1][1]"
        float[][] outputVal = new float[1][1];

        //run the inference, giving it the input shape and the output is the output shape
        tflite.run(ProcessImage.getBitmapFromView(Camera.getImageView(), 32, 32), outputVal);

        //result
        float inferredValue = outputVal[0][0];
        return inferredValue;
    }
    */

    //gets the bitmap and sends it to the nn
    String classifyFrame(Bitmap bitmap) {
        if (tflite == null) {
            Log.e(TAG, "Image classifier has not been initialized; Skipped.");
            return "Uninitialized Classifier.";
        }
        convertBitmapToByteBuffer(ProcessImage.getBitmapFromView());

        //send processed bitmap to nn
        long startTime = SystemClock.uptimeMillis();
        tflite.run(imgData, labelProbArray);
        long endTime = SystemClock.uptimeMillis();
        Log.d(TAG, "Timecost to run model inference: " + Long.toString(endTime - startTime));

        // smooth the results
        applyFilter();

        // print the results
        String textToShow = printTopKLabels();
        textToShow = Long.toString(endTime - startTime) + "ms" + textToShow;
        return textToShow;
    }

    //converts bitmap to byte buffer
    private void convertBitmapToByteBuffer(Bitmap bitmap) {
        if (imgData == null) {
            return;
        }
        imgData.rewind();
        bitmap.getPixels(intValues, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        // Convert the image to floating point.
        int pixel = 0;
        long startTime = SystemClock.uptimeMillis();
        for (int i = 0; i < imageSizeX; ++i) {
            for (int j = 0; j < imageSizeY; ++j) {
                final int val = intValues[pixel++];
                imgData.putFloat((((val >> 16) & 0xFF)-IMAGE_MEAN)/IMAGE_STD);
                imgData.putFloat((((val >> 8) & 0xFF)-IMAGE_MEAN)/IMAGE_STD);
                imgData.putFloat((((val) & 0xFF)-IMAGE_MEAN)/IMAGE_STD);
            }
        }
        long endTime = SystemClock.uptimeMillis();
        Log.d(TAG, "Timecost to put values into ByteBuffer: " + Long.toString(endTime - startTime));
    }

    //not entirely certain what it does, but "Low pass filter `labelProbArray` into the first stage of the filter."
    void applyFilter(){
        int num_labels =  labelList.size();

        // Low pass filter `labelProbArray` into the first stage of the filter.
        for(int j=0; j<num_labels; ++j){
            filterLabelProbArray[0][j] += FILTER_FACTOR*(labelProbArray[0][j] -
                    filterLabelProbArray[0][j]);
        }
        // Low pass filter each stage into the next.
        for (int i=1; i<FILTER_STAGES; ++i){
            for(int j=0; j<num_labels; ++j){
                filterLabelProbArray[i][j] += FILTER_FACTOR*(
                        filterLabelProbArray[i-1][j] -
                                filterLabelProbArray[i][j]);

            }
        }

        // Copy the last stage filter output back to `labelProbArray`.
        for(int j=0; j<num_labels; ++j){
            labelProbArray[0][j] = filterLabelProbArray[FILTER_STAGES-1][j];
        }
    }

    // "Prints top-K labels, to be shown in UI as the results."
    private String printTopKLabels() {
        for (int i = 0; i < labelList.size(); ++i) {
            sortedLabels.add(
                    new AbstractMap.SimpleEntry<>(labelList.get(i), labelProbArray[0][i]));
            if (sortedLabels.size() > RESULTS_TO_SHOW) {
                sortedLabels.poll();
            }
        }
        String textToShow = "";
        final int size = sortedLabels.size();
        for (int i = 0; i < size; ++i) {
            Map.Entry<String, Float> label = sortedLabels.poll();
            textToShow = String.format("\n%s: %4.2f",label.getKey(),label.getValue()) + textToShow;
        }
        return textToShow;
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

    /** load label list (in assets) */
    private List<String> loadLabelList(Activity activity) throws IOException {
        List<String> labelList = new ArrayList<String>();
        BufferedReader reader =
                new BufferedReader(new InputStreamReader(activity.getAssets().open("flower_labels.csv")));
        String line;
        while ((line = reader.readLine()) != null) {
            labelList.add(line);
        }
        reader.close();
        return labelList;
    }
}
