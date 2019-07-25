package com.example.plantidentifier;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.View;

public class ProcessImage {
    //https://dev.to/pranavpandey/android-create-bitmap-from-a-view-3lck
    public static Bitmap getBitmapFromView(View view, int width, int height) {
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

    /*public static Bitmap resizeBitmap(Bitmap bitmap) {
        Bitmap resized = BitmapFactory.
    }*/
}
