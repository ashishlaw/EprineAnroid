package com.eprine.customcontrols;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

import com.eprine.controller.Fonts;

public class ButtonRalewayBold extends Button {
    public ButtonRalewayBold(Context context) {
        super(context);

        init();
    }

    public ButtonRalewayBold(Context context, AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    public ButtonRalewayBold(Context context, AttributeSet attrs, int defStyleAttr) {
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
