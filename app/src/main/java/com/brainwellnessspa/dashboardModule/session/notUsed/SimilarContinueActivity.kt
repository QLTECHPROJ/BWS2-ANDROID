package com.brainwellnessspa.dashboardModule.session.notUsed

import android.app.Activity
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.brainwellnessspa.R
import com.brainwellnessspa.databinding.ActivitySimilarContinueBinding

class SimilarContinueActivity : AppCompatActivity() {
    lateinit var binding: ActivitySimilarContinueBinding
    lateinit var act: Activity
    lateinit var ctx: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_similar_continue)
        act = this@SimilarContinueActivity
        ctx = this@SimilarContinueActivity
        prepareData()
    }
    fun prepareData() {
      /*  if (BWSApplication.isNetworkConnected(ctx)) {
            BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, act)
            val listCall = APINewClient.client.introSessionContent
            listCall.enqueue(object : Callback<IntroSessionModel?> {
                override fun onResponse(call: Call<IntroSessionModel?>, response: Response<IntroSessionModel?>) {
                    try {
                        val listModel = response.body()
                        if (listModel!!.responseCode.equals(getString(R.string.ResponseCodesuccess), ignoreCase = true)) {
                            BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, act)
                            binding.tvTitle.text = listModel.responseData!!.freeSessionTitle
                            binding.tvSubTitle.text = listModel.responseData!!.freeSessionContent
                            Glide.with(act).load( listModel.responseData!!.freeSessionImg).thumbnail(0.05f)
                                .apply(RequestOptions.bitmapTransform(RoundedCorners(28)))
                                .priority(Priority.HIGH).diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into( binding.ivIntroImg)
                            binding.ivIntroImg1.visibility = View.GONE

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
        }*/
    }
}