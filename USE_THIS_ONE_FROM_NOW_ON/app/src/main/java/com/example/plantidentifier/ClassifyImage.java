package com.example.plantidentifier;

import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;

import org.tensorflow.lite.Interpreter;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;


public class ClassifyImage {
    Interpreter tflite;
    ByteBuffer byteBuffer = Camera.chosenImageByteBuffer; //irrelevant, just testing something

}
