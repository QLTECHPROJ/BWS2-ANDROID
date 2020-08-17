package com.qltech.bws.Utility;

import android.graphics.Color;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

public class MySpannable extends ClickableSpan {
    private boolean isUnderline = false;

    public MySpannable(boolean isUnderline) {
        this.isUnderline = isUnderline;
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        ds.setUnderlineText(isUnderline);
        ds.setFakeBoldText(true);
        ds.setColor(Color.parseColor("#F15d36"));
    }

    @Override
    public void onClick(View widget) {
    }
}