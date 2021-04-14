package com.example.places.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

public class UtilImage {

    public static Bitmap createImageFromPath(String path){
        return BitmapFactory.decodeFile(path);
    }

    public static Bitmap scaleBitmap(Bitmap image){

        return  Bitmap.createScaledBitmap(
                image, image.getWidth()/12, image.getHeight()/12, true
        );

    }

    public static Bitmap rotateBitmap(Bitmap thumbnail){

        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        return Bitmap.createBitmap(thumbnail, 0, 0, thumbnail.getWidth(), thumbnail.getHeight(), matrix, true);

    }

}
