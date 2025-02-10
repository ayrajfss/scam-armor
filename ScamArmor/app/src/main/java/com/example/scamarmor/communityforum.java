package com.example.scamarmor;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.view.ViewGroup;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.List;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.content.Context;
import android.view.LayoutInflater;


public class communityforum extends AppCompatActivity {

    EditText commentEditText;
    Button postButton;
    ListView commentListView;
    CommentAdapter commentAdapter; // Custom adapter
    CommentDAO commentDAO;
    LinearLayout adminOptions;
    Button deleteButton;
    private boolean isAdmin;

    private long selectedCommentId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.communityforum);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Community Forum");

        isAdmin = getIntent().getBooleanExtra("isAdmin", false);

        commentEditText = findViewById(R.id.commentEditText);
        postButton = findViewById(R.id.postButton);
        commentListView = findViewById(R.id.commentListView);
        adminOptions = findViewById(R.id.adminOptions); // Initialize adminOptions
        deleteButton = findViewById(R.id.deleteButton); // Initialize deleteButton

        if (!isAdmin) {
            adminOptions.setVisibility(View.GONE);
        }

        commentDAO = new CommentDAO(this);
        commentDAO.open();

        loadComments();

        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postComment();
            }
        });

        commentListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CommentDAO.Comment selectedComment = commentAdapter.getItem(position);
                selectedCommentId = selectedComment.getId();
                if (isAdmin) {
                    adminOptions.setVisibility(View.VISIBLE); // Show admin options when a comment is selected
                }
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedCommentId != -1) {
                    commentDAO.deleteComment(selectedCommentId);
                    loadComments();
                    adminOptions.setVisibility(View.GONE); // Hide admin options after deleting
                    Toast.makeText(communityforum.this, "Comment deleted", Toast.LENGTH_SHORT).show();
                }
            }
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.navigation_community_forum);

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            Intent intent;
            if (itemId == R.id.navigation_home) {
                intent = new Intent(this, MainActivity.class);
            } else if (itemId == R.id.navigation_scan_text) {
                intent = new Intent(this, scantext.class);
            } else if (itemId == R.id.navigation_community_forum) {
                return true;
            } else {
                return false;
            }
            intent.putExtra("isAdmin", isAdmin);
            startActivity(intent);
            return true;
        });
    }

    private void loadComments() {
        List<CommentDAO.Comment> comments = commentDAO.getAllComments();
        commentAdapter = new CommentAdapter(this, comments);
        commentListView.setAdapter(commentAdapter);
    }

    private void postComment() {
        String comment = commentEditText.getText().toString().trim();
        if (!comment.isEmpty()) {
            commentDAO.addComment(comment);
            loadComments();
            commentEditText.getText().clear();
            Toast.makeText(this, "Comment posted", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Please enter a comment", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        commentDAO.close();
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
        intent.putExtra("isAdmin", isAdmin);
        startActivity(intent);
        return true;
    }

    // Custom Adapter class for handling comments with profile pictures
    private class CommentAdapter extends ArrayAdapter<CommentDAO.Comment> {

        private final List<CommentDAO.Comment> comments;
        private final LayoutInflater inflater;
        private final Context context;

        public CommentAdapter(Context context, List<CommentDAO.Comment> comments) {
            super(context, 0, comments); // Use 0 as the resource ID, since we're customizing with list_item_comment.xml
            this.context = context;
            this.comments = comments;
            this.inflater = LayoutInflater.from(context);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                view = inflater.inflate(R.layout.list_item_comment, parent, false);
            }

            ImageView profileImageView = view.findViewById(R.id.profileImageView);
            TextView commentTextView = view.findViewById(R.id.commentTextView);
            TextView timestampTextView = view.findViewById(R.id.timestampTextView); // TextView for timestamp

            // Set profile image (replace R.drawable.default_profile_image with user-uploaded image)
            profileImageView.setImageResource(R.drawable.ccat);

            // Set comment text
            CommentDAO.Comment comment = comments.get(position);
            commentTextView.setText(comment.getComment());

            // Format and set the timestamp
            long timestamp = comment.getTimestamp();
            String formattedTimestamp = android.text.format.DateFormat.format("dd/MM/yyyy hh:mm:ss", new java.util.Date(timestamp)).toString();
            timestampTextView.setText(formattedTimestamp);

            return view;
        }
    }
}
