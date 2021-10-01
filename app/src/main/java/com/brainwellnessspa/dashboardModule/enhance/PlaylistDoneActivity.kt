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
                        val listModel: AuthOtpModel = response.body()!!
                        hideProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                        if (listModel.ResponseCode == getString(R.string.ResponseCodesuccess)) {
                            IsLock = listModel.ResponseData.Islock
                            val shared = getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, Context.MODE_PRIVATE)
                            val editor = shared.edit()
                            editor.putString(CONSTANTS.PREFE_ACCESS_mainAccountID, listModel.ResponseData.MainAccountID)
                            editor.putString(CONSTANTS.PREFE_ACCESS_UserId, listModel.ResponseData.UserId)
                            editor.putString(CONSTANTS.PREFE_ACCESS_EMAIL, listModel.ResponseData.Email)
                            editor.putString(CONSTANTS.PREFE_ACCESS_NAME, listModel.ResponseData.Name)
                            editor.putString(CONSTANTS.PREFE_ACCESS_MOBILE, listModel.ResponseData.Mobile)
                            editor.putString(CONSTANTS.PREFE_ACCESS_CountryCode, listModel.ResponseData.CountryCode)
                            editor.putString(CONSTANTS.PREFE_ACCESS_SLEEPTIME, listModel.ResponseData.AvgSleepTime)
                            editor.putString(CONSTANTS.PREFE_ACCESS_INDEXSCORE, listModel.ResponseData.indexScore)
                            editor.putString(CONSTANTS.PREFE_ACCESS_SCORELEVEL, listModel.ResponseData.ScoreLevel)
                            editor.putString(CONSTANTS.PREFE_ACCESS_IMAGE, listModel.ResponseData.Image)
                            editor.putString(CONSTANTS.PREFE_ACCESS_ISPROFILECOMPLETED, listModel.ResponseData.isProfileCompleted)
                            editor.putString(CONSTANTS.PREFE_ACCESS_ISAssCOMPLETED, listModel.ResponseData.isAssessmentCompleted)
                            editor.putString(CONSTANTS.PREFE_ACCESS_directLogin, listModel.ResponseData.directLogin)
                            editor.putString(CONSTANTS.PREFE_ACCESS_isPinSet, listModel.ResponseData.isPinSet)
                            editor.putString(CONSTANTS.PREFE_ACCESS_isEmailVerified, listModel.ResponseData.isEmailVerified)
                            editor.putString(CONSTANTS.PREFE_ACCESS_isMainAccount, listModel.ResponseData.isMainAccount)
                            editor.putString(CONSTANTS.PREFE_ACCESS_coUserCount, listModel.ResponseData.CoUserCount)
                            editor.putString(CONSTANTS.PREFE_ACCESS_isInCouser, listModel.ResponseData.IsInCouser)
                            editor.putString(CONSTANTS.PREFE_ACCESS_paymentType, listModel.ResponseData.paymentType)
                            if(listModel.ResponseData.paymentType == "0"){
                                // Stripe
                                try {
                                    if (listModel.ResponseData.oldPaymentDetails.isNotEmpty()) {
                                        editor.putString(CONSTANTS.PREFE_ACCESS_PlanId, listModel.ResponseData.oldPaymentDetails[0].PlanId)
                                        editor.putString(CONSTANTS.PREFE_ACCESS_PlanPurchaseDate, listModel.ResponseData.oldPaymentDetails[0].purchaseDate)
                                        editor.putString(CONSTANTS.PREFE_ACCESS_PlanExpireDate, listModel.ResponseData.oldPaymentDetails[0].expireDate)
                                        editor.putString(CONSTANTS.PREFE_ACCESS_TransactionId, "")
                                        editor.putString(CONSTANTS.PREFE_ACCESS_TrialPeriodStart, "")
                                        editor.putString(CONSTANTS.PREFE_ACCESS_TrialPeriodEnd, "")
                                        editor.putString(CONSTANTS.PREFE_ACCESS_PlanStr, listModel.ResponseData.oldPaymentDetails[0].PlanStr)
                                        editor.putString(CONSTANTS.PREFE_ACCESS_OrderTotal, listModel.ResponseData.oldPaymentDetails[0].OrderTotal)
                                        editor.putString(CONSTANTS.PREFE_ACCESS_PlanStatus, listModel.ResponseData.oldPaymentDetails[0].PlanStatus)
                                        editor.putString(CONSTANTS.PREFE_ACCESS_CardId, listModel.ResponseData.oldPaymentDetails[0].CardId)
                                        editor.putString(CONSTANTS.PREFE_ACCESS_PlanContent, listModel.ResponseData.oldPaymentDetails[0].PlanContent)
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }else if(listModel.ResponseData.paymentType == "1"){
                                // IAP
                                try {
                                    if (listModel.ResponseData.planDetails.isNotEmpty()) {
                                        editor.putString(CONSTANTS.PREFE_ACCESS_PlanId, listModel.ResponseData.planDetails[0].PlanId)
                                        editor.putString(CONSTANTS.PREFE_ACCESS_PlanPurchaseDate, listModel.ResponseData.planDetails[0].PlanPurchaseDate)
                                        editor.putString(CONSTANTS.PREFE_ACCESS_PlanExpireDate, listModel.ResponseData.planDetails[0].PlanExpireDate)
                                        editor.putString(CONSTANTS.PREFE_ACCESS_TransactionId, listModel.ResponseData.planDetails[0].TransactionId)
                                        editor.putString(CONSTANTS.PREFE_ACCESS_TrialPeriodStart, listModel.ResponseData.planDetails[0].TrialPeriodStart)
                                        editor.putString(CONSTANTS.PREFE_ACCESS_TrialPeriodEnd, listModel.ResponseData.planDetails[0].TrialPeriodEnd)
                                        editor.putString(CONSTANTS.PREFE_ACCESS_PlanStatus, listModel.ResponseData.planDetails[0].PlanStatus)
                                        editor.putString(CONSTANTS.PREFE_ACCESS_PlanContent, listModel.ResponseData.planDetails[0].PlanContent)
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                            editor.apply()
                            val shred = getSharedPreferences(CONSTANTS.RecommendedCatMain, Context.MODE_PRIVATE)
                            val edited = shred.edit()
                            edited.putString(CONSTANTS.PREFE_ACCESS_SLEEPTIME, listModel.ResponseData.AvgSleepTime)
                            val selectedCategoriesTitle = arrayListOf<String>()
                            val selectedCategoriesName = arrayListOf<String>()
                            val gson = Gson()
                            for (i in listModel.ResponseData.AreaOfFocus) {
                                selectedCategoriesTitle.add(i.MainCat)
                                selectedCategoriesName.add(i.RecommendedCat)
                            }
                            edited.putString(CONSTANTS.selectedCategoriesTitle, gson.toJson(selectedCategoriesTitle))
                            edited.putString(CONSTANTS.selectedCategoriesName, gson.toJson(selectedCategoriesName))
                            edited.apply()
                        } else if (listModel.ResponseCode == getString(R.string.ResponseCodeDeleted)) {
                            callDelete403(activity, listModel.ResponseMessage)
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