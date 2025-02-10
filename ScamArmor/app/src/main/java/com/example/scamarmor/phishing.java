package com.example.scamarmor;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class phishing extends AppCompatActivity {

    private boolean isAdmin; // Declare isAdmin as a class-level variable

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.phishing);

        // Retrieve the isAdmin value
        isAdmin = getIntent().getBooleanExtra("isAdmin", false);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Remove default title text
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Access the TextView in the custom toolbar and set its text
        TextView toolbarTitle = toolbar.findViewById(R.id.toolbarTitle);
        toolbarTitle.setText("PHISHING SCAMS");

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);

        // Clear bottom navigation selection
        bottomNavigationView.getMenu().setGroupCheckable(0, true, false);
        for (int i = 0; i < bottomNavigationView.getMenu().size(); i++) {
            bottomNavigationView.getMenu().getItem(i).setChecked(false);
        }
        bottomNavigationView.getMenu().setGroupCheckable(0, true, true);

        // Set click listener on the home button
        toolbar.setNavigationOnClickListener(view -> {
            Intent intent = new Intent(phishing.this, MainActivity.class);
            intent.putExtra("isAdmin", isAdmin); // Pass isAdmin to MainActivity
            startActivity(intent);
        });

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            Intent intent;
            if (itemId == R.id.navigation_home) {
                intent = new Intent(phishing.this, MainActivity.class);
                intent.putExtra("isAdmin", isAdmin); // Pass isAdmin to MainActivity
            } else if (itemId == R.id.navigation_scan_text) {
                intent = new Intent(phishing.this, scantext.class);
                intent.putExtra("isAdmin", isAdmin); // Pass isAdmin to scantext
            } else if (itemId == R.id.navigation_community_forum) {
                intent = new Intent(phishing.this, communityforum.class);
                intent.putExtra("isAdmin", isAdmin); // Pass isAdmin to communityforum
            } else {
                return false;
            }
            startActivity(intent);
            return true;
        });
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
            showToast("Malware Scams selected");
            intent = new Intent(this, malware.class);
        } else if (itemId == R.id.item2) {
            showToast("Phishing Scams selected");
            intent = new Intent(this, phishing.class);
        } else if (itemId == R.id.item3) {
            showToast("Investment selected");
            intent = new Intent(this, investment.class);
        } else if (itemId == R.id.item4) {
            showToast("Cash Reward Scams selected");
            intent = new Intent(this, cashreward.class);
        } else {
            return super.onOptionsItemSelected(item);
        }
        intent.putExtra("isAdmin", isAdmin); // Pass isAdmin to the selected activity
        startActivity(intent);
        return true;
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
