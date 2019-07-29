package com.example.plantidentifier;


import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ProcessImage {

    private TextView textView;
    private ClassifyImage classifier;

    //https://dev.to/pranavpandey/android-create-bitmap-from-a-view-3lck
    public Bitmap getBitmapFromView(View view, int width, int height) {
        if (width > 0 && height > 0) {
            view.measure(View.MeasureSpec.makeMeasureSpec(convertDpToPixels(width),
                    View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(convertDpToPixels(height),
                            View.MeasureSpec.EXACTLY));
        }
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());

        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(),
                view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Drawable background = view.getBackground();

        if (background != null) {
            background.draw(canvas);
        }
        view.draw(canvas);

        return bitmap;
    }

    //Same as above source
    public static int convertDpToPixels(float dp) {
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dp, Resources.getSystem().getDisplayMetrics()));
    }

    //https://stackoverflow.com/questions/4837715/how-to-resize-a-bitmap-in-android
    public static Bitmap resizeBitmap(Bitmap bitmap) {
        byte[] imageAsBytes = getBytesFromBitmap(bitmap);
        //b is a _____ of the inputted bitmap.
        Bitmap b = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
        //create a scaled version of b
        Bitmap resizedImage = Bitmap.createScaledBitmap(b, 32, 32, false);

        return resizedImage;
    }

    //https://stackoverflow.com/questions/10513976/how-to-convert-image-into-byte-array-and-byte-array-to-base64-string-in-android
    public static byte[] getBytesFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        //!What is 70???
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
        return stream.toByteArray();
    }

    //tflite tutorial
    /** Classifies a frame from the preview stream. */
    private void classifyFrame() {
        if (classifier == null ) {
            textView.setText("Uninitialized Classifier or invalid context.");
            return;
        }
        Bitmap bitmap = Camera.getChosenImageBitmap();
        textView = (TextView) View.findViewById(R.id.resultTextView);
        String textToShow = classifier.classifyFrame(bitmap);
        bitmap.recycle();
        textView.setText(textToShow);
    }


    /** Connect the buttons to their event handler. */
    @Override
    public void onViewCreated(final View view) {
        textView = (TextView) view.findViewById(R.id.text);
    }

    /** Load the model and labels. */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        try {
            classifier = new ImageClassifier(getActivity());
        } catch (IOException e) {
            Log.e(TAG, "Failed to initialize an image classifier.");
        }
    }
}
