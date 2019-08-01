package com.example.plantidentifier;
import org.tensorflow.lite.Interpreter;
import java.io.IOException;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.util.Scanner;
import java.io.FileReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.io.File;
import android.graphics.Bitmap;


import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.os.SystemClock;
import android.util.Log;
import android.widget.TextView;

import static android.content.ContentValues.TAG;

public class ClassifyImage {

    private static final String modelPath = "YEEES.tflite";

    private static final String labelPath = "FlowerLabels.txt";

    //dimensions of image
    protected static final int sizeX = 32;
    protected static final int sizeY = 32;
    protected static final int pixelSize = 1;
    public int[] sizeArray = new int[sizeX * sizeY];

    //labels from file
    private List<String> labels = null;

    //labels as a 2D array
    private float[][] labelProbArray = null;

    private MappedByteBuffer NNModel;

    public static String flowerType = "";

    public ClassifyImage (Activity activity) throws IOException {

        //load the tflite model
        NNModel = loadModel(activity);

        //create an instance of Interpreter
        //tflite = new Interpreter(NNModel);

        //load the labels
        labels = loadLabelList(activity);

        labelProbArray = new float[1][labels.size()];
        Log.e("ClassifyImage", "labelList size: " + labels.size());

    }

    public String classifyPlantType() throws IOException {

        if (Camera.chosenImageByteBuffer == null)
        {
            Log.e("ClassifyImage", "chosenImageByteBuffer = null");
        }
        else
        {
            Log.e("ClassifyImae", "chosenImageByteBuffer != null and is about to be resized");

            //format byte buffer
            //the following code has been edited and is from https://codelabs.developers.google.com/codelabs/tensorflow-for-poets/#0 (accessed July 29, 2019), under this license: https://www.apache.org/licenses/LICENSE-2.0
            Camera.chosenImageByteBuffer = ByteBuffer.allocateDirect(4 * sizeX * sizeY * pixelSize);

            Camera.chosenImageByteBuffer.order(ByteOrder.nativeOrder());
        }



        //run the model
        try (Interpreter tflite = new Interpreter(NNModel)) {

            //do we need this? also should this be here?
            //tflite.resizeInput(1, new int[9]);

            tflite.run(Camera.chosenImageByteBuffer, labelProbArray);

            tflite.close();
        }

        for (int ii = 0; ii < 103; ii++)
        {
            Log.e("ClassifyImage", "Testing " + Float.toString(ii) + ": " + Float.toString(labelProbArray[0][ii]));
        }
        //analyze the results
        //sort the data with the largest prob in position 3
        int maxProbLabel = 0;
        for (int ii = 0; ii < labelProbArray[0].length; ii++)
        {
            if (ii != 58 && ii!=56) {
                Log.e("ClassifyImage", "ii: " + Float.toString(ii));
                if (labelProbArray[0][ii] > labelProbArray[0][maxProbLabel]) {
                    maxProbLabel = ii;
                }
            }
        }

        Log.e("ClassifyImage", "maxProbLabel: " + maxProbLabel);

        //read text file to get label
        Log.e("ClassifyImage", "type: " + labels.get(maxProbLabel));
        Log.e("ClassifyImage", "Testing" + labels.get(6));
        flowerType = labels.get(maxProbLabel);

        Log.e("ClassifyImage", "flowerType: " + flowerType);

        return flowerType;
    }

    //loads the file of the tflite model
    //the following code has been edited and is from https://www.tensorflow.org/lite/models/image_classification/android (accessed July 29, 2019), under this license: https://www.apache.org/licenses/LICENSE-2.0
    private MappedByteBuffer loadModel(Activity activity) throws IOException {
        //get the file
        AssetFileDescriptor fileDescriptor = activity.getAssets().openFd(modelPath);
        //get info  about file
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();

        //returns all info about file
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    //loads the label list
    //the following code has been edited and is from https://codelabs.developers.google.com/codelabs/tensorflow-for-poets/#0 (accessed July 29, 2019), under this license: https://www.apache.org/licenses/LICENSE-2.0
    private List<String> loadLabelList(Activity activity) throws IOException {
        List<String> labels = new ArrayList<String>();
        String line;

        BufferedReader reader = new BufferedReader(new InputStreamReader(activity.getAssets().open(labelPath)));

        while ((line = reader.readLine()) != null) {
            labels.add(line);
        }
        reader.close();
        return labels;
    }

    public static String readLabel(int lineNum) throws IOException
    {
       File file = new File(labelPath);
       Scanner sc = new Scanner(file);

       String answer = "Error";

       for(int ii = 0; ii <= lineNum; ii ++) {
           answer = sc.nextLine();
       }
       return answer;
    }
}