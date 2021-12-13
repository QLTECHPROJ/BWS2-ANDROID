package com.brainwellnessspa.dashboardModule.session

import android.app.Activity
import android.content.Context
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
import com.brainwellnessspa.BWSApplication
import com.brainwellnessspa.R
import com.brainwellnessspa.dashboardModule.models.IntroSessionModel
import com.brainwellnessspa.databinding.ActivitySessionExpContinueBinding
import com.brainwellnessspa.utility.APINewClient
import com.brainwellnessspa.utility.MySpannable
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SessionFreeIntroScreenActivity : AppCompatActivity() {
    lateinit var binding: ActivitySessionExpContinueBinding
    lateinit var act: Activity
    lateinit var ctx: Context
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_session_exp_continue)
        act = this@SessionFreeIntroScreenActivity
        ctx = this@SessionFreeIntroScreenActivity
        prepareData()
        binding.tvCESubTitle.post {
            val lineCount: Int = binding.tvCESubTitle.lineCount
            if (lineCount <= 3) {
            } else {
                makeTextViewResizable(binding.tvCESubTitle, 3, "...More", true)
            }
        }

        binding.btnContinue.setOnClickListener {
            intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_NO_ANIMATION
            intent.putExtra("IsFirst", "1")
            startActivity(intent)
            finish()
        }

    }
    fun prepareData() {
        if (BWSApplication.isNetworkConnected(ctx)) {
            BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, act)
            val listCall = APINewClient.client.introSessionContent
            listCall.enqueue(object : Callback<IntroSessionModel?> {
                override fun onResponse(call: Call<IntroSessionModel?>, response: Response<IntroSessionModel?>) {
                    try {
                        val listModel = response.body()
                        if (listModel!!.responseCode.equals(getString(R.string.ResponseCodesuccess), ignoreCase = true)) {
                            BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, act)
                            binding.tvCETitle.text = listModel.responseData!!.freeSessionTitle
                            binding.tvCESubTitle.text = listModel.responseData!!.freeSessionContent
                            Glide.with(act).load( listModel.responseData!!.freeSessionImg).thumbnail(0.05f)
                                .apply(RequestOptions.bitmapTransform(RoundedCorners(28)))
                                .priority(Priority.HIGH).diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into( binding.ivIntroImg1)


                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onFailure(call: Call<IntroSessionModel?>, t: Throwable) {
                    BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, act)
                }
            })
        } else {
            BWSApplication.showToast(getString(R.string.no_server_found), act)
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