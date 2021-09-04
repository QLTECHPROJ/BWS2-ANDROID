package com.brainwellnessspa.dashboardModule.enhance

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.brainwellnessspa.BWSApplication.*
import com.brainwellnessspa.R
import com.brainwellnessspa.dashboardModule.activities.BottomNavigationActivity
import com.brainwellnessspa.databinding.ActivityPlaylistDoneBinding
import com.brainwellnessspa.userModule.models.AuthOtpModel
import com.brainwellnessspa.userModule.signupLogin.SignInActivity
import com.brainwellnessspa.utility.APINewClient
import com.brainwellnessspa.utility.CONSTANTS
import com.google.gson.Gson
import com.segment.analytics.Properties
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PlaylistDoneActivity : AppCompatActivity() {
    lateinit var binding: ActivityPlaylistDoneBinding
    private var backClick: String? = ""
    var coUserId: String? = ""
    lateinit var activity: Activity
    var userId: String? = ""
    private var mLastClickTime: Long = 0

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_playlist_done)
        activity = this@PlaylistDoneActivity
        val shared = getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, Context.MODE_PRIVATE)
        userId = shared.getString(CONSTANTS.PREFE_ACCESS_mainAccountID, "")
        coUserId = shared.getString(CONSTANTS.PREFE_ACCESS_UserId, "")

        val p = Properties()
        addToSegment(" Playlist  Ready Screen Viewed", p, CONSTANTS.screen)

        binding.tvTitle.text = getString(R.string.your_playlist_is_ready)
        binding.tvSubTitle.text = getString(R.string.playlist_ready_subtitle)

        binding.btnContinue.setOnClickListener {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return@setOnClickListener
            }
            mLastClickTime = SystemClock.elapsedRealtime()
            if (intent.extras != null) {
                backClick = intent.getStringExtra("BackClick")
            }
            if (backClick.equals("0")) {
                checkUserDetails()
                val intent = Intent(activity, BottomNavigationActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                intent.putExtra("IsFirst", "1")
                startActivity(intent)
                finish()
            } else if (backClick.equals("1")) {
                finish()
            }
        }
    }

    private fun checkUserDetails() {
        if (isNetworkConnected(this)) {
            showProgressBar(binding.progressBar, binding.progressBarHolder, activity)
            val listCall: Call<AuthOtpModel> = APINewClient.client.getCoUserDetails(coUserId)
            listCall.enqueue(object : Callback<AuthOtpModel> {
                override fun onResponse(call: Call<AuthOtpModel>, response: Response<AuthOtpModel>) {
                    try {
                        val authOtpModel: AuthOtpModel = response.body()!!
                        hideProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                        if (authOtpModel.ResponseCode == getString(R.string.ResponseCodesuccess)) {
                            IsLock = authOtpModel.ResponseData.Islock
                            val shared = getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, Context.MODE_PRIVATE)
                            val editor = shared.edit()
                            editor.putString(CONSTANTS.PREFE_ACCESS_mainAccountID, authOtpModel.ResponseData.MainAccountID)
                            editor.putString(CONSTANTS.PREFE_ACCESS_UserId, authOtpModel.ResponseData.UserId)
                            editor.putString(CONSTANTS.PREFE_ACCESS_EMAIL, authOtpModel.ResponseData.Email)
                            editor.putString(CONSTANTS.PREFE_ACCESS_NAME, authOtpModel.ResponseData.Name)
                            editor.putString(CONSTANTS.PREFE_ACCESS_MOBILE, authOtpModel.ResponseData.Mobile)
                            editor.putString(CONSTANTS.PREFE_ACCESS_SLEEPTIME, authOtpModel.ResponseData.AvgSleepTime)
                            editor.putString(CONSTANTS.PREFE_ACCESS_INDEXSCORE, authOtpModel.ResponseData.indexScore)
                            editor.putString(CONSTANTS.PREFE_ACCESS_SCORELEVEL, authOtpModel.ResponseData.ScoreLevel)
                            editor.putString(CONSTANTS.PREFE_ACCESS_IMAGE, authOtpModel.ResponseData.Image)
                            editor.putString(CONSTANTS.PREFE_ACCESS_ISPROFILECOMPLETED, authOtpModel.ResponseData.isProfileCompleted)
                            editor.putString(CONSTANTS.PREFE_ACCESS_ISAssCOMPLETED, authOtpModel.ResponseData.isAssessmentCompleted)
                            editor.putString(CONSTANTS.PREFE_ACCESS_directLogin, authOtpModel.ResponseData.directLogin)
                            editor.putString(CONSTANTS.PREFE_ACCESS_isPinSet, authOtpModel.ResponseData.isPinSet)
                            editor.putString(CONSTANTS.PREFE_ACCESS_isEmailVerified, authOtpModel.ResponseData.isEmailVerified)
                            editor.putString(CONSTANTS.PREFE_ACCESS_isMainAccount, authOtpModel.ResponseData.isMainAccount)
                            editor.putString(CONSTANTS.PREFE_ACCESS_coUserCount, authOtpModel.ResponseData.CoUserCount)
                            editor.putString(CONSTANTS.PREFE_ACCESS_isInCouser, authOtpModel.ResponseData.IsInCouser)
                            try {
                                if (authOtpModel.ResponseData.planDetails.isNotEmpty()) {
                                    editor.putString(CONSTANTS.PREFE_ACCESS_PlanId, authOtpModel.ResponseData.planDetails[0].PlanId)
                                    editor.putString(CONSTANTS.PREFE_ACCESS_PlanPurchaseDate, authOtpModel.ResponseData.planDetails[0].PlanPurchaseDate)
                                    editor.putString(CONSTANTS.PREFE_ACCESS_PlanExpireDate, authOtpModel.ResponseData.planDetails[0].PlanExpireDate)
                                    editor.putString(CONSTANTS.PREFE_ACCESS_TransactionId, authOtpModel.ResponseData.planDetails[0].TransactionId)
                                    editor.putString(CONSTANTS.PREFE_ACCESS_TrialPeriodStart, authOtpModel.ResponseData.planDetails[0].TrialPeriodStart)
                                    editor.putString(CONSTANTS.PREFE_ACCESS_TrialPeriodEnd, authOtpModel.ResponseData.planDetails[0].TrialPeriodEnd)
                                    editor.putString(CONSTANTS.PREFE_ACCESS_PlanStatus, authOtpModel.ResponseData.planDetails[0].PlanStatus)
                                    editor.putString(CONSTANTS.PREFE_ACCESS_PlanContent, authOtpModel.ResponseData.planDetails[0].PlanContent)
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                            editor.apply()
                            val shred = getSharedPreferences(CONSTANTS.RecommendedCatMain, Context.MODE_PRIVATE)
                            val edited = shred.edit()
                            edited.putString(CONSTANTS.PREFE_ACCESS_SLEEPTIME, authOtpModel.ResponseData.AvgSleepTime)
                            val selectedCategoriesTitle = arrayListOf<String>()
                            val selectedCategoriesName = arrayListOf<String>()
                            val gson = Gson()
                            for (i in authOtpModel.ResponseData.AreaOfFocus) {
                                selectedCategoriesTitle.add(i.MainCat)
                                selectedCategoriesName.add(i.RecommendedCat)
                            }
                            edited.putString(CONSTANTS.selectedCategoriesTitle, gson.toJson(selectedCategoriesTitle))
                            edited.putString(CONSTANTS.selectedCategoriesName, gson.toJson(selectedCategoriesName))
                            edited.apply()
                        } else if (authOtpModel.ResponseCode == getString(R.string.ResponseCodeDeleted)) {
                            deleteCall(activity)
                            showToast(authOtpModel.ResponseMessage, activity)
                            val i = Intent(activity, SignInActivity::class.java)
                            i.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                            i.putExtra("mobileNo", "")
                            i.putExtra("countryCode", "")
                            i.putExtra("name", "")
                            i.putExtra("email", "")
                            i.putExtra("countryShortName", "")
                            startActivity(i)
                            finish()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onFailure(call: Call<AuthOtpModel>, t: Throwable) {
                }
            })
        } else {
            showToast(getString(R.string.no_server_found), activity)
        }
    }
}