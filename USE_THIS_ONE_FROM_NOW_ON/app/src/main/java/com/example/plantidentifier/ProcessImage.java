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
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.tensorflow.lite.Interpreter;

public class ProcessImage {

    //Interpreter tflite;
    /*The following code has been taken from https://dev.to/pranavpandey/android-create-bitmap-from-a-view-3lck (accessed July 26, 2019)*/
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

    //The following code has been taken from https://github.com/pranavpandey/dynamic-utils/blob/df2fa843cd1ed0b9fd7c80e236bc99a40d546bba/dynamic-utils/src/main/java/com/pranavpandey/android/dynamic/utils/DynamicUnitUtils.java#L34 (Accessed July 26, 2019)
    public static int convertDpToPixels(float dp) {
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dp, Resources.getSystem().getDisplayMetrics()));
    }

    //The following code has been taken and modified from https://stackoverflow.com/questions/4837715/how-to-resize-a-bitmap-in-android (accessed July 26, 2019)
    public static Bitmap resizeBitmap(Bitmap bitmap) {
        byte[] imageAsBytes = getBytesFromBitmap(bitmap);
        //b is a _____ of the inputted bitmap.
        Bitmap b = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
        //create a scaled version of b
        Bitmap resizedImage = Bitmap.createScaledBitmap(b, 32, 32, false);

        return resizedImage;
    }

    //The following code has been taken from https://stackoverflow.com/questions/10513976/how-to-convert-image-into-byte-array-and-byte-array-to-base64-string-in-android (accessed July 29, 2019)
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
                //int green = Color.green(pixel);
                int blue = Color.blue(pixel);
                int alpha = Color.alpha(pixel);
                output[x][y] = (blue+red)/2;
                //Log.i("Colors", "("+red+", "+green+", "+blue+")");

                outputBitmap.setPixel(x, y, Color.argb(alpha, output[x][y], output[x][y], output[x][y]));
            }
        }

        return outputBitmap;
    }



    //The following code is taken and modified from https://github.com/ZZANZUPROJECT/TFLite-Object-Detection/blob/master/app/src/main/java/com/example/android/alarmapp/tflite/TensorFlowImageClassifier.java (accessed August 1, 2019)
    public static ByteBuffer scaledByteBuffer (Bitmap b) throws UnsupportedEncodingException {
        //dont know if this works
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4*4*32*32*1);
        byteBuffer.order(ByteOrder.nativeOrder());
        int[] intValues = new int[32*32];
        b.getPixels(intValues, 0, b.getWidth(), 0, 0, b.getWidth(), b.getHeight());
        int pixel = 0;
        for(int i = 0; i < 32; ++i){
            for(int j = 0; j < 32; ++j) {
                final int val = intValues[pixel++];
                byteBuffer.putFloat(((val >> 16) & 0xFF)/255.0f);
                byteBuffer.putFloat(((val >> 8) & 0xFF)/255.0f);
                byteBuffer.putFloat(((val) & 0xFF)/255.0f);
            }
        }

        //Log.e("ProcessImage", byteBuffer[0].toString());
        String converted = new String(byteBuffer.array(), "UTF-8");
        Log.e("ProcessImage", converted);

        return byteBuffer;
    }

    public static ByteBuffer preprocessImage(View view, int width, int height) throws UnsupportedEncodingException{
        Bitmap bitmap = getBitmapFromView(view, width, height);
        bitmap = resizeBitmap(bitmap);
        bitmap = grayscaleBitmap(bitmap);

        //ByteBuffer output = bitmapToByteBuffer(bitmap);
        ByteBuffer output = scaledByteBuffer(bitmap);

        return output;
    }


}
