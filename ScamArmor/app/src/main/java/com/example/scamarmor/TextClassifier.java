package com.example.scamarmor;

import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;

public class TextClassifier {

    private Interpreter tflite;

    public TextClassifier(AssetManager assetManager, String modelPath) throws IOException {
        tflite = new Interpreter(loadModelFile(assetManager, modelPath));
    }

    private MappedByteBuffer loadModelFile(AssetManager assetManager, String modelPath) throws IOException {
        AssetFileDescriptor fileDescriptor = assetManager.openFd(modelPath);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    public String classify(String text, SimpleTokenizer tokenizer, int maxLength) {
        // Preprocess and tokenize the text
        int[] inputText = preprocessAndTokenizeText(text, tokenizer, maxLength);

        // Make prediction
        float[][] output = new float[1][1];
        tflite.run(new int[][]{inputText}, output);

        // Interpret the output
        return output[0][0] > 0.5 ? "scam" : "legitimate";
    }

    private int[] preprocessAndTokenizeText(String text, SimpleTokenizer tokenizer, int maxLength) {
        int[] sequences = tokenizer.textsToSequences(text);
        return tokenizer.padSequences(sequences, maxLength);
    }
}
