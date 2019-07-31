package com.example.plantidentifier;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import org.tensorflow.lite.Interpreter;

public class ProcessImage {

    //Interpreter tflite;
    /*This was taken from a post */
    //https://dev.to/pranavpandey/android-create-bitmap-from-a-view-3lck
    public static Bitmap getBitmapFromView(View view, int width, int height) {

        if (width > 0 && height > 0) {
            view.measure(View.MeasureSpec.makeMeasureSpec(convertDpToPixels(width),
                    View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(convertDpToPixels(height),
                            View.MeasureSpec.EXACTLY));
        }
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        Bitmap bitmap = Bitmap.createBitmap(width,
                height, Bitmap.Config.ARGB_8888);
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

    public static Bitmap grayscaleBitmap(Bitmap bitmap) {
        int imgWidth = bitmap.getWidth();
        int imgHeight = bitmap.getHeight();
        int output[][]= new int[imgWidth][imgHeight];
        Bitmap outputBitmap = bitmap;

        for(int x = 0; x < imgWidth; x ++) {
            for(int y = 0; y < imgHeight; y ++) {
                //getPixel outputs hexadecimal ints
                int pixel = bitmap.getPixel(x, y);
                int red = Color.red(pixel);
                int green = Color.green(pixel);
                int blue = Color.blue(pixel);
                int alpha = Color.alpha(pixel);
                output[x][y] = blue+red/2;
                //Log.i("Colors", "("+red+", "+green+", "+blue+")");

                outputBitmap.setPixel(x, y, Color.argb(alpha, output[x][y], output[x][y], output[x][y]));
            }
        }

        return outputBitmap;
    }

    //convert a bitmap to a byteBuffer.
    //https://stackoverflow.com/questions/10191871/converting-bitmap-to-bytearray-android
    public static ByteBuffer bitmapToByteBuffer (Bitmap b) {
        int numBytes = b.getByteCount();
        Log.e("ProcessImage", "numBytes: " + Integer.toString(numBytes));
        ByteBuffer buffer = ByteBuffer.allocate(numBytes);
        b.copyPixelsToBuffer(buffer);

        return buffer;

    }

    //TODO: write this function
    public static ByteBuffer scaledByteBuffer (ByteBuffer b) {
        //dont know if this works
        byte[] arr = new byte[b.remaining()];
        b.get(arr, 0, arr.length);

        for(int i = 0; i < arr.length; i ++)
        {
            arr[i] /= 255;
        }

        ByteBuffer buf = ByteBuffer.wrap(arr);

        return buf; //change this
    }

    public static ByteBuffer preprocessImage(View view, int width, int height) {
        Bitmap bitmap = getBitmapFromView(view, width, height);
        bitmap = resizeBitmap(bitmap);
        bitmap = grayscaleBitmap(bitmap);

        ByteBuffer output = bitmapToByteBuffer(bitmap);
        output = scaledByteBuffer(output);

        return output;
    }


}
