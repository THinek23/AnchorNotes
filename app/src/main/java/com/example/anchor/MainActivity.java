package com.example.anchor;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {
    static final ReminderController reminderController = new ReminderController();
    static ReminderScheduler reminderScheduler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FloatingActionButton fab = findViewById(R.id.fabAddNote);
        fab.setOnClickListener(this::onClickToCreateNote);
        reminderScheduler = new ReminderScheduler(this);
        // TODO check where to create and store controller objects
        // TODO set relevantStore and noteStore to ReminderController
    }
    public void onClickToCreateNote(View view) {
        Intent intent = new Intent(this, NoteCreationActivity.class);
        startActivity(intent);

    }
}

