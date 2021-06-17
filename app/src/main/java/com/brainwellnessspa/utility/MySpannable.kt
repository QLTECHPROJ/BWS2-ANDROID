package com.brainwellnessspa.utility

import android.graphics.Color
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.view.View

open class MySpannable(isUnderline: Boolean) : ClickableSpan() {
    private var isUnderline = false
    override fun updateDrawState(ds: TextPaint) {
        ds.isUnderlineText = isUnderline
        ds.isFakeBoldText = true
        ds.color = Color.parseColor("#F15d36")
    }

    override fun onClick(widget: View) {}

    init {
        this.isUnderline = isUnderline
    }
}