package com.eprine.customcontrols;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.eprine.controller.Fonts;

public class TextViewRalewayBold extends TextView {
    public TextViewRalewayBold(Context context) {
        super(context);

        init();
    }

    public TextViewRalewayBold(Context context, AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    public TextViewRalewayBold(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    private void init() {
        if (isInEditMode()) {
            return;
        }

        setTypeface(Fonts.setRalewayBold(getContext()));

    }

}
