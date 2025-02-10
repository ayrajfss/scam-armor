package com.example.scamarmor;

import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.graphics.Typeface;
import android.content.DialogInterface;
import androidx.appcompat.app.AlertDialog;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.HashSet;
import java.util.Set;

public class scantext extends AppCompatActivity {

    private boolean isAdmin; // Declare isAdmin as a class-level variable

    EditText messageEditText;
    Button scanButton;
    TextView resultTextView;

    // TensorFlow Lite model
    private Interpreter tflite;
    private static final String MODEL_PATH = "scamdetect_last.tflite"; // Ensure this is correct
    private static final int INPUT_SIZE = 24346; // Model input size
    private static final int BYTES_PER_CHANNEL = 4; // 4 bytes for float32
    private static final float THRESHOLD = 0.5f; // Adjust based on your model confidence threshold

    // Preprocessing components
    private Set<String> stopWords;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scantext);

        // Check if the user is logged in
        boolean isLoggedIn = getIntent().getBooleanExtra("isLoggedIn", true);
        if (!isLoggedIn) {
            Intent loginIntent = new Intent(scantext.this, LoginActivity.class);
            startActivity(loginIntent);
            finish();
            return;
        }

        // Retrieve the isAdmin value
        isAdmin = getIntent().getBooleanExtra("isAdmin", false);

        // Load toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Scan Text");

        // Initialize views
        messageEditText = findViewById(R.id.messageEditText);
        scanButton = findViewById(R.id.scanButton);
        resultTextView = findViewById(R.id.resultTextView);

        // Initialize TensorFlow Lite model
        try {
            tflite = new Interpreter(loadModelFile());
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Initialize stop words (add relevant stop words)
        stopWords = new HashSet<>();
        stopWords.add("a");
        stopWords.add("an");
        stopWords.add("the");
        // Add more stop words as needed

        // Button click listener
        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanTextMessage();
            }
        });

        // Bottom navigation setup
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.navigation_scan_text);

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            Intent intent;
            if (itemId == R.id.navigation_home) {
                intent = new Intent(this, MainActivity.class);
                intent.putExtra("isAdmin", isAdmin); // Pass isAdmin to MainActivity
                startActivity(intent);
                return true;
            } else if (itemId == R.id.navigation_scan_text) {
                return true;
            } else if (itemId == R.id.navigation_community_forum) {
                intent = new Intent(this, communityforum.class);
                intent.putExtra("isAdmin", isAdmin);
                startActivity(intent);
                return true;
            }
            return false;
        });
    }

    // Method to load TensorFlow Lite model from file
    private MappedByteBuffer loadModelFile() throws IOException {
        AssetFileDescriptor fileDescriptor = this.getAssets().openFd(MODEL_PATH);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    // Method to preprocess text (basic lemmatization example)
    private String preprocessText(String text) {
        // Convert text to lowercase
        text = text.toLowerCase();

        // Remove special characters and numbers
        text = text.replaceAll("[^a-zA-Z ]", "");

        // Tokenization (splitting text into words)
        String[] words = text.split("\\s+");

        // Remove stop words
        StringBuilder processedText = new StringBuilder();
        for (String word : words) {
            if (!stopWords.contains(word)) {
                processedText.append(lemmatize(word)).append(" ");
            }
        }

        return processedText.toString().trim();
    }

    // Method for basic lemmatization
    private String lemmatize(String word) {
        // Example lemmatization, replace with your own rules or use a library
        // Example mapping for English lemmatization
        switch (word) {
            case "running":
            case "ran":
                return "run";
            default:
                return word; // Default to returning the original word
        }
    }

    // Method to tokenize text and prepare input for TensorFlow Lite model
    private void tokenizeInputText(String text, float[] inputArray) {
        // Example implementation: tokenize text and fill inputArray
        char[] charArray = text.toCharArray();
        int index = 0;
        for (char c : charArray) {
            if (index >= INPUT_SIZE) {
                break;
            }
            inputArray[index++] = (float) c;
        }
        // Fill the rest of the array with zeros if text length is less than INPUT_SIZE
        while (index < INPUT_SIZE) {
            inputArray[index++] = 0f;
        }
    }

    // Method to handle text scanning and classification
    private void scanTextMessage() {
        String textMessage = messageEditText.getText().toString().trim();
        if (textMessage.isEmpty()) {
            // Display a Toast message if the input is empty
            Toast.makeText(this, "Please input the text message", Toast.LENGTH_SHORT).show();
            return; // Exit the method
        }
            String preprocessedText = preprocessText(textMessage);

            // Convert preprocessed text to input format for TensorFlow Lite model
            float[] inputArray = new float[INPUT_SIZE];
            tokenizeInputText(preprocessedText, inputArray);

            // Allocate buffer for input values and copy values into it
            ByteBuffer inputBuffer = ByteBuffer.allocateDirect(INPUT_SIZE * BYTES_PER_CHANNEL);
            inputBuffer.order(ByteOrder.nativeOrder());
            inputBuffer.rewind();
            for (float value : inputArray) {
                inputBuffer.putFloat(value);
            }

            // Run inference
            float[][] outputArray = new float[1][1]; // Adjust based on your model output shape
            tflite.run(inputBuffer, outputArray);

            // Process output and show classification result
            float confidence = outputArray[0][0];
            if (confidence > THRESHOLD) {
                String scamText = "The text is classified as SCAM.";
                SpannableString spannableString = new SpannableString(scamText);
                spannableString.setSpan(new StyleSpan(Typeface.BOLD), scamText.indexOf("SCAM"), scamText.indexOf("SCAM") + "SCAM".length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                resultTextView.setText(spannableString);
                showScamAlert();
            } else {
                String legitimateText = "The text is classified as LEGITIMATE.";
                SpannableString spannableString = new SpannableString(legitimateText);
                spannableString.setSpan(new StyleSpan(Typeface.BOLD), legitimateText.indexOf("LEGITIMATE"), legitimateText.indexOf("LEGITIMATE") + "LEGITIMATE".length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                resultTextView.setText(spannableString);
            }
        }

    // Method to show scam alert dialog
    private void showScamAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Scam Detected");
        builder.setMessage("The text is classified as a scam. It is recommended to report this message to the authorities and avoid sharing personal information.");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_item, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        Intent intent;
        if (itemId == R.id.item1) {
            Toast.makeText(this, "Malware Scams selected", Toast.LENGTH_SHORT).show();
            intent = new Intent(this, malware.class);
        } else if (itemId == R.id.item2) {
            Toast.makeText(this, "Phishing Scams selected", Toast.LENGTH_SHORT).show();
            intent = new Intent(this, phishing.class);
        } else if (itemId == R.id.item3) {
            Toast.makeText(this, "Investment selected", Toast.LENGTH_SHORT).show();
            intent = new Intent(this, investment.class);
        } else if (itemId == R.id.item4) {
            Toast.makeText(this, "Cash Reward Scams selected", Toast.LENGTH_SHORT).show();
            intent = new Intent(this, cashreward.class);
        } else {
            return super.onOptionsItemSelected(item);
        }
        intent.putExtra("isAdmin", isAdmin); // Use the class-level variable here
        startActivity(intent);
        return true;
    }

    @Override
    protected void onDestroy() {
        if (tflite != null) {
            tflite.close();
        }
        super.onDestroy();
    }
}
