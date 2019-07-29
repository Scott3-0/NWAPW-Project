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
    /* labels that allow you to classify the output */
    private List<String> labelList;

    //byteBuffer holds image data for the tflite file
    private ByteBuffer imgData = null;

    //holds the location of the tflite model
    private static final String modelPath = "androidModel.tflite";

    //location of labels
    private static final String labelPath = "flower_labels.csv";

    //image dimensions and size
    private static final int batchSize = 1;

    private static final int pixelSize = 3;

    static final int imageSizeX = 32;
    static final int imageSizeY = 32;

    private static final int IMAGE_MEAN = 128;
    private static final float IMAGE_STD = 128.0f;

    //array holds results from inference
    private float[][] labelProbArray = null;

    /**
     * multi-stage low pass filter
     **/
    private float[][] filterLabelProbArray = null;
    private static final int FILTER_STAGES = 3;
    private static final float FILTER_FACTOR = 0.4f;

    //array to store image dimensions
    private int[] intValues = new int[imageSizeX * imageSizeY];

    //how many results to show in UI
    private
    final int resultsToShow = 3;

    //need output as TextView
    TextView outputString;

    //declare interpreter
    private Interpreter tflite;
    private MappedByteBuffer tfliteModel;

    //constructor
    ClassifyImage(Activity activity) throws IOException {
        tfliteModel = loadModelFile(activity);
        tflite = new Interpreter(tfliteModel);
        labelList = loadLabelList(activity);
        imgData =
                ByteBuffer.allocateDirect(
                        4 * batchSize * imageSizeX * imageSizeY * pixelSize);
        imgData.order(ByteOrder.nativeOrder());
    }

    public static class Recognition {
        private final String id;
        private final String title;
        private final Float confidence;

        public Recognition(final String id, final String title, final Float confidence) {
            this.id = id;
            this.title = title;
            this.confidence = confidence;
        }

        public Float getConfidence() {
            return confidence;
        }
    }


    /**
     * load label list (in assets)
     */
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

    //"memory-map the model file in assets"
    private MappedByteBuffer loadModelFile(Activity activity) throws IOException {
        AssetFileDescriptor fileDescriptor = activity.getAssets().openFd("andriodModel.tflite");
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    //converts bitmap to byte buffer
    private void convertBitmapToByteBuffer(Bitmap bitmap) {
        imgData.rewind();
        bitmap.getPixels(intValues, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        // Convert the image to floating point.
        int pixel = 0;
        for (int i = 0; i < imageSizeX; ++i) {
            for (int j = 0; j < imageSizeY; ++j) {
                final int val = intValues[pixel++];
                imgData.put((byte) ((val >> 16) & 0xFF));
                imgData.put((byte) ((val >> 8) & 0xFF));
                imgData.put((byte) (val & 0xFF));
            }
        }
    }

    protected void runInference() {
        tflite.run(imgData, labelProbArray);
    }

    protected List<Recognition> recognizeImage(Bitmap bitmap) {
        convertBitmapToByteBuffer(bitmap);
        runInference();
        PriorityQueue<Recognition> pq =
                new PriorityQueue<Recognition>(
                        3,
                        new Comparator<Recognition>() {
                            @Override
                            public int compare(Recognition lhs, Recognition rhs) {
                                // Intentionally reversed to put high confidence at the head of the queue.
                                return Float.compare(rhs.getConfidence(), lhs.getConfidence());
                            }
                        });
        for (int i = 0; i < labelList.size(); ++i) {
            pq.add(
                    new Recognition(
                            "" + i,
                            labelList.size() > i ? labelList.get(i) : "unknown",
                            getNormalizedProbability(i),
                            null));
        }
        ArrayList<Recognition> recognitions = new ArrayList<Recognition>();
        for(int i = 0; i < pq.size(); ++i) {
            recognitions.add(pq.poll());
        }
        return recognitions;
    }

    protected float getNormalizedProbability(int labelIndex) {
        return (labelProbArray[0][labelIndex] & 0xff) / 255.0f;
    }
}