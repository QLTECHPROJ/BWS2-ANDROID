package com.brainwellnessspa.dashboardModule.session

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.PorterDuff
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.brainwellnessspa.BWSApplication
import com.brainwellnessspa.R
import com.brainwellnessspa.dashboardModule.models.*
import com.brainwellnessspa.databinding.ActivitySessionPersonalHistoryBinding
import com.brainwellnessspa.databinding.FormFillLayoutBinding
import com.brainwellnessspa.databinding.SessionPersonalHistoryBinding
import com.brainwellnessspa.utility.APINewClient
import com.brainwellnessspa.utility.CONSTANTS
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.segment.analytics.Properties
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class SessionPersonalHistoryActivity : AppCompatActivity() {
	lateinit var binding: ActivitySessionPersonalHistoryBinding
	lateinit var firstListAdapter: OptionsFirstListAdapter
	lateinit var secondListAdapter: OptionsSecondListAdapter
	lateinit var ctx: Context
	var navigation: String = ""
	var progressReportQus = arrayListOf<String>()
	var progressReportAns = arrayListOf<String>()
	lateinit var act: Activity
	var myPos: Int = 0
	private var doubleBackToExitPressedOnce = false
	var mainAccountID: String? = ""
	var userId: String? = ""
	var email: String? = ""
	var listModel = StepTypeTwoSaveDataModel.ResponseData()
	var sessionId: String? = ""
	var stepId:String? = ""
	var nextForm:String? = ""
	var gson: Gson = Gson()
	var mod = 0
	val sendAnsArray = arrayListOf<sendQusData>()
	lateinit var editor: SharedPreferences.Editor
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		binding = DataBindingUtil.setContentView(this, R.layout.activity_session_personal_history)

		// mixed qus 2 textBox ,2 radio
		val shared = getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, Context.MODE_PRIVATE)
		mainAccountID = shared.getString(CONSTANTS.PREFE_ACCESS_mainAccountID, "")
		userId = shared.getString(CONSTANTS.PREFE_ACCESS_UserId, "")
		email = shared.getString(CONSTANTS.PREFE_ACCESS_EMAIL, "")
		ctx = this@SessionPersonalHistoryActivity
		act = this@SessionPersonalHistoryActivity
		getAssSaveData()
		binding.rvFirstList.layoutManager = LinearLayoutManager(ctx)

		if (intent.extras != null) {
			nextForm = intent.getStringExtra("nextForm").toString()
			sessionId = intent.getStringExtra("SessionId").toString()
			stepId = intent.getStringExtra("StepId").toString()
			val json = intent.getStringExtra("Data").toString()
			val type1 = object : TypeToken<StepTypeTwoSaveDataModel.ResponseData>() {}.type
			listModel = gson.fromJson(json, type1)
		}
		mod = listModel.questions!!.size % 2

		/* This is the next button click */
		binding.btnNext.setOnClickListener {
			getAssSaveData()
			if (myPos < listModel.questions!!.size - 1) {
				myPos += Integer.parseInt(listModel.chunkSize!!)

				binding.tvNumberOfQus.text = myPos.toString()
				binding.lpIndicator.progress = myPos

				var s = myPos + Integer.parseInt(listModel.chunkSize!!)
				if (myPos == listModel.questions!!.size - Integer.parseInt(listModel.chunkSize!!)) {
					binding.btnNext.visibility = View.GONE
					binding.btnContinue.visibility = View.VISIBLE
					firstListAdapter = OptionsFirstListAdapter(listModel.questions!!.subList(myPos, listModel.questions!!.size), myPos, listModel.questions!!.size, ctx, binding, act, listModel)
					binding.rvFirstList.adapter = firstListAdapter
				} else if (s > listModel.questions!!.size) {
					binding.btnNext.visibility = View.GONE
					binding.btnContinue.visibility = View.VISIBLE
					firstListAdapter = OptionsFirstListAdapter(listModel.questions!!.subList(myPos,listModel.questions!!.size), myPos, listModel.questions!!.size, ctx, binding, act, listModel)
					binding.rvFirstList.adapter = firstListAdapter
				}else if(myPos < listModel.questions!!.size) {
					binding.btnNext.visibility = View.VISIBLE
					binding.btnContinue.visibility = View.GONE
					firstListAdapter = OptionsFirstListAdapter(listModel.questions!!.subList(myPos, myPos + Integer.parseInt(listModel.chunkSize!!)), myPos,  myPos + Integer.parseInt(listModel.chunkSize!!), ctx, binding, act, listModel)
					binding.rvFirstList.adapter = firstListAdapter
				}
			}
			if (myPos > Integer.parseInt(listModel.chunkSize!!)) {
				binding.btnPrev.visibility = View.VISIBLE
			} else {
				binding.btnPrev.visibility = View.GONE
			}
			Log.e("Ass Post Data", gson.toJson(progressReportAns))
		}

		/* This is the previous button click */
		binding.btnPrev.setOnClickListener {
			callBack()
		}

		/* This is the continue button click when form is complete */
		binding.btnContinue.setOnClickListener {
			binding.lpIndicator.progress = listModel.questions!!.size

			binding.tvNumberOfQus.text = listModel.questions!!.size.toString()
			for (i in 0 until progressReportQus.size) {
				val sendR = sendQusData()
				sendR.question_id = (progressReportQus[i])
				sendR.answer = (progressReportAns[i])
				sendAnsArray.add(sendR)
			}
			callSaveProgressReport(gson.toJson(sendAnsArray) )
			Log.e("Ass Post Data", gson.toJson(sendAnsArray))
		}
		prepareData()
	}
	private fun callSaveProgressReport(answerJson:String ) {
		if (BWSApplication.isNetworkConnected(act)) {
			BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, act)
			val listCall = APINewClient.client.getSaveProgressReport(userId, sessionId, stepId,nextForm,answerJson)
			listCall.enqueue(object : Callback<SaveProgressReportModel?> {
				override fun onResponse(call: Call<SaveProgressReportModel?>, response: Response<SaveProgressReportModel?>) {
					try {
						val listModel1 = response.body()
						if (listModel1?.responseCode.equals(act.getString(R.string.ResponseCodesuccess), ignoreCase = true)) {
							Log.e("sussess","true")
							val preferencesd1: SharedPreferences = getSharedPreferences(CONSTANTS.ProgressReportMain, Context.MODE_PRIVATE)
							val edited1 = preferencesd1.edit()
							edited1.remove(CONSTANTS.ProgressReportQus)
							edited1.remove(CONSTANTS.ProgressReportAns)
							edited1.clear()
							edited1.apply()
							callCheckProgressReport()
						} else if(listModel1!!.responseCode.equals(act.getString(R.string.ResponseCodeDeleted))) {
							BWSApplication.callDelete403(act, listModel1.responseMessage)
						} else{
							BWSApplication.showToast(listModel1.responseMessage, act)
						}
					} catch (e: Exception) {
						e.printStackTrace()
					}
				}
				override fun onFailure(call: Call<SaveProgressReportModel?>, t: Throwable) {
					BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, act)
				}
			})
		} else {
			BWSApplication.showToast(act.getString(R.string.no_server_found), act)
		}
	}
	fun callCheckProgressReport() {
		if (BWSApplication.isNetworkConnected(act)) {
			BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, act)
			val listCall = APINewClient.client.getCheckProgressReportStatus(userId, sessionId, stepId)
			listCall.enqueue(object : Callback<CheckProgressReportStatusModel?> {
				override fun onResponse(call: Call<CheckProgressReportStatusModel?>, response: Response<CheckProgressReportStatusModel?>) {
					//                    try {
					Log.e("sussess chk","true")
					val listModel1 = response.body()
					val response = listModel1?.responseData
					if (listModel1?.responseCode.equals(act.getString(R.string.ResponseCodesuccess), ignoreCase = true)) {
						if (response != null) {
							if (response.nextForm != "") {
								val listCall = APINewClient.client.getSessionProgressReport(sessionId, stepId, response.nextForm)
								listCall.enqueue(object : Callback<StepTypeTwoSaveDataModel?> {
									override fun onResponse(call: Call<StepTypeTwoSaveDataModel?>, response2: Response<StepTypeTwoSaveDataModel?>) {
										try {
											val listModel2 = response2.body()
											val response1 = listModel2?.responseData
											Log.e("sussess chk x x", "true")

											if (listModel2?.responseCode.equals(act.getString(R.string.ResponseCodesuccess), ignoreCase = true)) {
												BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, act)
												if (response1 != null) {
													val gson = Gson()
													val i = Intent(act, SessionWalkScreenActivity::class.java)
													i.putExtra("Data", gson.toJson(response1))
													i.putExtra("nextForm", response.nextForm)
													i.putExtra("SessionId", sessionId)
													i.putExtra("StepId", stepId)
													act.startActivity(i)
													act.finish()
												}
											} else if (listModel2!!.responseCode.equals(act.getString(R.string.ResponseCodeDeleted))) {
												BWSApplication.callDelete403(act, listModel2.responseMessage)
											} else {
												BWSApplication.showToast(listModel2.responseMessage, act)
											}
										} catch (e: Exception) {
											e.printStackTrace()
										}
									}

									override fun onFailure(call: Call<StepTypeTwoSaveDataModel?>, t: Throwable) {
										BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, act)
									}
								})
							}else{
								if (BWSApplication.isNetworkConnected(act)) {
									val listCall = APINewClient.client.getSessionStepStatusList(userId, sessionId, stepId)
									listCall.enqueue(object : Callback<SessionStepStatusListModel?> {
										override fun onResponse(call: Call<SessionStepStatusListModel?>, response: Response<SessionStepStatusListModel?>) {
											try {
												val listModel = response.body()
												val response = listModel?.responseData
												if (listModel!!.responseCode.equals(act.getString(R.string.ResponseCodesuccess), ignoreCase = true)) {
													act.finish()
												}
											} catch (e: Exception) {
												e.printStackTrace()
											}
										}
										override fun onFailure(call: Call<SessionStepStatusListModel?>, t: Throwable) {
										}
									})
								} else {
									BWSApplication.showToast(act.getString(R.string.no_server_found), act)
								}
								finish()
							}
						}
					}else if(listModel1!!.responseCode.equals(act.getString(R.string.ResponseCodeDeleted))) {
						BWSApplication.callDelete403(act, listModel1.responseMessage)
					} else{
						BWSApplication.showToast(listModel1.responseMessage, act)
					}
					/*} catch (e: Exception) {
                         e.printStackTrace()
                     }*/
				}

				override fun onFailure(call: Call<CheckProgressReportStatusModel?>, t: Throwable) {
					BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, act)
				}
			})
		} else {
			BWSApplication.showToast(act.getString(R.string.no_server_found), act)
		}
	}

	private fun addInSegment(p: Properties) {
		BWSApplication.addToSegment(CONSTANTS.Assessment_Screen_Viewed, p, CONSTANTS.screen)
	}

	/* This is the device back click event */
	override fun onBackPressed() {
		callBack()
	}

	/* This is the back click event function */
	private fun callBack() {
		if (myPos >0) {
			myPos -= Integer.parseInt(listModel.chunkSize!!)
			val p = Properties()
			p.putValue("screen", myPos)
			addInSegment(p)
			binding.lpIndicator.progress = myPos
			binding.tvNumberOfQus.text = myPos.toString()
			var s = myPos + Integer.parseInt(listModel.chunkSize!!)
			if (myPos == listModel.questions!!.size - Integer.parseInt(listModel.chunkSize!!)) {
				binding.btnNext.visibility = View.GONE
				binding.btnContinue.visibility = View.VISIBLE
				firstListAdapter = OptionsFirstListAdapter(listModel.questions!!.subList(myPos, listModel.questions!!.size), myPos, listModel.questions!!.size, ctx, binding, act, listModel)
				binding.rvFirstList.adapter = firstListAdapter
			} else if (s > listModel.questions!!.size) {
				binding.btnNext.visibility = View.GONE
				binding.btnContinue.visibility = View.VISIBLE
				firstListAdapter = OptionsFirstListAdapter(listModel.questions!!.subList(myPos,listModel.questions!!.size), myPos, listModel.questions!!.size, ctx, binding, act, listModel)
				binding.rvFirstList.adapter = firstListAdapter
			}else if(myPos < listModel.questions!!.size) {
				binding.btnNext.visibility = View.VISIBLE
				binding.btnContinue.visibility = View.GONE
				firstListAdapter = OptionsFirstListAdapter(listModel.questions!!.subList(myPos, myPos + Integer.parseInt(listModel.chunkSize!!)), myPos,  myPos + Integer.parseInt(listModel.chunkSize!!), ctx, binding, act, listModel)
				binding.rvFirstList.adapter = firstListAdapter
			}
		} else {
			finish()
		}
	}

	/* This function is save assessment result */
	private fun getAssSaveData() {
		val shared = ctx.getSharedPreferences(CONSTANTS.ProgressReportMain, Context.MODE_PRIVATE)
		val json2 = shared.getString(CONSTANTS.ProgressReportQus, gson.toString())
		val json3 = shared.getString(CONSTANTS.ProgressReportAns, gson.toString())
		if (!json2.equals(gson.toString(), ignoreCase = true)) {
			val type1 = object : TypeToken<ArrayList<String?>?>() {}.type
			progressReportQus = gson.fromJson(json2, type1)
			progressReportAns = gson.fromJson(json3, type1)
		}
	}

	/* This function is get assessment questions */
	private fun prepareData() {
		binding.tvQus.visibility = View.GONE
		binding.tvText1.visibility =  View.GONE
		binding.lpIndicator.max = listModel.questions!!.size
		binding.lpIndicator.progress = 0

		binding.tvTitle.text = listModel.sectionSubtitle
		binding.tvNumberOfQus.text = myPos.toString()
		binding.tvTotalQus.text = listModel.questions!!.size.toString()

		var s = myPos + Integer.parseInt(listModel.chunkSize!!)
		if (myPos == listModel.questions!!.size - Integer.parseInt(listModel.chunkSize!!)) {
			binding.btnNext.visibility = View.GONE
			binding.btnContinue.visibility = View.VISIBLE
			firstListAdapter = OptionsFirstListAdapter(listModel.questions!!.subList(myPos, listModel.questions!!.size), myPos, listModel.questions!!.size, ctx, binding, act, listModel)
			binding.rvFirstList.adapter = firstListAdapter
		} else if (s > listModel.questions!!.size) {
			binding.btnNext.visibility = View.GONE
			binding.btnContinue.visibility = View.VISIBLE
			firstListAdapter = OptionsFirstListAdapter(listModel.questions!!.subList(myPos,listModel.questions!!.size), myPos, listModel.questions!!.size, ctx, binding, act, listModel)
			binding.rvFirstList.adapter = firstListAdapter
		}else if(myPos < listModel.questions!!.size) {
			binding.btnNext.visibility = View.VISIBLE
			binding.btnContinue.visibility = View.GONE
			firstListAdapter = OptionsFirstListAdapter(listModel.questions!!.subList(myPos, myPos + Integer.parseInt(listModel.chunkSize!!)), myPos,  myPos + Integer.parseInt(listModel.chunkSize!!), ctx, binding, act, listModel)
			binding.rvFirstList.adapter = firstListAdapter
		}
	}

	/* This is the first options box input layout */
	class OptionsFirstListAdapter(private val listModel: List<StepTypeTwoSaveDataModel.ResponseData.Question>?, private val myPos: Int, private val mypos2: Int, private val ctx: Context, var binding: ActivitySessionPersonalHistoryBinding, val act: Activity,val listModelMain: StepTypeTwoSaveDataModel.ResponseData) : RecyclerView.Adapter<OptionsFirstListAdapter.MyViewHolder>() {
		private var dass = SessionPersonalHistoryActivity()
		private lateinit var userTextWatcher: TextWatcher

		inner class MyViewHolder(var bindingAdapter: SessionPersonalHistoryBinding) : RecyclerView.ViewHolder(bindingAdapter.root) {
			init {
				userTextWatcher = object : TextWatcher {
					override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
					override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
						val ckName: String = bindingAdapter.edtFirstAnswer.text.toString().trim()

						when {
							ckName.equals("", ignoreCase = true) -> {
								checkBtnStatus(0)
							}
							else -> {
								setData()
								if (dass.progressReportQus.size == 0) {
									dass.progressReportQus.add(listModel!![absoluteAdapterPosition].questionId.toString())
									dass.progressReportAns.add(0, ckName)

								} else {
									if (dass.progressReportQus.contains(listModel!![absoluteAdapterPosition].questionId)) {
										for (i in 0 until dass.progressReportQus.size) {
											if (dass.progressReportQus[i] == listModel[absoluteAdapterPosition].questionId) {
												dass.progressReportAns.removeAt(i)
												dass.progressReportAns.add(i, ckName)
											}
										}
									} else {
										if (myPos > dass.progressReportQus.size) {
											dass.progressReportQus.add(myPos - 1, listModel[absoluteAdapterPosition].questionId.toString())
											dass.progressReportAns.add(myPos - 1,ckName)
										} else {
											dass.progressReportQus.add(myPos, listModel[absoluteAdapterPosition].questionId.toString())
											dass.progressReportAns.add(myPos, ckName)
										}
									}
								}

								Log.e("Qus", dass.progressReportQus.toString())
								Log.e("Ans", dass.progressReportAns.toString())
								visibleGoneNext()
								dass.editor = ctx.getSharedPreferences(CONSTANTS.ProgressReportMain, Context.MODE_PRIVATE).edit()
								dass.editor.putString(CONSTANTS.ProgressReportQus, dass.gson.toJson(dass.progressReportQus))
								dass.editor.putString(CONSTANTS.ProgressReportAns, dass.gson.toJson(dass.progressReportAns))
								dass.editor.apply()
								dass.editor.commit()
								checkBtnStatus(1)
							}
						}
					}

					override fun afterTextChanged(s: Editable) {}
				}
			}
		}

		/* This is the first options box input layout create */
		override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
			val v: SessionPersonalHistoryBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.session_personal_history, parent, false)
			return MyViewHolder(v)
		}

		/* This is the first options box set input layout */
		override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
			setData()
			if (myPos == 0) {
				binding.btnPrev.visibility = View.GONE
				holder.bindingAdapter.tvQusDesc.visibility = View.VISIBLE
			} else {
				binding.btnPrev.visibility = View.VISIBLE
				holder.bindingAdapter.tvQusDesc.visibility = View.GONE
			}
			if (listModel != null) {
				holder.bindingAdapter.tvSecond.text = listModel[position].question
				holder.bindingAdapter.tvQusTitle.text = listModel[position].question
				when {
					listModelMain.optionType.equals("tenoptions") -> {
						holder.bindingAdapter.llMainLayoutOne.visibility = View.VISIBLE
						holder.bindingAdapter.llMainLayoutTwo.visibility = View.GONE
						holder.bindingAdapter.rvSecondList.layoutManager = GridLayoutManager(ctx, 3)
						holder.bindingAdapter.rvSecondList.setBackgroundColor(ContextCompat.getColor(act, R.color.white))
						dass.secondListAdapter = OptionsSecondListAdapter(listModel[position], myPos, mypos2, ctx, binding, act,listModelMain)
						holder.bindingAdapter.rvSecondList.adapter = dass.secondListAdapter
					}
					listModelMain.optionType.equals("fiveoptions") -> {
						holder.bindingAdapter.llMainLayoutOne.visibility = View.VISIBLE
						holder.bindingAdapter.llMainLayoutTwo.visibility = View.GONE
						holder.bindingAdapter.rvSecondList.layoutManager = GridLayoutManager(ctx,1,  GridLayoutManager.HORIZONTAL,false)
						//                    holder.bindingAdapter.rvSecondList.layoutManager = LinearLayoutManager(ctx, LinearLayoutManager.HORIZONTAL, false)
						holder.bindingAdapter.rvSecondList.setBackgroundColor(ContextCompat.getColor(act, R.color.light_white))
						dass.secondListAdapter = OptionsSecondListAdapter(listModel[position], myPos, mypos2, ctx, binding, act,listModelMain)
						holder.bindingAdapter.rvSecondList.adapter = dass.secondListAdapter
					}
					listModelMain.optionType.equals("twooptions") -> {
						holder.bindingAdapter.llMainLayoutOne.visibility = View.VISIBLE
						holder.bindingAdapter.llMainLayoutTwo.visibility = View.GONE
						holder.bindingAdapter.rvSecondList.layoutManager = LinearLayoutManager(ctx, LinearLayoutManager.VERTICAL, false)
						holder.bindingAdapter.rvSecondList.setBackgroundColor(ContextCompat.getColor(act, R.color.white))
						dass.secondListAdapter = OptionsSecondListAdapter(listModel[position], myPos, mypos2, ctx, binding, act,listModelMain)
						holder.bindingAdapter.rvSecondList.adapter = dass.secondListAdapter
					}
					listModelMain.optionType.equals("textfield") -> {
						holder.bindingAdapter.llMainLayoutOne.visibility = View.GONE
						holder.bindingAdapter.llMainLayoutTwo.visibility = View.VISIBLE
					}
				}
				if(listModelMain.optionType.equals("textfield")){
					if (dass.progressReportQus.contains(listModel[position].questionId)) {
						for (i in 0 until dass.progressReportQus.size) {
							if (dass.progressReportQus[i] == listModel[position].questionId) {
								holder.bindingAdapter.edtFirstAnswer.setText(dass.progressReportAns[i])
								break
							}
						}
					}
					holder.bindingAdapter.edtFirstAnswer.addTextChangedListener(userTextWatcher)
				}
			}
		}
		private fun visibleGoneNext() {
			if (dass.progressReportQus.size >= mypos2) {
				binding.btnNext.isClickable = true
				binding.btnNext.isEnabled = true
				binding.btnNext.setColorFilter(ContextCompat.getColor(act, R.color.black), PorterDuff.Mode.SRC_ATOP)
				binding.btnContinue.isClickable = true
				binding.btnContinue.isEnabled = true
				binding.btnContinue.setBackgroundResource(R.drawable.light_green_rounded_filled)
			} else {
				binding.btnNext.isEnabled = false
				binding.btnNext.isClickable = false
				binding.btnNext.setColorFilter(ContextCompat.getColor(act, R.color.gray), PorterDuff.Mode.SRC_ATOP)
				binding.btnContinue.isEnabled = false
				binding.btnContinue.isClickable = false
				binding.btnContinue.setBackgroundResource(R.drawable.gray_round_cornor)
			}
		}
		private fun checkBtnStatus(check: Int) {
			if (check == 0) {
				binding.btnContinue.isEnabled = false
				binding.btnContinue.setTextColor(ContextCompat.getColor(act, R.color.white))
				binding.btnContinue.setBackgroundResource(R.drawable.gray_round_cornor)
			} else if (check == 1) {
				binding.btnContinue.isEnabled = true
				binding.btnContinue.setTextColor(ContextCompat.getColor(act, R.color.white))
				binding.btnContinue.setBackgroundResource(R.drawable.light_green_rounded_filled)
			}
		}
		private fun setData() {
			val shared = ctx.getSharedPreferences(CONSTANTS.ProgressReportMain, Context.MODE_PRIVATE)
			val json2 = shared.getString(CONSTANTS.ProgressReportQus, dass.gson.toString())
			val json3 = shared.getString(CONSTANTS.ProgressReportAns, dass.gson.toString())
			if (!json2.equals(dass.gson.toString(), ignoreCase = true)) {
				val type1 = object : TypeToken<ArrayList<String?>?>() {}.type
				dass.progressReportQus = dass.gson.fromJson(json2, type1)
				dass.progressReportAns = dass.gson.fromJson(json3, type1)
			}
			visibleGoneNext()
		}
		override fun getItemCount(): Int {
			return listModel!!.size
		}
	}

	/* This is the second options box input layout */
	class OptionsSecondListAdapter(val listModel: StepTypeTwoSaveDataModel.ResponseData.Question, val pos: Int, private val mmypos2: Int, val ctx: Context, var binding: ActivitySessionPersonalHistoryBinding, val act: Activity, val listModelMain: StepTypeTwoSaveDataModel.ResponseData) : RecyclerView.Adapter<OptionsSecondListAdapter.MyViewHolder>() {
		var mSelectedItem = -1
		var posItem: Int = -1

		var dass = SessionPersonalHistoryActivity()

		inner class MyViewHolder(var bindingAdapter: FormFillLayoutBinding) : RecyclerView.ViewHolder(bindingAdapter.root) {
			init {
				bindingAdapter.cbChecked.setOnClickListener {
					callCheckedBox(absoluteAdapterPosition)
				}
				bindingAdapter.rbOne.setOnClickListener {
					callCheckedBox(absoluteAdapterPosition)
				}
			}
		}

		private fun callCheckedBox(absoluteAdapterPosition: Int) {
			setData()
			mSelectedItem = absoluteAdapterPosition
			if (posItem != -1) notifyItemChanged(posItem)
			notifyItemChanged(mSelectedItem)
			posItem = mSelectedItem
			if (dass.progressReportQus.size == 0) {
				dass.progressReportQus.add(listModel.questionId.toString())
				dass.progressReportAns.add(0, absoluteAdapterPosition.toString())

			} else {
				if (dass.progressReportQus.contains(listModel.questionId)) {
					for (i in 0 until dass.progressReportQus.size) {
						if (dass.progressReportQus[i] == listModel.questionId) {
							dass.progressReportAns.removeAt(i)
							dass.progressReportAns.add(i, absoluteAdapterPosition.toString())
						}
					}
				} else {
					if (pos > dass.progressReportQus.size) {
						dass.progressReportQus.add(pos - 1, listModel.questionId.toString())
						dass.progressReportAns.add(pos - 1, absoluteAdapterPosition.toString())
					} else {
						dass.progressReportQus.add(pos, listModel.questionId.toString())
						dass.progressReportAns.add(pos, absoluteAdapterPosition.toString())
					}
				}
			}

			Log.e("Qus", dass.progressReportQus.toString())
			Log.e("Ans", dass.progressReportAns.toString())
			visibleGoneNext()
			dass.editor = ctx.getSharedPreferences(CONSTANTS.ProgressReportMain, Context.MODE_PRIVATE).edit()
			dass.editor.putString(CONSTANTS.ProgressReportQus, dass.gson.toJson(dass.progressReportQus))
			dass.editor.putString(CONSTANTS.ProgressReportAns, dass.gson.toJson(dass.progressReportAns))
			dass.editor.apply()
			dass.editor.commit()
		}

		/* This is the second options box input layout create */
		override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
			val v: FormFillLayoutBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.form_fill_layout, parent, false)
			return MyViewHolder(v)
		}

		/* This is the second options set box input layout */
		override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
			setData()
			when {
				listModelMain.optionType.equals("tenoptions") -> {
					holder.bindingAdapter.llOldRadio.visibility = View.VISIBLE
					holder.bindingAdapter.llTopRadio.visibility = View.GONE
				}
				listModelMain.optionType.equals("fiveoptions") -> {
					holder.bindingAdapter.llOldRadio.visibility = View.GONE
					holder.bindingAdapter.llTopRadio.visibility = View.VISIBLE
				}
				listModelMain.optionType.equals("twooptions") -> {
					holder.bindingAdapter.llOldRadio.visibility = View.VISIBLE
					holder.bindingAdapter.llTopRadio.visibility = View.GONE
				}
			}
			holder.bindingAdapter.tvOne.text =listModel.questionOptions!![position].replace(" ","\n")
			holder.bindingAdapter.cbChecked.text = listModel.questionOptions!![position]
			if (dass.progressReportQus.contains(listModel.questionId)) {
				for (i in 0 until dass.progressReportQus.size) {
					if (dass.progressReportQus[i] == listModel.questionId) {
						posItem = Integer.parseInt(dass.progressReportAns[i])
						mSelectedItem = posItem
						break
					}
				}
			}
			if (position == posItem) {
				holder.bindingAdapter.cbChecked.isChecked = position == posItem
				holder.bindingAdapter.rbOne.isChecked = position == posItem
			} else {
				holder.bindingAdapter.cbChecked.isChecked = false
				holder.bindingAdapter.rbOne.isChecked = false
			}
		}

		/* This function is set que, ans & arranging assessment data */
		private fun setData() {
			val shared = ctx.getSharedPreferences(CONSTANTS.ProgressReportMain, Context.MODE_PRIVATE)
			val json2 = shared.getString(CONSTANTS.ProgressReportQus, dass.gson.toString())
			val json3 = shared.getString(CONSTANTS.ProgressReportAns, dass.gson.toString())
			if (!json2.equals(dass.gson.toString(), ignoreCase = true)) {
				val type1 = object : TypeToken<ArrayList<String?>?>() {}.type
				dass.progressReportQus = dass.gson.fromJson(json2, type1)
				dass.progressReportAns = dass.gson.fromJson(json3, type1)
			}
			visibleGoneNext()
		}

		/* This function is visible & gone next button */
		private fun visibleGoneNext() {
			if (dass.progressReportQus.size >= mmypos2) {
				binding.btnNext.isClickable = true
				binding.btnNext.isEnabled = true
				binding.btnNext.setColorFilter(ContextCompat.getColor(act, R.color.black), PorterDuff.Mode.SRC_ATOP)
				binding.btnContinue.isClickable = true
				binding.btnContinue.isEnabled = true
				binding.btnContinue.setBackgroundResource(R.drawable.light_green_rounded_filled)
			} else {
				binding.btnNext.isEnabled = false
				binding.btnNext.isClickable = false
				binding.btnNext.setColorFilter(ContextCompat.getColor(act, R.color.gray), PorterDuff.Mode.SRC_ATOP)
				binding.btnContinue.isEnabled = false
				binding.btnContinue.isClickable = false
				binding.btnContinue.setBackgroundResource(R.drawable.gray_round_cornor)
			}
		}

		override fun getItemCount(): Int {
			return listModelMain.questions!![0].questionOptions!!.size
		}
	}
}