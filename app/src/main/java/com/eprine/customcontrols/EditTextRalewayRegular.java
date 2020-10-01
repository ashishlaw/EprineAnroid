package com.eprine.customcontrols;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;

import com.eprine.controller.Fonts;

public class EditTextRalewayRegular  extends EditText {


    public EditTextRalewayRegular(Context context) {
        super(context);

        init();
    }

    public EditTextRalewayRegular(Context context, AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    public EditTextRalewayRegular(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }


    private void init() {
        if (isInEditMode()) {
            return;
        }

        setTypeface(Fonts.setRalewayRegular(getContext()));

    }
}
