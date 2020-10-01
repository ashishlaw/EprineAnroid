package com.eprine.customcontrols;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.eprine.controller.Fonts;


public class TextViewRegular extends TextView {
    public TextViewRegular(Context context) {
        super(context);

        init();
    }

    public TextViewRegular(Context context, AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    public TextViewRegular(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    private void init() {
        if (isInEditMode()) {
            return;
        }

        setTypeface(Fonts.setLatoRegular(getContext()));

    }


}
