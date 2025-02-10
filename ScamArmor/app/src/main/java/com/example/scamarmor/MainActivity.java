package com.example.scamarmor;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private boolean isAdmin; // Add this line

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if the user is logged in
        boolean isLoggedIn = getIntent().getBooleanExtra("isLoggedIn", true);
        if (!isLoggedIn) {
            Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(loginIntent);
            finish();
            return;
        }

        // Retrieve the isAdmin value
        isAdmin = getIntent().getBooleanExtra("isAdmin", false);

        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("SCAM ARMOR");

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);

        // Set Home as selected by default
        bottomNavigationView.setSelectedItemId(R.id.navigation_home);

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            Intent intent;
            if (itemId == R.id.navigation_home) {
                // Stay on the Home activity
                return true;
            } else if (itemId == R.id.navigation_scan_text) {
                intent = new Intent(MainActivity.this, scantext.class);
                intent.putExtra("isAdmin", isAdmin); // Pass isAdmin to scantext
            } else if (itemId == R.id.navigation_community_forum) {
                intent = new Intent(MainActivity.this, communityforum.class);
                intent.putExtra("isAdmin", isAdmin);
            } else {
                return false;
            }
            startActivity(intent);
            return true;
        });
    }

    private void openWebsite(String url) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(browserIntent);
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
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
}
