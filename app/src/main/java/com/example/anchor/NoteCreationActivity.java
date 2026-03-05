package com.example.anchor;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.net.URI;
import java.time.Instant;
import java.util.UUID;

public class NoteCreationActivity extends AppCompatActivity {

    private NoteController noteController;
    private Note currentNote;

    // UI Components
    private TextView btnBack;
    private TextView btnSave;
    private EditText noteTitleEditText;
    private EditText noteBodyEditText;

    // Action buttons
    private TextView btnPin;
    private TextView btnTags;
    private TextView btnChecklist;
    private TextView btnReminder;
    private TextView btnLocation;
    private TextView btnAttach;

    // Sections
    private LinearLayout checklistSection;
    private LinearLayout attachmentsSection;
    private LinearLayout tagsSection;
    private LinearLayout remindersSection;
    private LinearLayout locationContainer;

    // Recycler views
    private RecyclerView checklistRecyclerView;
    private RecyclerView attachmentsRecyclerView;

    // Chip groups
    private ChipGroup tagsChipGroup;

    // Reminder/Location views
    private LinearLayout timeReminderContainer;
    private LinearLayout geofenceReminderContainer;
    private TextView timeReminderText;
    private TextView geofenceReminderText;
    private TextView locationText;
    private TextView btnRemoveTimeReminder;
    private TextView btnRemoveGeofenceReminder;
    private TextView btnRemoveLocation;
    private TextView btnAddChecklistItem;

    // State
    private boolean isPinned = false;
    private boolean hasPlace = false; // <- NEW: toggle-able Place state

    // Pickers
    private ActivityResultLauncher<Intent> pickPhotoLauncher;
    private ActivityResultLauncher<Intent> pickAudioLauncher;
    private RecyclerView.Adapter<?> attachmentsAdapter;

    // Audio playback
    private android.media.MediaPlayer currentMediaPlayer;
    private int currentPlayingPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_creation);

        // Initialize controller
        noteController = new NoteController();
        noteController.noteStore = new NoteStore(this);
        noteController.tagStore = new TagStore();
        noteController.mediaStore = new MediaStore();
        noteController.locationService = new LocationService();
        noteController.reminderScheduler = MainActivity.reminderScheduler;

        // Initialize views
        initializeViews();

        // Create a new blank note
        currentNote = noteController.createBlank("");

        // Initial render of action states
        renderPin(isPinned);
        renderPlace(hasPlace);

        // Setup listeners
        setupListeners();

        // Register activity result launchers
        registerPickers();
    }

    private void initializeViews() {
        // Toolbar buttons
        btnBack = findViewById(R.id.btnBack);
        btnSave = findViewById(R.id.btnSave);

        // Edit fields
        noteTitleEditText = findViewById(R.id.noteTitleEditText);
        noteBodyEditText = findViewById(R.id.noteBodyEditText);

        // Action buttons
        btnPin = findViewById(R.id.btnPin);
        btnTags = findViewById(R.id.btnTags);
        btnChecklist = findViewById(R.id.btnChecklist);
        btnReminder = findViewById(R.id.btnReminder);
        btnLocation = findViewById(R.id.btnLocation);
        btnAttach = findViewById(R.id.btnAttach);

        // Sections
        checklistSection = findViewById(R.id.checklistSection);
        attachmentsSection = findViewById(R.id.attachmentsSection);
        tagsSection = findViewById(R.id.tagsSection);
        remindersSection = findViewById(R.id.remindersSection);
        locationContainer = findViewById(R.id.locationContainer);

        // RecyclerViews
        checklistRecyclerView = findViewById(R.id.checklistRecyclerView);
        attachmentsRecyclerView = findViewById(R.id.attachmentsRecyclerView);

        checklistRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        attachmentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        attachmentsAdapter = new AttachmentAdapter();
        attachmentsRecyclerView.setAdapter(attachmentsAdapter);

        // Chip groups
        tagsChipGroup = findViewById(R.id.tagsChipGroup);

        // Reminder/Location views
        timeReminderContainer = findViewById(R.id.timeReminderContainer);
        geofenceReminderContainer = findViewById(R.id.geofenceReminderContainer);
        timeReminderText = findViewById(R.id.timeReminderText);
        geofenceReminderText = findViewById(R.id.geofenceReminderText);
        locationText = findViewById(R.id.locationText);
        btnRemoveTimeReminder = findViewById(R.id.btnRemoveTimeReminder);
        btnRemoveGeofenceReminder = findViewById(R.id.btnRemoveGeofenceReminder);
        btnRemoveLocation = findViewById(R.id.btnRemoveLocation);
        btnAddChecklistItem = findViewById(R.id.btnAddChecklistItem);
    }

    private void setupListeners() {
        // Back button (return to notes list)
        btnBack.setOnClickListener(v -> finish());

        // Save button
        btnSave.setOnClickListener(v -> saveNote());

        // Pin button (toggle)
        btnPin.setOnClickListener(v -> togglePin());

        // Tags button
        btnTags.setOnClickListener(v -> toggleTagsSection());

        // Checklist button
        btnChecklist.setOnClickListener(v -> toggleChecklistSection());

        // Reminder button
        btnReminder.setOnClickListener(v -> showReminderOptions());

        // Place button (toggle on/off rather than add-only)
        btnLocation.setOnClickListener(v -> togglePlace());

        // Attach button
        btnAttach.setOnClickListener(v -> showAttachmentOptions());

        // Add checklist item
        btnAddChecklistItem.setOnClickListener(v -> addChecklistItem());

        // Remove reminder buttons
        btnRemoveTimeReminder.setOnClickListener(v -> removeTimeReminder());
        btnRemoveGeofenceReminder.setOnClickListener(v -> removeGeofenceReminder());

        // Remove location chip/section
        btnRemoveLocation.setOnClickListener(v -> {
            if (hasPlace) {
                removeLocation();
                hasPlace = false;
                renderPlace(false);
            }
        });
    }

    // ----- RENDER HELPERS -----

    private void renderPin(boolean selected) {
        // Two-line label so “Pinned” won’t cut off under the emoji
        btnPin.setText(selected ? "📌\nPinned" : "📌\nPin");
        btnPin.setSelected(selected); // drives bg_action_item.xml if you added it

        // Fallback/text color emphasis (works even without drawable)
        int color = ContextCompat.getColor(this,
                selected ? R.color.actionTextSelected : R.color.actionText);
        btnPin.setTextColor(color);
    }

    private void renderPlace(boolean selected) {
        btnLocation.setText(selected ? "📍\nPlace (On)" : "📍\nPlace");
        btnLocation.setSelected(selected);

        int color = ContextCompat.getColor(this,
                selected ? R.color.actionTextSelected : R.color.actionText);
        btnLocation.setTextColor(color);

        // Show/hide the visible location pill/section
        locationContainer.setVisibility(selected ? View.VISIBLE : View.GONE);

        // Optional: tie into the reminder section UI if you want
        if (!selected) {
            // When turning off, hide any geofence reminder UI that depends on place
            geofenceReminderContainer.setVisibility(View.GONE);
        }
    }

    // ----- SAVE -----

    private void saveNote() {
        String title = noteTitleEditText.getText().toString().trim();
        String body = noteBodyEditText.getText().toString().trim();

        if (title.isEmpty() && body.isEmpty()) {
            Toast.makeText(this, "Note is empty", Toast.LENGTH_SHORT).show();
            return;
        }

        currentNote.title = title.isEmpty() ? "Untitled" : title;
        currentNote.setBodyHTML(body);

        // Persist pin & place state (if your Note model supports it)
        // Example:
        // currentNote.isPinned = isPinned;
        // if (hasPlace) noteController.setCurrentLocation(currentNote); else noteController.removeLocation(currentNote);

        noteController.save(currentNote);
        Toast.makeText(this, "Note saved successfully", Toast.LENGTH_SHORT).show();
        finish();
    }

    // ----- PIN -----

    private void togglePin() {
        isPinned = !isPinned;

        // Update model (delegates to your existing controller)
        noteController.togglePin(currentNote);

        // Refresh UI
        renderPin(isPinned);

        Toast.makeText(this, isPinned ? "Note pinned" : "Note unpinned", Toast.LENGTH_SHORT).show();
    }

    // ----- TAGS -----

    private void toggleTagsSection() {
        if (tagsSection.getVisibility() == View.GONE) {
            tagsSection.setVisibility(View.VISIBLE);
            loadTags();
        } else {
            tagsSection.setVisibility(View.GONE);
        }
    }

    private void loadTags() {
        tagsChipGroup.removeAllViews();

        // Sample tags - in real app, load from TagController
        String[] sampleTags = {"Work", "Personal", "Ideas", "Important", "Meeting"};
        int[] tagColors = {
                android.R.color.holo_blue_light,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light,
                android.R.color.holo_purple
        };

        for (int i = 0; i < sampleTags.length; i++) {
            Chip chip = new Chip(this);
            chip.setText(sampleTags[i]);
            chip.setCheckable(true);
            chip.setChipBackgroundColorResource(tagColors[i]);

            final long tagId = i + 1;
            chip.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    noteController.addTag(currentNote, tagId);
                } else {
                    noteController.removeTag(currentNote, tagId);
                }
            });

            tagsChipGroup.addView(chip);
        }
    }

    // ----- CHECKLIST -----

    private void toggleChecklistSection() {
        if (checklistSection.getVisibility() == View.GONE) {
            checklistSection.setVisibility(View.VISIBLE);
            addChecklistItem(); // Add first item automatically
        } else {
            checklistSection.setVisibility(View.GONE);
        }
    }

    private void addChecklistItem() {
        String uuid = UUID.randomUUID().toString();
        currentNote.addChecklistItem(uuid, "", false, currentNote.checklist.size());
        // In real app, update RecyclerView adapter
        Toast.makeText(this, "Checklist item added", Toast.LENGTH_SHORT).show();
    }

    // ----- REMINDERS -----

    private void showReminderOptions() {
        if (remindersSection.getVisibility() == View.GONE) {
            remindersSection.setVisibility(View.VISIBLE);
            setTimeReminder(); // default demo
        } else {
            remindersSection.setVisibility(View.GONE);
        }
    }

    private void setTimeReminder() {
        Instant tomorrow = Instant.now().plusSeconds(86400);
        noteController.setTimeReminder(currentNote, tomorrow, null);

        timeReminderContainer.setVisibility(View.VISIBLE);
        timeReminderText.setText("Tomorrow at 2:00 PM");

        Toast.makeText(this, "Time reminder set", Toast.LENGTH_SHORT).show();
    }

    private void removeTimeReminder() {
        noteController.clearTimeReminder(currentNote);
        timeReminderContainer.setVisibility(View.GONE);
        Toast.makeText(this, "Time reminder removed", Toast.LENGTH_SHORT).show();
    }

    private void removeGeofenceReminder() {
        noteController.clearGeofenceReminder(currentNote);
        geofenceReminderContainer.setVisibility(View.GONE);
        Toast.makeText(this, "Location reminder removed", Toast.LENGTH_SHORT).show();
    }

    // ----- PLACE (toggle) -----

    private void togglePlace() {
        if (!hasPlace) {
            setCurrentLocation();   // turns it on
            hasPlace = true;
            renderPlace(true);
        } else {
            removeLocation();       // turns it off
            hasPlace = false;
            renderPlace(false);
        }
    }

    private void setCurrentLocation() {
        noteController.setCurrentLocation(currentNote);
        locationContainer.setVisibility(View.VISIBLE);
        locationText.setText("Current Location");
        Toast.makeText(this, "Location added", Toast.LENGTH_SHORT).show();
    }

    private void removeLocation() {
        noteController.removeLocation(currentNote);
        locationContainer.setVisibility(View.GONE);
        Toast.makeText(this, "Location removed", Toast.LENGTH_SHORT).show();
    }

    // ----- ATTACHMENTS -----

    private void showAttachmentOptions() {
        // Show a chooser to attach either a photo or an audio file
        String[] options = new String[]{"Attach Photo", "Attach Audio"};
        new AlertDialog.Builder(this)
                .setTitle("Add attachment")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                        intent.setType("image/*");
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
                        pickPhotoLauncher.launch(intent);
                    } else if (which == 1) {
                        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                        intent.setType("audio/*");
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
                        pickAudioLauncher.launch(intent);
                    }
                })
                .show();
    }

    private void registerPickers() {
        // Handles result for picking a photo and attaches it to the current note
        pickPhotoLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    try {
                        if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                            Uri contentUri = result.getData().getData();
                            if (contentUri == null) {
                                Toast.makeText(this, "Failed to get file", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            if (currentNote == null) {
                                Toast.makeText(this, "Note not available", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            // Persist permission for long-term access
                            try {
                                final int flags = result.getData().getFlags() & (Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                if (flags != 0) {
                                    getContentResolver().takePersistableUriPermission(contentUri, flags);
                                }
                            } catch (SecurityException e) {
                                // Permission might not be persistable, that's okay
                            } catch (Exception e) {
                                // Continue anyway
                            }

                            String mime = null;
                            try {
                                mime = getContentResolver().getType(contentUri);
                            } catch (Exception e) {
                                // Continue with null mime
                            }

                            if (mime == null || !mime.startsWith("image/")) {
                                Toast.makeText(this, "Unsupported file. Please select an image.", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            try {
                                URI uri = URI.create(contentUri.toString());
                                noteController.attachPhoto(currentNote, uri, mime, null, null);

                                // Set noteId on attachment
                                if (currentNote.attachments != null && !currentNote.attachments.isEmpty()) {
                                    Attachment lastAttachment = currentNote.attachments.get(currentNote.attachments.size() - 1);
                                    if (lastAttachment != null) {
                                        lastAttachment.noteId = currentNote.id;
                                    }
                                }

                                refreshAttachmentsUI();
                                Toast.makeText(this, "Photo attached", Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
                                Toast.makeText(this, "Failed to attach photo: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    } catch (Exception e) {
                        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
        );

        // Handles result for picking an audio file and attaches it to the current note
        pickAudioLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    try {
                        if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                            Uri contentUri = result.getData().getData();
                            if (contentUri == null) {
                                Toast.makeText(this, "Failed to get file", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            if (currentNote == null) {
                                Toast.makeText(this, "Note not available", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            try {
                                final int flags = result.getData().getFlags() & (Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                if (flags != 0) {
                                    getContentResolver().takePersistableUriPermission(contentUri, flags);
                                }
                            } catch (SecurityException e) {
                                // Permission might not be persistable, that's okay
                            } catch (Exception e) {
                                // Continue anyway
                            }

                            String mime = null;
                            try {
                                mime = getContentResolver().getType(contentUri);
                            } catch (Exception e) {
                                // Continue with null mime
                            }

                            if (mime == null || !mime.startsWith("audio/")) {
                                Toast.makeText(this, "Unsupported file. Please select an audio file.", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            try {
                                URI uri = URI.create(contentUri.toString());
                                Long durationMs = null;

                                // Try to get duration if possible
                                try {
                                    android.media.MediaMetadataRetriever mmr = new android.media.MediaMetadataRetriever();
                                    mmr.setDataSource(this, contentUri);
                                    String d = mmr.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_DURATION);
                                    if (d != null) {
                                        durationMs = Long.parseLong(d);
                                    }
                                    mmr.release();
                                } catch (Exception e) {
                                    // Duration is optional, continue
                                }

                                noteController.attachAudio(currentNote, uri, mime, durationMs);

                                // Set noteId on attachment
                                if (currentNote.attachments != null && !currentNote.attachments.isEmpty()) {
                                    Attachment lastAttachment = currentNote.attachments.get(currentNote.attachments.size() - 1);
                                    if (lastAttachment != null) {
                                        lastAttachment.noteId = currentNote.id;
                                    }
                                }

                                refreshAttachmentsUI();
                                Toast.makeText(this, "Audio attached", Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
                                Toast.makeText(this, "Failed to attach audio: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    } catch (Exception e) {
                        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
        );
    }

    private void refreshAttachmentsUI() {
        // Ensure UI updates happen on main thread
        if (android.os.Looper.myLooper() == android.os.Looper.getMainLooper()) {
            refreshAttachmentsUIInternal();
        } else {
            runOnUiThread(this::refreshAttachmentsUIInternal);
        }
    }

    private void refreshAttachmentsUIInternal() {
        try {
            // Reveal the attachments section if the note contains attachments and
            // ask the adapter to rebind the list
            if (currentNote != null && currentNote.attachments != null && !currentNote.attachments.isEmpty()) {
                if (attachmentsSection != null) {
                    attachmentsSection.setVisibility(View.VISIBLE);
                }
            }
            if (attachmentsAdapter != null) {
                attachmentsAdapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            android.util.Log.e("NoteCreation", "Error refreshing attachments UI: " + e.getMessage(), e);
        }
    }

    private class AttachmentAdapter extends RecyclerView.Adapter<AttachmentViewHolder> {
        @Override
        public AttachmentViewHolder onCreateViewHolder(android.view.ViewGroup parent, int viewType) {
            android.view.View view = android.view.LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.attachment, parent, false);
            return new AttachmentViewHolder(view);
        }

        @Override
        public void onBindViewHolder(AttachmentViewHolder holder, int position) {
            try {
                if (currentNote == null || currentNote.attachments == null || position >= currentNote.attachments.size()) {
                    return;
                }

                Attachment a = currentNote.attachments.get(position);
                if (a == null) {
                    return;
                }

                if (a.isPhoto() && a.uri != null) {
                    // Show photo thumbnail
                    if (holder.thumbnail != null) {
                        holder.thumbnail.setVisibility(View.VISIBLE);
                    }
                    if (holder.audioOverlay != null) {
                        holder.audioOverlay.setVisibility(View.GONE);
                    }
                    try {
                        if (holder.thumbnail != null) {
                            holder.thumbnail.setImageURI(Uri.parse(a.uri.toString()));
                        }
                    } catch (Exception e) {
                        if (holder.thumbnail != null) {
                            holder.thumbnail.setVisibility(View.GONE);
                        }
                    }
                } else if (a.isAudio()) {
                    // Show audio player overlay
                    try {
                        if (holder.thumbnail != null) {
                            holder.thumbnail.setVisibility(View.VISIBLE);
                            holder.thumbnail.setImageResource(android.R.color.transparent);
                            holder.thumbnail.setClickable(false);
                            holder.thumbnail.setFocusable(false);
                        }
                        if (holder.audioOverlay != null) {
                            holder.audioOverlay.setVisibility(View.VISIBLE);
                            holder.audioOverlay.setClickable(true);
                            holder.audioOverlay.setFocusable(true);
                        }
                    } catch (Exception e) {
                        android.util.Log.e("NoteCreation", "Error setting up audio overlay: " + e.getMessage(), e);
                    }

                    // Show duration if available
                    if (holder.audioDuration != null) {
                        try {
                            if (a instanceof AudioAttachment) {
                                AudioAttachment audio = (AudioAttachment) a;
                                if (audio.durationMs > 0) {
                                    long seconds = audio.durationMs / 1000;
                                    long minutes = seconds / 60;
                                    seconds = seconds % 60;
                                    holder.audioDuration.setText(String.format("%d:%02d", minutes, seconds));
                                } else {
                                    holder.audioDuration.setText("Voice Memo");
                                }
                            } else {
                                holder.audioDuration.setText("Voice Memo");
                            }
                        } catch (Exception e) {
                            holder.audioDuration.setText("Voice Memo");
                        }
                    }

                    // Set up play button and overlay click handlers
                    try {
                        if (holder.playButton != null) {
                            // Update button icon based on playing state
                            try {
                                boolean isPlaying = false;
                                if (currentPlayingPosition == position && currentMediaPlayer != null) {
                                    try {
                                        isPlaying = currentMediaPlayer.isPlaying();
                                    } catch (IllegalStateException e) {
                                        isPlaying = false;
                                    }
                                }
                                holder.playButton.setImageResource(isPlaying ? android.R.drawable.ic_media_pause : android.R.drawable.ic_media_play);
                            } catch (Exception e) {
                                android.util.Log.e("NoteCreation", "Error setting button icon: " + e.getMessage(), e);
                                try {
                                    holder.playButton.setImageResource(android.R.drawable.ic_media_play);
                                } catch (Exception ex) {
                                    // Ignore
                                }
                            }

                            holder.playButton.setVisibility(View.VISIBLE);
                            holder.playButton.setClickable(true);
                            holder.playButton.setEnabled(true);
                            holder.playButton.setFocusable(true);
                            holder.playButton.setFocusableInTouchMode(true);

                            holder.playButton.setOnClickListener(null);

                            final int safePosition = position;
                            final Attachment safeAttachment = a;
                            holder.playButton.setOnClickListener(v -> {
                                android.util.Log.d("NoteCreation", "Play button clicked for position: " + safePosition);
                                try {
                                    toggleAudioPlayback(safePosition, safeAttachment);
                                } catch (Exception e) {
                                    android.util.Log.e("NoteCreation", "Error in play button click: " + e.getMessage(), e);
                                    runOnUiThread(() -> {
                                        Toast.makeText(NoteCreationActivity.this, "Error playing audio", Toast.LENGTH_SHORT).show();
                                    });
                                }
                            });
                        }

                        // Also make the entire overlay clickable as fallback
                        if (holder.audioOverlay != null) {
                            final int safePosition = position;
                            final Attachment safeAttachment = a;
                            holder.audioOverlay.setOnClickListener(v -> {
                                android.util.Log.d("NoteCreation", "Audio overlay clicked for position: " + safePosition);
                                try {
                                    toggleAudioPlayback(safePosition, safeAttachment);
                                } catch (Exception e) {
                                    android.util.Log.e("NoteCreation", "Error in overlay click: " + e.getMessage(), e);
                                    runOnUiThread(() -> {
                                        Toast.makeText(NoteCreationActivity.this, "Error playing audio", Toast.LENGTH_SHORT).show();
                                    });
                                }
                            });
                        }
                    } catch (Exception e) {
                        android.util.Log.e("NoteCreation", "Error setting up click handlers: " + e.getMessage(), e);
                    }
                } else {
                    // Hide unknown attachment types
                    if (holder.itemView != null) {
                        holder.itemView.setVisibility(View.GONE);
                        android.view.ViewGroup.LayoutParams params = holder.itemView.getLayoutParams();
                        if (params != null) {
                            params.height = 0;
                            holder.itemView.setLayoutParams(params);
                        }
                    }
                }
            } catch (Exception e) {
                // Silently handle binding errors
            }
        }

        @Override
        public int getItemCount() {
            return currentNote != null && currentNote.attachments != null ? currentNote.attachments.size() : 0;
        }
    }

    private static class AttachmentViewHolder extends RecyclerView.ViewHolder {
        android.widget.ImageView thumbnail;
        android.view.ViewGroup audioOverlay;
        TextView audioDuration;
        android.widget.ImageButton playButton;

        AttachmentViewHolder(android.view.View itemView) {
            super(itemView);
            this.thumbnail = itemView.findViewById(R.id.attachmentThumbnail);
            this.audioOverlay = itemView.findViewById(R.id.audioPlayerOverlay);
            this.audioDuration = itemView.findViewById(R.id.audioDuration);
            this.playButton = itemView.findViewById(R.id.audioPlayButton);
        }
    }

    // ----- AUDIO PLAYBACK -----

    private void toggleAudioPlayback(int position, Attachment attachment) {
        try {
            // Prevent multiple simultaneous calls
            if (currentMediaPlayer != null && currentPlayingPosition == -1) {
                return;
            }

            // If clicking the same audio that's currently playing, pause it
            if (currentPlayingPosition == position && currentMediaPlayer != null) {
                try {
                    boolean isPlaying = false;
                    try {
                        isPlaying = currentMediaPlayer.isPlaying();
                    } catch (IllegalStateException e) {
                        android.util.Log.w("NoteCreation", "MediaPlayer in wrong state, stopping");
                        stopAudio();
                        return;
                    }

                    if (isPlaying) {
                        pauseAudio();
                    } else {
                        resumeAudio();
                    }
                } catch (Exception e) {
                    android.util.Log.e("NoteCreation", "Error toggling playback: " + e.getMessage(), e);
                    stopAudio();
                }
                return;
            }

            // Stop any currently playing audio
            if (currentMediaPlayer != null) {
                stopAudio();
            }

            // Reset position before starting new playback
            currentPlayingPosition = -1;

            // Start playing the selected audio
            if (attachment.uri != null) {
                playAudio(position, attachment.uri);
            } else {
                Toast.makeText(this, "Audio file not available", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            android.util.Log.e("NoteCreation", "Error in toggleAudioPlayback: " + e.getMessage(), e);
            Toast.makeText(this, "Failed to play audio", Toast.LENGTH_SHORT).show();
            stopAudio();
        }
    }

    private void resumeAudio() {
        try {
            if (currentMediaPlayer != null) {
                try {
                    if (!currentMediaPlayer.isPlaying()) {
                        currentMediaPlayer.start();
                        refreshAttachmentsUI();
                    }
                } catch (IllegalStateException e) {
                    android.util.Log.e("NoteCreation", "MediaPlayer in wrong state for resume: " + e.getMessage(), e);
                    stopAudio();
                    Toast.makeText(this, "Cannot resume audio", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            android.util.Log.e("NoteCreation", "Error resuming audio: " + e.getMessage(), e);
            Toast.makeText(this, "Failed to resume audio", Toast.LENGTH_SHORT).show();
        }
    }

    private void playAudio(int position, URI audioUri) {
        try {
            android.util.Log.d("NoteCreation", "playAudio called for position: " + position + ", URI: " + audioUri);

            if (currentMediaPlayer != null) {
                android.util.Log.d("NoteCreation", "Stopping existing MediaPlayer");
                stopAudio();
            }

            Uri uri = Uri.parse(audioUri.toString());
            android.util.Log.d("NoteCreation", "Parsed URI: " + uri);

            try {
                getContentResolver().takePersistableUriPermission(uri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION);
                android.util.Log.d("NoteCreation", "URI permission taken");
            } catch (Exception e) {
                android.util.Log.d("NoteCreation", "Permission already granted or not needed: " + e.getMessage());
            }

            currentMediaPlayer = new android.media.MediaPlayer();
            android.util.Log.d("NoteCreation", "Created new MediaPlayer instance");

            setupMediaPlayerListeners(currentMediaPlayer, position);

            android.os.Handler handler = new android.os.Handler(android.os.Looper.getMainLooper());
            handler.post(() -> {
                try {
                    android.util.Log.d("NoteCreation", "Setting data source: " + uri);
                    currentMediaPlayer.setDataSource(NoteCreationActivity.this, uri);
                    android.util.Log.d("NoteCreation", "Data source set successfully");

                    android.util.Log.d("NoteCreation", "Starting prepareAsync for audio");
                    currentMediaPlayer.prepareAsync();
                } catch (Exception e) {
                    android.util.Log.e("NoteCreation", "Error setting data source: " + e.getMessage(), e);
                    try {
                        if (uri.getScheme() != null && uri.getScheme().equals("file")) {
                            android.util.Log.d("NoteCreation", "Trying file path: " + uri.getPath());
                            currentMediaPlayer.setDataSource(uri.getPath());
                            currentMediaPlayer.prepareAsync();
                        } else {
                            String path = uri.getPath();
                            if (path != null) {
                                android.util.Log.d("NoteCreation", "Trying path: " + path);
                                currentMediaPlayer.setDataSource(path);
                                currentMediaPlayer.prepareAsync();
                            } else {
                                throw new Exception("Cannot set data source: " + e.getMessage());
                            }
                        }
                    } catch (Exception e2) {
                        android.util.Log.e("NoteCreation", "All data source methods failed: " + e2.getMessage(), e2);
                        if (currentMediaPlayer != null) {
                            try {
                                currentMediaPlayer.release();
                            } catch (Exception ex) {
                                // Ignore
                            }
                            currentMediaPlayer = null;
                        }
                        currentPlayingPosition = -1;
                        Toast.makeText(NoteCreationActivity.this, "Failed to load audio: " + e2.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });

        } catch (Exception e) {
            android.util.Log.e("NoteCreation", "Error in playAudio: " + e.getMessage(), e);
            if (currentMediaPlayer != null) {
                try {
                    currentMediaPlayer.release();
                } catch (Exception ex) {
                    // Ignore
                }
                currentMediaPlayer = null;
            }
            currentPlayingPosition = -1;
            Toast.makeText(this, "Failed to play audio: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void setupMediaPlayerListeners(android.media.MediaPlayer mp, int position) {
        try {
            final int playPosition = position;

            mp.setOnCompletionListener(mediaPlayer -> {
                android.util.Log.d("NoteCreation", "MediaPlayer playback completed");
                stopAudio();
                refreshAttachmentsUI();
            });

            mp.setOnErrorListener((mediaPlayer, what, extra) -> {
                android.util.Log.e("NoteCreation", "MediaPlayer error: what=" + what + ", extra=" + extra);
                Toast.makeText(NoteCreationActivity.this, "Audio playback error: " + what, Toast.LENGTH_SHORT).show();
                stopAudio();
                refreshAttachmentsUI();
                return true;
            });

            mp.setOnPreparedListener(mediaPlayer -> {
                try {
                    android.util.Log.d("NoteCreation", "MediaPlayer prepared, starting playback for position: " + playPosition);
                    if (mediaPlayer == currentMediaPlayer) {
                        mediaPlayer.start();
                        currentPlayingPosition = playPosition;
                        android.util.Log.d("NoteCreation", "Playback started successfully");
                        refreshAttachmentsUI();
                    } else {
                        android.util.Log.w("NoteCreation", "MediaPlayer instance mismatch, not starting");
                    }
                } catch (Exception e) {
                    android.util.Log.e("NoteCreation", "Failed to start playback: " + e.getMessage(), e);
                    Toast.makeText(NoteCreationActivity.this, "Failed to start playback: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    stopAudio();
                }
            });

        } catch (Exception e) {
            android.util.Log.e("NoteCreation", "Error setting up MediaPlayer listeners: " + e.getMessage(), e);
            if (mp != null) {
                try {
                    mp.release();
                } catch (Exception ex) {
                    // Ignore
                }
            }
            currentMediaPlayer = null;
            currentPlayingPosition = -1;
            Toast.makeText(this, "Failed to setup audio player: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void pauseAudio() {
        try {
            if (currentMediaPlayer != null) {
                try {
                    if (currentMediaPlayer.isPlaying()) {
                        currentMediaPlayer.pause();
                        refreshAttachmentsUI();
                    }
                } catch (IllegalStateException e) {
                    android.util.Log.e("NoteCreation", "MediaPlayer in wrong state for pause: " + e.getMessage(), e);
                    stopAudio();
                }
            }
        } catch (Exception e) {
            android.util.Log.e("NoteCreation", "Error pausing audio: " + e.getMessage(), e);
        }
    }

    private void stopAudio() {
        try {
            if (currentMediaPlayer != null) {
                try {
                    if (currentMediaPlayer.isPlaying()) {
                        currentMediaPlayer.stop();
                    }
                } catch (Exception e) {
                    // MediaPlayer might be in wrong state, continue to release
                }
                try {
                    currentMediaPlayer.release();
                } catch (Exception e) {
                    // Ignore release errors
                }
                currentMediaPlayer = null;
            }
            currentPlayingPosition = -1;
        } catch (Exception e) {
            // Ignore all errors during cleanup
            currentMediaPlayer = null;
            currentPlayingPosition = -1;
        }
    }

    // ----- AUTOSAVE -----

    @Override
    protected void onPause() {
        super.onPause();
        // Stop audio playback when activity is paused
        stopAudio();

        // Auto-save draft when leaving activity
        String title = noteTitleEditText.getText().toString().trim();
        String body = noteBodyEditText.getText().toString().trim();

        if (!title.isEmpty() || !body.isEmpty()) {
            currentNote.title = title.isEmpty() ? "Untitled" : title;
            currentNote.setBodyHTML(body);
            // Optionally persist pin/place state here too:
            // currentNote.isPinned = isPinned;
            // if (hasPlace) noteController.setCurrentLocation(currentNote); else noteController.removeLocation(currentNote);
            noteController.save(currentNote);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Clean up MediaPlayer when activity is destroyed
        stopAudio();
    }
}
