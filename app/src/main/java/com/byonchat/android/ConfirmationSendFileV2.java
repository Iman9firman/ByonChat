package com.byonchat.android;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.byonchat.android.personalRoom.model.NotesPhoto;
import com.byonchat.android.utils.ImageUtil;
import com.byonchat.android.utils.TouchImageView;
import com.byonchat.android.utils.Validations;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.byonchat.android.ConfirmationSendFileFolllowup.EXTRA_TEXT_CAPTIONS;

public class ConfirmationSendFileV2 extends AppCompatActivity {
    public static final String EXTRA_NAME = "name";
    public static final String EXTRA_TYPE = "type";
    public static final String EXTRA_PHOTOS = "photos";
    public static final String EXTRA_CAPTIONS = "captions";

    private List<NotesPhoto> photos;
    private Map<String, String> captions;
    private String name, type;
    File compressedFile;

    EditText textMessage;
    TouchImageView imageView;
    ImageView imagePlay;
    Button btnCancel;
    Button btnSend;

    public static Intent generateIntent(Context context, String jabberid, List<NotesPhoto> soloPhotos, String type) {
        Intent intent = new Intent(context, ConfirmationSendFileV2.class);
        intent.putExtra(EXTRA_NAME, jabberid);
        intent.putExtra(EXTRA_TYPE, type);
        intent.putParcelableArrayListExtra(EXTRA_PHOTOS, (ArrayList<NotesPhoto>) soloPhotos);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.confirmation_send_file);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new Validations().getInstance(getApplicationContext()).header(getWindow()));
        name = getIntent().getStringExtra(EXTRA_NAME);
        type = getIntent().getStringExtra(EXTRA_TYPE);

        if (savedInstanceState != null) {
            captions = (Map<String, String>) savedInstanceState.getSerializable("saved_captions");
        }

        if (captions == null) {
            captions = new HashMap<>();
        }

        textMessage = (EditText) findViewById(R.id.textMessage);
        imageView = (TouchImageView) findViewById(R.id.imageView);
        imagePlay = (ImageView) findViewById(R.id.imagePlay);
        btnCancel = (Button) findViewById(R.id.btnCancel);
        btnSend = (Button) findViewById(R.id.btnSend);

        imagePlay.setVisibility(View.GONE);
        photos = getIntent().getParcelableArrayListExtra("photos");
        if (photos != null) {
            initPhotos();
            initCaptions();
        } else {
            finish();
            return;
        }

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirm();
            }
        });
    }

    void initPhotos() {
        NotesPhoto photo = photos.get(0);
        CompressFile(photo.getPhotoFile());
    }

    void initCaptions() {
        textMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                NotesPhoto photo = photos.get(0);
                captions.put(EXTRA_TEXT_CAPTIONS,
                        textMessage.getText().toString().trim());
            }
        });
    }

    void CompressFile(File file) {
        compressedFile = file;
        if (ImageUtil.isImage(file)) {
            try {
                compressedFile = ImageUtil.compressImage(file);
            } catch (NullPointerException e) {
                showError(getString(R.string.corrupted_file));
                return;
            }
        }

        if (!file.exists()) { //File have been removed, so we can not upload it anymore
            showError(getString(R.string.corrupted_file));
            return;
        } else {
            photos = new ArrayList<>();
            NotesPhoto nphoto = new NotesPhoto(compressedFile, textMessage.getText().toString());
            photos.add(nphoto);
        }
        Picasso.with(getApplicationContext()).load(compressedFile).into(imageView);
    }

    private void confirm() {
        if (TextUtils.isEmpty(textMessage.getText().toString())) {
            textMessage.setError("Message is required!");
        } else {
            Intent data = new Intent();
            data.putParcelableArrayListExtra(EXTRA_PHOTOS, (ArrayList<NotesPhoto>) photos);
            data.putExtra(EXTRA_CAPTIONS, (HashMap<String, String>) captions);
            setResult(RESULT_OK, data);
            finish();
        }
    }

    public void showError(String errorMessage) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("saved_captions", (HashMap<String, String>) captions);
    }
}
