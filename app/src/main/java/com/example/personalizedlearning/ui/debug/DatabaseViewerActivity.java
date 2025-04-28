package com.example.personalizedlearning.ui.debug;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.personalizedlearning.R;
import com.example.personalizedlearning.db.DatabaseHelper;

public class DatabaseViewerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database_viewer);

        TextView databaseContents = findViewById(R.id.database_contents);
        DatabaseHelper dbHelper = DatabaseHelper.getInstance(this);
        
        // Get and display database contents
        String contents = dbHelper.getDatabaseContents();
        databaseContents.setText(contents);
    }
} 