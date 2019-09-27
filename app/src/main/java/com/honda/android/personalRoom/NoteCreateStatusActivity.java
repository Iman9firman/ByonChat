package com.honda.android.personalRoom;

/**
 * Created by byonc on 2/8/2017.
 */

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.honda.android.R;
import com.honda.android.communication.MessengerConnectionService;
import com.honda.android.personalRoom.asynctask.ProfileSaveDescription;
import com.honda.android.provider.Contact;
import com.honda.android.provider.MessengerDatabaseHelper;
import com.honda.android.utils.UtilsPD;
import com.rockerhieu.emojicon.EmojiconEditText;
import com.rockerhieu.emojicon.EmojiconGridFragment;
import com.rockerhieu.emojicon.EmojiconsFragment;
import com.rockerhieu.emojicon.emoji.Emojicon;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import io.codetail.animation.SupportAnimator;
import io.codetail.animation.ViewAnimationUtils;

public class NoteCreateStatusActivity extends AppCompatActivity implements EmojiconGridFragment.OnEmojiconClickedListener, EmojiconsFragment.OnEmojiconBackspaceClickedListener {

    private String URL_SAVE_STATUS = "https://" + MessengerConnectionService.HTTP_SERVER2 + "/personal_room/webservice/proses/add_new_note.php";
    private LinearLayout emojicons, contentRoot;
    private ImageButton btnMic, btn_add_emoticon, btnKeyboard;
    EmojiconEditText writeStatus;
    Toolbar toolbar;
    private final int REQ_CODE_SPEECH_INPUT = 1208;
    private MessengerDatabaseHelper messengerHelper;
    LinearLayout mRevealView;
    boolean hidden = true;
    RelativeLayout rootView;
    Boolean isKeyboard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_status);

        rootView = (RelativeLayout) findViewById(R.id.rootView);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        contentRoot = (LinearLayout) findViewById(R.id.contentRoot);
        writeStatus = (EmojiconEditText) findViewById(R.id.writeStatus);
        btnKeyboard = (ImageButton) findViewById(R.id.btnKeyboard);
        emojicons = (LinearLayout) findViewById(R.id.emojiconsLayout);
        btn_add_emoticon = (ImageButton) findViewById(R.id.btn_add_emoticon);
        btnMic = (ImageButton) findViewById(R.id.btnMic);
        mRevealView = (LinearLayout) findViewById(R.id.reveal_items);

        toolbar.setTitleTextColor(Color.WHITE);
        mRevealView.setVisibility(View.INVISIBLE);

        /*rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int heightDiff = rootView.getRootView().getHeight() - rootView.getHeight();

                if (heightDiff > 100) {
                    isKeyboard = true;
                    Toast.makeText(NoteCreateStatusActivity.this, "true", Toast.LENGTH_SHORT).show();
                }
            }
        });*/

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(Color.BLACK);
        }

        if (messengerHelper == null) {
            messengerHelper = MessengerDatabaseHelper.getInstance(this);
        }

        btnMic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                promptSpeechInput();
            }
        });

        btnKeyboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (emojicons.getVisibility() == View.VISIBLE) {
                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                    writeStatus.setFocusableInTouchMode(true);
                    writeStatus.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(writeStatus, InputMethodManager.SHOW_IMPLICIT);
                    emojicons.setVisibility(View.GONE);
                } else {
                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                    writeStatus.setFocusableInTouchMode(true);
                    writeStatus.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(writeStatus, InputMethodManager.SHOW_IMPLICIT);
                    if (emojicons.getVisibility() == View.VISIBLE) {
                        emojicons.setVisibility(View.GONE);
                    }
                }
            }
        });

        btn_add_emoticon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (emojicons.getVisibility() == View.GONE) {
                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

                    Animation animFade = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.abc_slide_in_bottom);
                    emojicons.setVisibility(View.VISIBLE);
                    emojicons.startAnimation(animFade);

                    writeStatus.setFocusable(false);
                } else {
                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                    writeStatus.setFocusableInTouchMode(true);
                    writeStatus.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(writeStatus, InputMethodManager.SHOW_IMPLICIT);
                    emojicons.setVisibility(View.GONE);
                }
            }
        });

        writeStatus.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View arg0, MotionEvent arg1) {

                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                writeStatus.setFocusableInTouchMode(true);
                writeStatus.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(writeStatus, InputMethodManager.SHOW_IMPLICIT);
                if (emojicons.getVisibility() == View.VISIBLE) {
                    emojicons.setVisibility(View.GONE);
                }

                return false;
            }
        });

        View view = findViewById(R.id.btn_create);
        if (view != null && view instanceof TextView) {
            ((TextView) view).setTextColor(getResources().getColor(R.color.colorMenu)); // Make text colour blue
            ((TextView) view).setTextSize(TypedValue.COMPLEX_UNIT_SP, 20); // Increase font size
        }

        writeStatus.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable arg0) {
                View view = findViewById(R.id.btn_create);
                if (writeStatus.getText().toString().trim().length() > 0) {
                    if (view != null && view instanceof TextView) {
                        ((TextView) view).setTextColor(Color.WHITE); // Make text colour blue
                        ((TextView) view).setTextSize(TypedValue.COMPLEX_UNIT_SP, 20); // Increase font size
                    }
                } else {
                    if (view != null && view instanceof TextView) {
                        ((TextView) view).setTextColor(getResources().getColor(R.color.colorMenu)); // Make text colour blue
                        ((TextView) view).setTextSize(TypedValue.COMPLEX_UNIT_SP, 20); // Increase font size
                    }
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

    }

    @Override
    public void onEmojiconBackspaceClicked(View v) {
        EmojiconsFragment.backspace(writeStatus);
    }

    @Override
    public boolean onPrepareOptionsMenu(final Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.create_status_action, menu);

        menu.getItem(0).setEnabled(false);

        writeStatus.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable arg0) {
                if (writeStatus.getText().toString().trim().length() > 0) {
                    menu.getItem(0).setEnabled(true);
                } else {
                    menu.getItem(0).setEnabled(false);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });
        return super.onPrepareOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                Log.w("plert","dudiu");
                onBackPressed();
                return true;
            case R.id.btn_create:

                if (validateComment()) {
                    new SubmitStatus().execute(messengerHelper.getMyContact().getJabberId(), writeStatus.getText().toString().trim(), URL_SAVE_STATUS);
                    writeStatus.setText(null);
                    View view = getCurrentFocus();
                    if (view != null) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQ_CODE_SPEECH_INPUT) {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    String text = result.get(0);
                    if (writeStatus.getText().length() > 0)
                        text = writeStatus.getText().toString() + " " + result.get(0);
                    writeStatus.setText(text);
                    writeStatus.setSelection(writeStatus.getText().length());
                }
            }
        }
    }


    private boolean validateComment() {
        if (writeStatus.getText().toString().trim().length() == 0) {
            return false;
        }
        return true;
    }
//
//    public void onBackPressed() {
//        final View activityRootView = findViewById(R.id.rootView);
//        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                int heightDiff = activityRootView.getRootView().getHeight() - activityRootView.getHeight();
//                if (heightDiff > 100) { // if more than 100 pixels, its probably a keyboard...
//                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
//                    imm.hideSoftInputFromWindow(writeStatus.getWindowToken(), 0);
//                } else {
//                    NoteCreateStatusActivity.super.onBackPressed();
//                }
//            }
//        });
//    }

    class SubmitStatus extends AsyncTask<String, Void, String> {
        ProfileSaveDescription profileSaveDescription = new ProfileSaveDescription();
        String result = "";
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (progressDialog == null) {
                progressDialog = UtilsPD.createProgressDialog(NoteCreateStatusActivity.this);
                progressDialog.show();
            }
        }

        @Override
        protected String doInBackground(String... params) {
            HashMap<String, String> data = new HashMap<String, String>();
            data.put("userid", params[0]);
            data.put("content_note", params[1]);
            String result = profileSaveDescription.sendPostRequest(params[2], data);
            return result;
        }

        protected void onPostExecute(String s) {
            progressDialog.dismiss();
            if (s.equalsIgnoreCase("1")) {
                writeStatus.getText().clear();
                onBackPressed();
            } else if (s.equalsIgnoreCase("0")) {
                Toast.makeText(NoteCreateStatusActivity.this, "Failed to post status", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(NoteCreateStatusActivity.this, s, Toast.LENGTH_SHORT).show();
            }

        }
    }

    @Override
    public void onEmojiconClicked(Emojicon emojicon) {
        EmojiconsFragment.input(writeStatus, emojicon);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if(emojicons.getVisibility()== View.GONE){
                finish();
            }else {
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                writeStatus.setFocusableInTouchMode(true);
                writeStatus.requestFocus();
                InputMethodManager imm =  (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(writeStatus, InputMethodManager.SHOW_IMPLICIT);
                emojicons.setVisibility(View.GONE);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void promptSpeechInput() {
        showAttc(false);

        Contact contact = messengerHelper.getMyContact();
        String language = String.valueOf(Locale.getDefault());
        if (contact.getJabberId().substring(0, 2).equalsIgnoreCase("62")) language = "id";

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, language);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, language);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, language);
        intent.putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, language);
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(), getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void showAttc(boolean att) {
        int cx = (mRevealView.getLeft() + mRevealView.getRight());
        int cy = mRevealView.getTop();
        int radius = Math.max(mRevealView.getWidth(), mRevealView.getHeight());
        if (att) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                SupportAnimator animator =
                        ViewAnimationUtils.createCircularReveal(mRevealView, cx, cy, 0, radius);
                animator.setInterpolator(new AccelerateDecelerateInterpolator());
                animator.setDuration(300);

                SupportAnimator animator_reverse = animator.reverse();

                if (hidden) {
                    mRevealView.setVisibility(View.VISIBLE);
                    animator.start();
                    hidden = false;
                } else {
                    animator_reverse.addListener(new SupportAnimator.AnimatorListener() {
                        @Override
                        public void onAnimationStart() {

                        }

                        @Override
                        public void onAnimationEnd() {
                            mRevealView.setVisibility(View.INVISIBLE);
                            hidden = true;

                        }

                        @Override
                        public void onAnimationCancel() {

                        }

                        @Override
                        public void onAnimationRepeat() {

                        }
                    });
                    animator_reverse.start();

                }
            } else {
                if (hidden) {
                    Animator anim = android.view.ViewAnimationUtils.createCircularReveal(mRevealView, cx, cy, 0, radius);
                    mRevealView.setVisibility(View.VISIBLE);
                    anim.start();
                    hidden = false;

                } else {
                    Animator anim = android.view.ViewAnimationUtils.createCircularReveal(mRevealView, cx, cy, radius, 0);
                    anim.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            mRevealView.setVisibility(View.INVISIBLE);
                            hidden = true;
                        }
                    });
                    anim.start();

                }
            }
        } else {
            if (!hidden) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    SupportAnimator animator =
                            ViewAnimationUtils.createCircularReveal(mRevealView, cx, cy, 0, radius);
                    animator.setInterpolator(new AccelerateDecelerateInterpolator());
                    animator.setDuration(300);

                    SupportAnimator animator_reverse = animator.reverse();
                    animator_reverse.addListener(new SupportAnimator.AnimatorListener() {
                        @Override
                        public void onAnimationStart() {

                        }

                        @Override
                        public void onAnimationEnd() {
                            mRevealView.setVisibility(View.INVISIBLE);
                            hidden = true;
                        }

                        @Override
                        public void onAnimationCancel() {

                        }

                        @Override
                        public void onAnimationRepeat() {

                        }
                    });
                    animator_reverse.start();

                } else {
                    Animator anim = android.view.ViewAnimationUtils.createCircularReveal(mRevealView, cx, cy, radius, 0);
                    anim.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            mRevealView.setVisibility(View.INVISIBLE);
                            hidden = true;
                        }
                    });
                    anim.start();
                }
            }
        }
    }
}

