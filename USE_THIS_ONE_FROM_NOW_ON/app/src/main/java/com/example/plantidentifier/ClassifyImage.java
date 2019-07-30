package com.example.plantidentifier;
import org.tensorflow.lite.Interpreter;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
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
import android.graphics.Bitmap;


import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.os.SystemClock;
import android.util.Log;
import android.widget.TextView;

import static android.content.ContentValues.TAG;

public class ClassifyImage {

    private static final String modelPath = "androidModel.tflite";

    private static final String labelPath = "flower_labels.csv";

    //dimensions of image
    private static final int sizeX = 32;
    private static final int sizeY = 32;
    private static final int pixelSize = 3;

    //labels as a 2D array
    private float[][] labelProbArray = null;

    //labels from file
    private List<String> labels= null;

    private Interpreter tflite;

    private MappedByteBuffer NNModel;


    //constructor
    protected ClassifyImage(Activity activity) throws IOException {

        //load the tflite model
        NNModel = loadModel(activity);

        //create an instance of Interpreter
        tflite = new Interpreter(NNModel);

        //load the labels
        labels = loadLabelList(activity);


        //format bitmap into byte buffer
        //the following code has been edited and is from https://codelabs.developers.google.com/codelabs/tensorflow-for-poets/#0 (accessed July 29, 2019), under this license: https://www.apache.org/licenses/LICENSE-2.0
        Camera.chosenImageByteBuffer = ByteBuffer.allocateDirect(4 * sizeX * sizeY * pixelSize);
        Camera.chosenImageByteBuffer.order(ByteOrder.nativeOrder());

        //run the model
        tflite.run(Camera.chosenImageByteBuffer, labelProbArray);

        //analyze the results

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
}