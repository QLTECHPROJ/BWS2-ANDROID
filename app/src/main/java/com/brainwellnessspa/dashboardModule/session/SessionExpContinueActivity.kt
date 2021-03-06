package com.brainwellnessspa.dashboardModule.session

import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.view.ViewTreeObserver
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.brainwellnessspa.R
import com.brainwellnessspa.databinding.ActivitySessionExpContinueBinding
import com.brainwellnessspa.utility.MySpannable

class SessionExpContinueActivity : AppCompatActivity() {
    lateinit var binding: ActivitySessionExpContinueBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_session_exp_continue)

        binding.tvCESubTitle.post {
            val lineCount: Int = binding.tvCESubTitle.getLineCount()
            if (lineCount < 3 || lineCount == 3) {
            } else {
                makeTextViewResizable(binding.tvCESubTitle, 3, "...More", true)
            }
        }

        binding.btnContinue.setOnClickListener {
            val i = Intent(this, SessionExpActivity::class.java)
            startActivity(i)
        }

    }

    fun makeTextViewResizable(tv: TextView, maxLine: Int, expandText: String, viewMore: Boolean) {
        if (tv.tag == null) {
            tv.tag = tv.text
        }
        val vto = tv.viewTreeObserver
        vto.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                val text: String
                val lineEndIndex: Int
                val obs = tv.viewTreeObserver
                obs.removeGlobalOnLayoutListener(this)
                if (maxLine == 0) {
                    lineEndIndex = tv.layout.getLineEnd(0)
                    text = tv.text.subSequence(0, lineEndIndex - expandText.length + 1).toString() + " " + "<font   color=\"#313131\">" + expandText + "</font>"
                } else if (maxLine > 0 && tv.lineCount >= maxLine) {
                    lineEndIndex = tv.layout.getLineEnd(maxLine - 1)
                    text = tv.text.subSequence(0, lineEndIndex - expandText.length + 1).toString() + " " + "<font color=\"#313131\">" + expandText + "</font>"
                } else {
                    lineEndIndex = tv.layout.getLineEnd(tv.layout.lineCount - 1)
                    text = tv.text.subSequence(0, lineEndIndex).toString() + " " + "<font color=\"#313131\">" + expandText + "</font>"
                }
                tv.text = Html.fromHtml(text)
                tv.movementMethod = LinkMovementMethod.getInstance()
                tv.setText(addClickablePartTextViewResizable(Html.fromHtml(tv.text.toString()), tv, lineEndIndex, expandText, viewMore), TextView.BufferType.SPANNABLE)
            }
        })
    }

    fun addClickablePartTextViewResizable(strSpanned: Spanned, tv: TextView, maxLine: Int, spanableText: String, viewMore: Boolean): SpannableStringBuilder? {
        val str = strSpanned.toString()
        val ssb = SpannableStringBuilder(strSpanned)
        if (str.contains(spanableText)) {
            ssb.setSpan(object : MySpannable(false) {
                fun onClick() {
                    tv.layoutParams = tv.layoutParams
                    tv.setText(tv.tag.toString(), TextView.BufferType.SPANNABLE)
                    tv.invalidate()
                    if (viewMore) {
                        makeTextViewResizable(tv, -1, "...Less", false)
                    } else {
                        makeTextViewResizable(tv, 3, "...More", true)
                    }
                }
            }, str.indexOf(spanableText), str.indexOf(spanableText) + spanableText.length, 0)
        }
        return ssb
    }

}