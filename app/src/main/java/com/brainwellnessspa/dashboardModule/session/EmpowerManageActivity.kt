package com.brainwellnessspa.dashboardModule.session

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.brainwellnessspa.BWSApplication
import com.brainwellnessspa.R
import com.brainwellnessspa.billingOrderModule.activities.CancelMembershipActivity
import com.brainwellnessspa.dashboardModule.models.EEPPlanListModel
import com.brainwellnessspa.dashboardModule.models.SaveProgressReportModel
import com.brainwellnessspa.databinding.ActivityBrainStatusBinding
import com.brainwellnessspa.databinding.ActivityEmpowerManageBinding
import com.brainwellnessspa.utility.APINewClient
import com.brainwellnessspa.utility.CONSTANTS
import com.google.android.youtube.player.YouTubeBaseActivity
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EmpowerManageActivity : YouTubeBaseActivity(), YouTubePlayer.OnInitializedListener  {
    lateinit var binding: ActivityEmpowerManageBinding
    lateinit var act: Activity
    lateinit var ctx: Context
    var userId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_empower_manage)
        act = this@EmpowerManageActivity
        ctx = this@EmpowerManageActivity
        val shared1 = getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, Context.MODE_PRIVATE)
        userId = shared1.getString(CONSTANTS.PREFE_ACCESS_UserId, "")!!

        prepareData()
        binding.youtubeView.initialize(API_KEY, this)
        binding.llBack.setOnClickListener{
          finish()
        }
         binding.llSkip.setOnClickListener{
            finish()
        }
    }

    override fun onResume() {
        prepareData()
        super.onResume()
    }
    fun prepareData() {
        if (BWSApplication.isNetworkConnected(act)) {
            BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, act)
            val listCall = APINewClient.client.getEEPPlanList(userId)
            listCall.enqueue(object : Callback<EEPPlanListModel?> {
                override fun onResponse(call: Call<EEPPlanListModel?>, response: Response<EEPPlanListModel?>) {
                    try {
                        val listModel1 = response.body()
                        if (listModel1?.responseCode.equals(act.getString(R.string.ResponseCodesuccess), ignoreCase = true)) {

                            // do plan Code
                        } else if (listModel1!!.responseCode.equals(act.getString(R.string.ResponseCodeDeleted))) {
                            BWSApplication.callDelete403(act, listModel1.responseMessage)
                        } else {
                            BWSApplication.showToast(listModel1.responseMessage, act)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onFailure(call: Call<EEPPlanListModel?>, t: Throwable) {
                    BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, act)
                }
            })
        } else {
            BWSApplication.showToast(act.getString(R.string.no_server_found), act)
        }
    }
    override fun onBackPressed() {
        finish()
        super.onBackPressed()
    }

    override fun onInitializationSuccess(provider: YouTubePlayer.Provider, youTubePlayer: YouTubePlayer, wasRestored: Boolean) {
        if (!wasRestored) {
            youTubePlayer.loadVideo(CancelMembershipActivity.VIDEO_ID)
            youTubePlayer.setShowFullscreenButton(true)
        }
    }

    override fun onInitializationFailure(provider: YouTubePlayer.Provider, errorReason: YouTubeInitializationResult) {
        if (errorReason.isUserRecoverableError) {
            errorReason.getErrorDialog(this, RECOVERY_DIALOG_REQUEST).show()
        } else {
            val errorMessage = String.format(getString(R.string.error_player), errorReason.toString())
            BWSApplication.showToast(errorMessage, this)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        if (requestCode == RECOVERY_DIALOG_REQUEST) {
            youTubePlayerProvider.initialize(API_KEY, this)
        }
    }

    private val youTubePlayerProvider: YouTubePlayer.Provider
        private get() = binding.youtubeView

    companion object {
        const val API_KEY = "AIzaSyCzqUwQUD58tA8wrINDc1OnL0RgcU52jzQ"
        const val VIDEO_ID = "y1rfRW6WX08"
        private const val RECOVERY_DIALOG_REQUEST = 1
    }

}