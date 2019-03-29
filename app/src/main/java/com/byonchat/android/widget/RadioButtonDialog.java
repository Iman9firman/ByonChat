package com.byonchat.android.widget;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.Window;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.byonchat.android.R;


public class RadioButtonDialog extends Dialog {

    private RadioGroup radioGroup;
    private RadioButton radioButton;
    private MyRadioDialogListener listener;
    private int value;

    public RadioButtonDialog(Activity activity, int value, MyRadioDialogListener listener) {
        super(activity);
        this.value = value;
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.radio_button_layout);
        initViews();
    }

    private void initViews() {
        setCancelable(false);
        radioGroup = findViewById(R.id.radiogroup_radiobutton);
        if (value == -1) {
            radioGroup.clearCheck();
        } else {
            if (value == 0) {
                radioGroup.check(R.id.notok_radiobutton);
            } else {
                radioGroup.check(R.id.ok_radiobutton);
            }
        }

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.ok_radiobutton) {
                    listener.userSubmit(1);
                } else {
                    listener.userSubmit(0);
                }
                dismiss();
            }
        });
    }

    public interface MyRadioDialogListener {
        void userSubmit(int value);
    }
}
