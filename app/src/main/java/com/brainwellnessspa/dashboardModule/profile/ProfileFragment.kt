package com.brainwellnessspa.dashboardModule.profile

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.SystemClock
import android.provider.MediaStore
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.brainwellnessspa.BWSApplication.*
import com.brainwellnessspa.BuildConfig
import com.brainwellnessspa.R
import com.brainwellnessspa.billingOrderModule.activities.BillingOrderActivity
import com.brainwellnessspa.dashboardModule.models.AddProfileModel
import com.brainwellnessspa.dashboardModule.models.SucessModel
import com.brainwellnessspa.databinding.FragmentProfileBinding
import com.brainwellnessspa.downloadModule.activities.DownloadsActivity
import com.brainwellnessspa.faqModule.activities.FaqActivity
import com.brainwellnessspa.invoiceModule.activities.InvoiceActivity
import com.brainwellnessspa.membershipModule.activities.EnhanceActivity
import com.brainwellnessspa.reminderModule.activities.ReminderListsActivity
import com.brainwellnessspa.resourceModule.activities.ResourceActivity
import com.brainwellnessspa.userModule.accountInfo.AccountInfoActivity
import com.brainwellnessspa.userModule.models.AuthOtpModel
import com.brainwellnessspa.userModule.models.RemoveProfileModel
import com.brainwellnessspa.userModule.signupLogin.SignInActivity
import com.brainwellnessspa.utility.APIClientProfile.apiService
import com.brainwellnessspa.utility.APINewClient
import com.brainwellnessspa.utility.CONSTANTS
import com.brainwellnessspa.utility.FileUtil.getPath
import com.brainwellnessspa.utility.RequestPermissionHandler
import com.brainwellnessspa.utility.RequestPermissionHandler.RequestPermissionListener
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.installations.FirebaseInstallations
import com.google.firebase.installations.InstallationTokenResult
import com.google.gson.Gson
import com.segment.analytics.Properties
import retrofit.RetrofitError
import retrofit.mime.TypedFile
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class ProfileFragment : Fragment() {
    lateinit var binding: FragmentProfileBinding
    private var mLastClickTime: Long = 0
    private var logoutDialog: Dialog? = null
    lateinit var image: File
    lateinit var ctx: Context
    lateinit var act: Activity
    private var mRequestPermissionHandler: RequestPermissionHandler? = null
    private lateinit var options: Array<String>
    var userId: String? = null
    var coUserId: String? = null
    private var userEmail: String? = null
    private var userImage: String? = null
    private var deviceId: String? = null
    private var deviceType: String? = null
    var userName: String? = null
    var profilePicPath: String? = ""
    var userMobile: String? = null
    var isProfileCompleted: String? = null
    var isAssessmentCompleted: String? = null
    var indexScore: String? = null
    var scoreLevel: String? = null
    var avgSleepTime: String? = null
    var areaOfFocus: String? = ""

    //    areaOfFocus
    @SuppressLint("SetTextI18n")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false)
        val view = binding.root
        ctx = requireActivity()
        act = requireActivity()
        val shared1 = requireActivity().getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, Context.MODE_PRIVATE)
        userId = shared1.getString(CONSTANTS.PREFE_ACCESS_mainAccountID, "")
        coUserId = shared1.getString(CONSTANTS.PREFE_ACCESS_UserId, "")
        userName = shared1.getString(CONSTANTS.PREFE_ACCESS_NAME, "")
        userEmail = shared1.getString(CONSTANTS.PREFE_ACCESS_USEREMAIL, "")
        userImage = shared1.getString(CONSTANTS.PREFE_ACCESS_IMAGE, "")
        userMobile = shared1.getString(CONSTANTS.PREFE_ACCESS_MOBILE, "")
        isProfileCompleted = shared1.getString(CONSTANTS.PREFE_ACCESS_ISPROFILECOMPLETED, "")
        isAssessmentCompleted = shared1.getString(CONSTANTS.PREFE_ACCESS_ISAssCOMPLETED, "")
        indexScore = shared1.getString(CONSTANTS.PREFE_ACCESS_INDEXSCORE, "")
        scoreLevel = shared1.getString(CONSTANTS.PREFE_ACCESS_SCORELEVEL, "")
        scoreLevel = shared1.getString(CONSTANTS.PREFE_ACCESS_SCORELEVEL, "")
        deviceId = shared1.getString(CONSTANTS.PREFE_ACCESS_DeviceID, "")
        deviceType = shared1.getString(CONSTANTS.PREFE_ACCESS_DeviceType, "")
        val gson = Gson()
        val json5 = shared1.getString(CONSTANTS.PREFE_ACCESS_AreaOfFocus, gson.toString())
        if (!json5.equals(gson.toString(), ignoreCase = true)) areaOfFocus = json5
        val shared = requireActivity().getSharedPreferences(CONSTANTS.RecommendedCatMain, Context.MODE_PRIVATE)
        avgSleepTime = shared.getString(CONSTANTS.PREFE_ACCESS_SLEEPTIME, "")
        binding.tvName.text = userName
        mRequestPermissionHandler = RequestPermissionHandler()
        binding.tvVersion.text = "Version " + BuildConfig.VERSION_NAME
        val name: String?
        profilePicPath = userImage
        if (isNetworkConnected(requireActivity())) {
            if (profilePicPath.equals("", ignoreCase = true)) {
                binding.civProfile.visibility = View.GONE
                name = if (userName.equals("", ignoreCase = true)) {
                    "Guest"
                } else {
                    userName
                }
                val letter = name!!.substring(0, 1)
                binding.rlLetter.visibility = View.VISIBLE
                binding.tvLetter.text = letter
            } else {
                binding.civProfile.visibility = View.VISIBLE
                binding.rlLetter.visibility = View.GONE
                setProfilePic(profilePicPath)
            }
        } else {
            if (userName.equals("", ignoreCase = true) || userName.equals(" ", ignoreCase = true) || userName == null) {
                binding.tvName.setText(R.string.Guest)
            } else {
                binding.tvName.text = userName
            }
            binding.civProfile.visibility = View.GONE
            name = if (userName.equals("", ignoreCase = true)) {
                "Guest"
            } else {
                userName
            }
            val letter = name!!.substring(0, 1)
            binding.rlLetter.visibility = View.VISIBLE
            binding.tvLetter.text = letter
        }
        profileViewData()
        binding.llImageUpload.setOnClickListener {
            if (IsLock.equals("1")) {
                callEnhanceActivity(ctx, act)
            } else if (IsLock.equals("0")) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return@setOnClickListener
                }
                mLastClickTime = SystemClock.elapsedRealtime()
                if (isNetworkConnected(requireActivity())) {
                    selectImage()
                } else {
                    showToast(requireActivity().getString(R.string.no_server_found), requireActivity())
                }
            }
        }
        val p = Properties()
        addToSegment("Account Screen Viewed", p, CONSTANTS.screen)
        binding.llAcInfo.setOnClickListener {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return@setOnClickListener
            }
            mLastClickTime = SystemClock.elapsedRealtime()
            if (isNetworkConnected(requireActivity())) {
                val i = Intent(requireActivity(), AccountInfoActivity::class.java)
                i.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                startActivity(i)
                requireActivity().overridePendingTransition(0, 0)
            } else {
                showToast(requireActivity().getString(R.string.no_server_found), requireActivity())
            }
        }

        binding.llDownloads.setOnClickListener {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return@setOnClickListener
            }
            mLastClickTime = SystemClock.elapsedRealtime()
            val i = Intent(requireActivity(), DownloadsActivity::class.java)
            i.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(i)
            requireActivity().overridePendingTransition(0, 0)
        }

        binding.llInvoices.setOnClickListener {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return@setOnClickListener
            }
            mLastClickTime = SystemClock.elapsedRealtime()
            if (isNetworkConnected(requireActivity())) {
                InvoiceActivity.invoiceToRecepit = 1
                val i = Intent(requireActivity(), InvoiceActivity::class.java)
                i.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                i.putExtra("ComeFrom", "")
                startActivity(i)
                requireActivity().overridePendingTransition(0, 0)
            } else {
                showToast(requireActivity().getString(R.string.no_server_found), requireActivity())
            }
        }

        binding.llBillingOrder.setOnClickListener {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return@setOnClickListener
            }
            mLastClickTime = SystemClock.elapsedRealtime()
            if (isNetworkConnected(requireActivity())) {
                val i = Intent(requireActivity(), BillingOrderActivity::class.java)
                i.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                startActivity(i)
                requireActivity().overridePendingTransition(0, 0)
            } else {
                showToast(requireActivity().getString(R.string.no_server_found), requireActivity())
            }
        }

        binding.llManageUser.setOnClickListener {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return@setOnClickListener
            }
            mLastClickTime = SystemClock.elapsedRealtime()
            if (isNetworkConnected(requireActivity())) {
                val i = Intent(requireActivity(), ManageUserActivity::class.java)
                i.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                startActivity(i)
                requireActivity().overridePendingTransition(0, 0)
            } else {
                showToast(requireActivity().getString(R.string.no_server_found), requireActivity())
            }
        }

        binding.llReminder.setOnClickListener {
            ComeScreenReminder = 1
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return@setOnClickListener
            }
            mLastClickTime = SystemClock.elapsedRealtime()
            if (isNetworkConnected(requireActivity())) {
                val i = Intent(requireActivity(), ReminderListsActivity::class.java)
                i.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                startActivity(i)
                requireActivity().overridePendingTransition(0, 0)
            } else {
                showToast(requireActivity().getString(R.string.no_server_found), requireActivity())
            }
        }

        binding.llPlan.setOnClickListener {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return@setOnClickListener
            }
            mLastClickTime = SystemClock.elapsedRealtime()
            if (isNetworkConnected(requireActivity())) {
                IsBackFromEnhance = "0"
                val i = Intent(requireActivity(), EnhanceActivity::class.java)
                i.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                i.putExtra("plan", "0")
                startActivity(i)
                requireActivity().overridePendingTransition(0, 0)
            } else {
                showToast(requireActivity().getString(R.string.no_server_found), requireActivity())
            }
        }

        /*  binding.llPlan1.setOnClickListener {
              if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                  return@setOnClickListener
              }
              mLastClickTime = SystemClock.elapsedRealtime()
              if (isNetworkConnected(activity)) {
                  val i = Intent(activity, EnhanceActivity::class.java)
               i.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                  i.putExtra("plan","1")
                  startActivity(i)
                  requireActivity().overridePendingTransition(0, 0)
              } else {
                  showToast(getString(R.string.no_server_found), activity)
              }
          }*/

        binding.llResources.setOnClickListener {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return@setOnClickListener
            }
            mLastClickTime = SystemClock.elapsedRealtime()
            if (isNetworkConnected(requireActivity())) {
                val i = Intent(requireActivity(), ResourceActivity::class.java)
                i.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                startActivity(i)
                requireActivity().overridePendingTransition(0, 0)
            } else {
                showToast(requireActivity().getString(R.string.no_server_found), requireActivity())
            }
        }

        binding.llFAQ.setOnClickListener {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return@setOnClickListener
            }
            mLastClickTime = SystemClock.elapsedRealtime()
            if (isNetworkConnected(requireActivity())) {
                val i = Intent(requireActivity(), FaqActivity::class.java)
                i.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                startActivity(i)
                requireActivity().overridePendingTransition(0, 0)
            } else {
                showToast(requireActivity().getString(R.string.no_server_found), requireActivity())
            }
        }

        binding.llLogOut.setOnClickListener {
            if (isNetworkConnected(requireActivity())) {
                logoutDialog = Dialog(requireActivity())
                logoutDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
                logoutDialog!!.setContentView(R.layout.logout_layout)
                logoutDialog!!.window!!.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(requireActivity(), R.color.dark_blue_gray)))
                logoutDialog!!.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                val tvGoBack = logoutDialog!!.findViewById<TextView>(R.id.tvGoBack)
                val btn = logoutDialog!!.findViewById<Button>(R.id.Btn)
                val progressBar = logoutDialog!!.findViewById<ProgressBar>(R.id.progressBar)
                val progressBarHolder = logoutDialog!!.findViewById<FrameLayout>(R.id.progressBarHolder)
                logoutDialog!!.setOnKeyListener { _: DialogInterface?, keyCode: Int, _: KeyEvent? ->
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        logoutDialog!!.hide()
                        return@setOnKeyListener true
                    }
                    false
                }
                btn.setOnClickListener {
                    logoutDialog!!.hide()
                    showProgressBar(progressBar, progressBarHolder, requireActivity())
                    deleteCall(logoutDialog!!, progressBar, progressBarHolder)
                }
                tvGoBack.setOnClickListener { logoutDialog!!.hide() }
                logoutDialog!!.show()
                logoutDialog!!.setCancelable(false)
            } else {
                showToast(requireActivity().getString(R.string.no_server_found), requireActivity())
            }
        }
        return view
    }

    override fun onResume() {
        profileViewData()
        super.onResume()
    }

    private fun selectImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                callProfilePathSet()
            } else {
                mRequestPermissionHandler!!.requestPermission(requireActivity(), arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA), 123, object : RequestPermissionListener {
                    override fun onSuccess() {
                        callProfilePathSet()
                    }

                    override fun onFailed() {}
                })
            }
        } else if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                callProfilePathSet()
            } else {
                if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    mRequestPermissionHandler!!.requestPermission(requireActivity(), arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE), 123, object : RequestPermissionListener {
                        override fun onSuccess() {
                            callProfilePathSet()
                        }

                        override fun onFailed() {}
                    })
                } else if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
                    callCamaraPermission()
                } else if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                    callReadPermission()
                }
            }
        } else {
            callProfilePathSet()
        }
    }

    private fun callCamaraPermission() {
        val building = AlertDialog.Builder(requireActivity())
        building.setMessage("""To camera allow ${requireActivity().getString(R.string.app_name)} access to your camera. Tap Setting > permission, and turn Camera on.""")
        building.setCancelable(true)
        building.setPositiveButton(requireActivity().getString(R.string.Settings)) { dialogs: DialogInterface, _: Int ->
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri = Uri.fromParts("package", requireActivity().packageName, null)
            intent.data = uri
            startActivity(intent)
            dialogs.dismiss()
        }
        building.setNegativeButton(requireActivity().getString(R.string.not_now)) { dialogs: DialogInterface, _: Int -> dialogs.dismiss() }
        val alert11 = building.create()
        alert11.window!!.setBackgroundDrawableResource(R.drawable.dialog_bg)
        alert11.show()
        alert11.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(requireActivity(), R.color.blue))
        alert11.getButton(android.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(requireActivity(), R.color.blue))
    }

    private fun callReadPermission() {
        val buildable = AlertDialog.Builder(requireActivity())
        buildable.setMessage("""To upload image allow ${requireActivity().getString(R.string.app_name)} access to your device's files. 
Tap Setting > permission, and turn "Files and media" on.""")
        buildable.setCancelable(true)
        buildable.setPositiveButton(requireActivity().getString(R.string.Settings)) { dialogs: DialogInterface, _: Int ->
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri = Uri.fromParts("package", requireActivity().packageName, null)
            intent.data = uri
            startActivity(intent)
            dialogs.dismiss()
        }
        buildable.setNegativeButton(requireActivity().getString(R.string.not_now)) { dialogue: DialogInterface, _: Int -> dialogue.dismiss() }
        val alert11 = buildable.create()
        alert11.window!!.setBackgroundDrawableResource(R.drawable.dialog_bg)
        alert11.show()
        alert11.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(requireActivity(), R.color.blue))
        alert11.getButton(android.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(requireActivity(), R.color.blue))
    }

    private fun callProfilePathSet() {
        options = if (profilePicPath == "") {
            arrayOf(getString(R.string.takePhoto), getString(R.string.chooseFromGallary), getString(R.string.cancel))
        } else {
            arrayOf(getString(R.string.takePhoto), getString(R.string.chooseFromGallary), getString(R.string.removeProfilePicture), getString(R.string.cancel))
        }
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle(R.string.addPhoto)
        builder.setItems(options) { dialog: DialogInterface, item: Int ->
            if (options[item] == getString(R.string.takePhoto)) {
                val pictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                if (pictureIntent.resolveActivity(requireActivity().packageManager) != null) {
                    var photoFile: File? = null
                    try {
                        photoFile = createImageFile()
                    } catch (ex: IOException) {
                        ex.printStackTrace()
                    }
                    if (photoFile != null) {
                        val photoURI = FileProvider.getUriForFile(requireActivity(), BuildConfig.APPLICATION_ID + ".provider", photoFile)
                        pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                        startActivityForResult(pictureIntent, CONTENT_REQUEST)
                    }
                }
            } else if (options[item] == getString(R.string.chooseFromGallary)) {
                val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(intent, 2)
            } else if (options[item] == getString(R.string.removeProfilePicture)) {
                if (isNetworkConnected(requireActivity())) {
                    showProgressBar(binding.progressBar, binding.progressBarHolder, requireActivity())
                    val listCall = APINewClient.client.getRemoveProfile(coUserId)
                    listCall.enqueue(object : Callback<RemoveProfileModel?> {
                        override fun onResponse(call: Call<RemoveProfileModel?>, response: Response<RemoveProfileModel?>) {
                            try {
                                val viewModel = response.body()
                                hideProgressBar(binding.progressBar, binding.progressBarHolder, requireActivity())
                                if (viewModel != null) {
                                    if (viewModel.responseCode.equals(activity?.getString(R.string.ResponseCodesuccess), ignoreCase = true)) {
                                        showToast(viewModel.responseMessage, requireActivity())
                                        val shared = requireActivity().getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, Context.MODE_PRIVATE)
                                        val editor = shared.edit()
                                        editor.putString(CONSTANTS.PREFE_ACCESS_IMAGE, "")
                                        editor.apply()
                                        profileViewData()
                                    } else if (viewModel.responseCode.equals(activity?.getString(R.string.ResponseCodeDeleted), ignoreCase = true)) {
                                        deleteCall(requireActivity())
                                        showToast(viewModel.responseMessage, requireActivity())
                                        val i = Intent(requireActivity(), SignInActivity::class.java)
                                        i.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                                        i.putExtra("mobileNo", "")
                                        i.putExtra("countryCode", "")
                                        i.putExtra("name", "")
                                        i.putExtra("email", "")
                                        i.putExtra("countryShortName", "")
                                        startActivity(i)
                                        requireActivity().finish()
                                    }
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }

                        override fun onFailure(call: Call<RemoveProfileModel?>, t: Throwable) {
                            hideProgressBar(binding.progressBar, binding.progressBarHolder, requireActivity())
                        }
                    })
                }
            } else if (options[item] == getString(R.string.cancel)) {
//                val p = Properties()
//                addToSegment("Profile Photo Cancelled", p, CONSTANTS.track)
                dialog.dismiss()
            }
        }
        val alert11 = builder.create()
        alert11.window!!.setBackgroundDrawableResource(R.drawable.dialog_bg)
        alert11.show()
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "IMG_" + timeStamp + "_"
        val storageDir = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        image = File.createTempFile(imageFileName, ".jpg", storageDir)
        profilePicPath = image.absolutePath
        return image
    }

    fun profileViewData() {
        if (isNetworkConnected(requireActivity())) {
            showProgressBar(binding.progressBar, binding.progressBarHolder, requireActivity())
            APINewClient.client.getCoUserDetails(coUserId).enqueue(object : Callback<AuthOtpModel?> {
                override fun onResponse(call: Call<AuthOtpModel?>, response: Response<AuthOtpModel?>) {
                    try {
                        val viewModel = response.body()
                        if (viewModel != null) {
                            when {
                                viewModel.ResponseCode.equals(activity?.getString(R.string.ResponseCodesuccess), ignoreCase = true) -> {
                                    hideProgressBar(binding.progressBar, binding.progressBarHolder, requireActivity())
                                    if (viewModel.ResponseData.Name.equals("", ignoreCase = true) || viewModel.ResponseData.Name.equals(" ", ignoreCase = true)) {
                                        binding.tvName.setText(R.string.Guest)
                                    } else {
                                        binding.tvName.text = viewModel.ResponseData.Name
                                    }

                                    IsLock = viewModel.ResponseData.Islock
                                    if (viewModel.ResponseData.isMainAccount.equals("1", ignoreCase = true)) {
                                        binding.llManageUser.visibility = View.VISIBLE
                                        binding.llBillingOrder.visibility = View.VISIBLE
                                        binding.llPlan.visibility = View.VISIBLE
                                        binding.viewManage.visibility = View.VISIBLE
                                        binding.viewBillingOrder.visibility = View.VISIBLE
                                    } else {
                                        binding.llManageUser.visibility = View.GONE
                                        binding.llBillingOrder.visibility = View.GONE
                                        binding.llPlan.visibility = View.GONE
                                        binding.viewManage.visibility = View.GONE
                                        binding.viewBillingOrder.visibility = View.GONE
                                    }

                                    val name: String
                                    profilePicPath = viewModel.ResponseData.Image
                                    if (profilePicPath.equals("", ignoreCase = true)) {
                                        binding.civProfile.visibility = View.GONE
                                        name = if (viewModel.ResponseData.Name.equals("", ignoreCase = true)) {
                                            "Guest"
                                        } else {
                                            viewModel.ResponseData.Name
                                        }
                                        val letter = name.substring(0, 1)
                                        binding.rlLetter.visibility = View.VISIBLE
                                        binding.tvLetter.text = letter
                                    } else {
                                        binding.civProfile.visibility = View.VISIBLE
                                        binding.rlLetter.visibility = View.GONE
                                        setProfilePic(profilePicPath)
                                    }

                                    val shared = requireActivity().getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, Context.MODE_PRIVATE)
                                    val editor = shared?.edit()
                                    if (editor != null) {
                                        editor.putString(CONSTANTS.PREFE_ACCESS_mainAccountID, viewModel.ResponseData.MainAccountID)
                                        editor.putString(CONSTANTS.PREFE_ACCESS_UserId, viewModel.ResponseData.UserId)
                                        editor.putString(CONSTANTS.PREFE_ACCESS_EMAIL, viewModel.ResponseData.Email)
                                        editor.putString(CONSTANTS.PREFE_ACCESS_NAME, viewModel.ResponseData.Name)
                                        editor.putString(CONSTANTS.PREFE_ACCESS_MOBILE, viewModel.ResponseData.Mobile)
                                        editor.putString(CONSTANTS.PREFE_ACCESS_SLEEPTIME, viewModel.ResponseData.AvgSleepTime)
                                        editor.putString(CONSTANTS.PREFE_ACCESS_INDEXSCORE, viewModel.ResponseData.indexScore)
                                        editor.putString(CONSTANTS.PREFE_ACCESS_SCORELEVEL, viewModel.ResponseData.ScoreLevel)
                                        editor.putString(CONSTANTS.PREFE_ACCESS_IMAGE, viewModel.ResponseData.Image)
                                        editor.putString(CONSTANTS.PREFE_ACCESS_ISPROFILECOMPLETED, viewModel.ResponseData.isProfileCompleted)
                                        editor.putString(CONSTANTS.PREFE_ACCESS_ISAssCOMPLETED, viewModel.ResponseData.isAssessmentCompleted)
                                        editor.putString(CONSTANTS.PREFE_ACCESS_directLogin, viewModel.ResponseData.directLogin)
                                        editor.putString(CONSTANTS.PREFE_ACCESS_isPinSet, viewModel.ResponseData.isPinSet)
                                        editor.putString(CONSTANTS.PREFE_ACCESS_isEmailVerified, viewModel.ResponseData.isEmailVerified)
                                        editor.putString(CONSTANTS.PREFE_ACCESS_isMainAccount, viewModel.ResponseData.isMainAccount)
                                        editor.putString(CONSTANTS.PREFE_ACCESS_coUserCount, viewModel.ResponseData.CoUserCount)
                                        try {
                                            if (viewModel.ResponseData.planDetails.isNotEmpty()) {
                                                editor.putString(CONSTANTS.PREFE_ACCESS_PlanId, viewModel.ResponseData.planDetails[0].PlanId)
                                                editor.putString(CONSTANTS.PREFE_ACCESS_PlanPurchaseDate, viewModel.ResponseData.planDetails[0].PlanPurchaseDate)
                                                editor.putString(CONSTANTS.PREFE_ACCESS_PlanExpireDate, viewModel.ResponseData.planDetails[0].PlanExpireDate)
                                                editor.putString(CONSTANTS.PREFE_ACCESS_TransactionId, viewModel.ResponseData.planDetails[0].TransactionId)
                                                editor.putString(CONSTANTS.PREFE_ACCESS_TrialPeriodStart, viewModel.ResponseData.planDetails[0].TrialPeriodStart)
                                                editor.putString(CONSTANTS.PREFE_ACCESS_TrialPeriodEnd, viewModel.ResponseData.planDetails[0].TrialPeriodEnd)
                                                editor.putString(CONSTANTS.PREFE_ACCESS_PlanStatus, viewModel.ResponseData.planDetails[0].PlanStatus)
                                                editor.putString(CONSTANTS.PREFE_ACCESS_PlanContent, viewModel.ResponseData.planDetails[0].PlanContent)
                                            }
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                        }
                                        editor.apply()
                                    }

                                    val sharded = requireActivity().getSharedPreferences(CONSTANTS.RecommendedCatMain, Context.MODE_PRIVATE)
                                    val edited = sharded?.edit()
                                    if (edited != null) {
                                        edited.putString(CONSTANTS.PREFE_ACCESS_SLEEPTIME, viewModel.ResponseData.AvgSleepTime)
                                        val selectedCategoriesTitle = arrayListOf<String>()
                                        val selectedCategoriesName = arrayListOf<String>()
                                        val gson = Gson()
                                        for (i in viewModel.ResponseData.AreaOfFocus) {
                                            selectedCategoriesTitle.add(i.MainCat)
                                            selectedCategoriesName.add(i.RecommendedCat)
                                        }
                                        edited.putString(CONSTANTS.selectedCategoriesTitle, gson.toJson(selectedCategoriesTitle)) //Friend
                                        edited.putString(CONSTANTS.selectedCategoriesName, gson.toJson(selectedCategoriesName)) //Friend
                                        edited.apply()
                                    }
                                }
                                viewModel.ResponseCode.equals(activity?.getString(R.string.ResponseCodeDeleted), ignoreCase = true) -> {
                                    deleteCall(requireActivity())
                                    showToast(viewModel.ResponseMessage, requireActivity())
                                    val i = Intent(requireActivity(), SignInActivity::class.java)
                                    i.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                                    i.putExtra("mobileNo", "")
                                    i.putExtra("countryCode", "")
                                    i.putExtra("name", "")
                                    i.putExtra("email", "")
                                    i.putExtra("countryShortName", "")
                                    requireActivity().startActivity(i)
                                    requireActivity().finish()
                                }
                                else -> {
                                    hideProgressBar(binding.progressBar, binding.progressBarHolder, requireActivity())
                                }
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onFailure(call: Call<AuthOtpModel?>, t: Throwable) {
                    hideProgressBar(binding.progressBar, binding.progressBarHolder, requireActivity())
                }
            })
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CONTENT_REQUEST && resultCode == Activity.RESULT_OK) {
            try {
                showProgressBar(binding.progressBar, binding.progressBarHolder, requireActivity())
                setProfilePic(profilePicPath)
                if (isNetworkConnected(requireActivity())) {
                    val map = HashMap<String, String?>()
                    map[CONSTANTS.PREF_KEY_UserID] = coUserId
                    val typedFile = TypedFile(CONSTANTS.MULTIPART_FORMAT, image)
                    apiService!!.getAddProfiles(coUserId, typedFile, object : retrofit.Callback<AddProfileModel> {
                        override fun success(addProfileModel: AddProfileModel, response: retrofit.client.Response) {
                            try {
                                if (addProfileModel.responseCode.equals(activity?.getString(R.string.ResponseCodesuccess), ignoreCase = true)) {
                                    showToast(addProfileModel.responseMessage, requireActivity())
//                                    val p = Properties()
//                                    addToSegment("Camera Photo Added", p, CONSTANTS.track)

                                    Glide.with(requireActivity()).load(addProfileModel.responseData?.profileImage).thumbnail(0.10f).apply(RequestOptions.bitmapTransform(RoundedCorners(126))).into(binding.civProfile)
                                    profileViewData()
                                    val shared = requireActivity().getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, Context.MODE_PRIVATE)
                                    val editor = shared.edit()
                                    editor.putString(CONSTANTS.PREFE_ACCESS_IMAGE, addProfileModel.responseData?.profileImage)
                                    editor.apply()
                                    hideProgressBar(binding.progressBar, binding.progressBarHolder, requireActivity())
                                } else if (addProfileModel.responseCode.equals(activity?.getString(R.string.ResponseCodeDeleted), ignoreCase = true)) {
                                    deleteCall(requireActivity())
                                    showToast(addProfileModel.responseMessage, requireActivity())
                                    val i = Intent(requireActivity(), SignInActivity::class.java)
                                    i.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                                    i.putExtra("mobileNo", "")
                                    i.putExtra("countryCode", "")
                                    i.putExtra("name", "")
                                    i.putExtra("email", "")
                                    i.putExtra("countryShortName", "")
                                    startActivity(i)
                                    requireActivity().finish()
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }

                        override fun failure(e: RetrofitError) {
                            hideProgressBar(binding.progressBar, binding.progressBarHolder, requireActivity())
                            showToast(e.message, requireActivity())
                        }
                    })
                } else {
                    showToast(getString(R.string.no_server_found), requireActivity())
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("Permission", e.message!!)
            }
        } else if (requestCode == 2 && resultCode == Activity.RESULT_OK) {
            try {
                if (data != null) {
                    val selectedImageUri = data.data
                    showProgressBar(binding.progressBar, binding.progressBarHolder, requireActivity())
                    setProfilePic(selectedImageUri.toString())
                    try {
                        if (isNetworkConnected(requireActivity())) {
                            val map = HashMap<String, String?>()
                            map[CONSTANTS.PREF_KEY_UserID] = coUserId
                            val file = File(Objects.requireNonNull(getPath(selectedImageUri!!, requireActivity())))
                            val typedFile = TypedFile(CONSTANTS.MULTIPART_FORMAT, file)
                            apiService!!.getAddProfiles(coUserId, typedFile, object : retrofit.Callback<AddProfileModel> {
                                override fun success(addProfileModel: AddProfileModel, response: retrofit.client.Response) {
                                    if (addProfileModel.responseCode.equals(activity?.getString(R.string.ResponseCodesuccess), ignoreCase = true)) {
                                        profilePicPath = addProfileModel.responseData?.profileImage
                                        setProfilePic(profilePicPath)
                                        profileViewData()
//                                val p = Properties()
//                                addToSegment("Gallery Photo Added", p, CONSTANTS.track)
                                        showToast(addProfileModel.responseMessage, requireActivity())
                                        val shared = requireActivity().getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, Context.MODE_PRIVATE)
                                        val editor = shared.edit()
                                        editor.putString(CONSTANTS.PREFE_ACCESS_IMAGE, addProfileModel.responseData?.profileImage)
                                        editor.apply()
                                        hideProgressBar(binding.progressBar, binding.progressBarHolder, requireActivity())
                                    } else if (addProfileModel.responseCode.equals(activity?.getString(R.string.ResponseCodeDeleted), ignoreCase = true)) {
                                        deleteCall(requireActivity())
                                        showToast(addProfileModel.responseMessage, requireActivity())
                                        val i = Intent(requireActivity(), SignInActivity::class.java)
                                        i.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                                        i.putExtra("mobileNo", "")
                                        i.putExtra("countryCode", "")
                                        i.putExtra("name", "")
                                        i.putExtra("email", "")
                                        i.putExtra("countryShortName", "")
                                        startActivity(i)
                                        requireActivity().finish()
                                    }
                                }

                                override fun failure(e: RetrofitError) {
                                    showToast(e.message, requireActivity())
                                    hideProgressBar(binding.progressBar, binding.progressBarHolder, requireActivity())
                                }
                            })
                        } else {
                            showToast(requireActivity().getString(R.string.no_server_found), requireActivity())
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("Permission", e.message!!)
            }
        } else if (requestCode == Activity.RESULT_CANCELED) {
//            val p = Properties()
//            addToSegment("Profile Photo Cancelled", p, CONSTANTS.track)
            requireActivity().finish()
        }
    }

    private fun setProfilePic(profilePicPath: String?) {
        Glide.with(requireActivity()).load(profilePicPath).thumbnail(0.10f).apply(RequestOptions.bitmapTransform(RoundedCorners(126))).into(binding.civProfile)
    }

    private fun deleteCall(dialog: Dialog, progressBar: ProgressBar, progressBarHolder: FrameLayout) {
        val preferences = requireActivity().getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, Context.MODE_PRIVATE)
        val edit = preferences.edit()
        edit.remove(CONSTANTS.PREFE_ACCESS_mainAccountID)
        edit.remove(CONSTANTS.PREFE_ACCESS_UserId)
        edit.remove(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER)
        edit.remove(CONSTANTS.PREFE_ACCESS_NAME)
        edit.remove(CONSTANTS.PREFE_ACCESS_EMAIL)
        edit.remove(CONSTANTS.PREFE_ACCESS_MOBILE)
        edit.remove(CONSTANTS.PREFE_ACCESS_SLEEPTIME)
        edit.remove(CONSTANTS.PREFE_ACCESS_INDEXSCORE)
        edit.remove(CONSTANTS.PREFE_ACCESS_IMAGE)
        edit.remove(CONSTANTS.PREFE_ACCESS_ISPROFILECOMPLETED)
        edit.remove(CONSTANTS.PREFE_ACCESS_ISAssCOMPLETED)
        edit.remove(CONSTANTS.PREFE_ACCESS_directLogin)
        edit.remove(CONSTANTS.PREFE_ACCESS_SCORELEVEL)
        edit.remove(CONSTANTS.PREFE_ACCESS_USEREMAIL)
        edit.remove(CONSTANTS.PREFE_ACCESS_DeviceType)
        edit.remove(CONSTANTS.PREFE_ACCESS_DeviceID)
        edit.remove(CONSTANTS.PREFE_ACCESS_isPinSet)
        edit.remove(CONSTANTS.PREFE_ACCESS_isSetLoginPin)
        edit.remove(CONSTANTS.PREFE_ACCESS_isMainAccount)
        edit.remove(CONSTANTS.PREFE_ACCESS_isEmailVerified)
        edit.remove(CONSTANTS.PREFE_ACCESS_coUserCount)
        edit.remove(CONSTANTS.PREFE_ACCESS_DOB)
        edit.remove(CONSTANTS.PREFE_ACCESS_PlanId)
        edit.remove(CONSTANTS.PREFE_ACCESS_PlanPurchaseDate)
        edit.remove(CONSTANTS.PREFE_ACCESS_PlanExpireDate)
        edit.remove(CONSTANTS.PREFE_ACCESS_TransactionId)
        edit.remove(CONSTANTS.PREFE_ACCESS_TrialPeriodStart)
        edit.remove(CONSTANTS.PREFE_ACCESS_TrialPeriodEnd)
        edit.remove(CONSTANTS.PREFE_ACCESS_PlanStatus)
        edit.remove(CONSTANTS.PREFE_ACCESS_AreaOfFocus)
        edit.remove(CONSTANTS.PREFE_ACCESS_assesmentContent)
        edit.remove(CONSTANTS.PREFE_ACCESS_PlanContent)
        edit.remove(CONSTANTS.PREF_KEY_UnLockAudiList)
        edit.remove(CONSTANTS.PREFE_ACCESS_PlanDeviceType)
        edit.remove(CONSTANTS.PREF_KEY_ReminderFirstLogin)
//        edit.remove(CONSTANTS.PREF_KEY_UserPromocode)
//        edit.remove(CONSTANTS.PREF_KEY_ReferLink)
        edit.clear()
        edit.apply()

        val preferred = requireActivity().getSharedPreferences(CONSTANTS.RecommendedCatMain, Context.MODE_PRIVATE)
        val edited = preferred.edit()
        edited.remove(CONSTANTS.selectedCategoriesTitle)
        edited.remove(CONSTANTS.selectedCategoriesName)
        edited.remove(CONSTANTS.PREFE_ACCESS_SLEEPTIME)
        edited.clear()
        edited.apply()

        val preferred1 = requireActivity().getSharedPreferences(CONSTANTS.AssMain, Context.MODE_PRIVATE)
        val edited1 = preferred1.edit()
        edited1.remove(CONSTANTS.AssQus)
        edited1.remove(CONSTANTS.AssAns)
        edited1.remove(CONSTANTS.AssSort)
        edited1.clear()
        edited1.apply()

        val shared = requireActivity().getSharedPreferences(CONSTANTS.PREF_KEY_LOGOUT, Context.MODE_PRIVATE)
        val editorcv = shared.edit()
        editorcv.putString(CONSTANTS.PREF_KEY_LOGOUT_UserID, userId)
        editorcv.putString(CONSTANTS.PREF_KEY_LOGOUT_CoUserID, coUserId)
        editorcv.apply()

        val preferreed = requireActivity().getSharedPreferences(CONSTANTS.PREF_KEY_USER_ACTIVITY, Context.MODE_PRIVATE)
        val editeed = preferreed.edit()
        editeed.remove(CONSTANTS.PREF_KEY_USER_TRACK_ARRAY)
        editeed.clear()
        editeed.apply()

        val preferrd = requireActivity().getSharedPreferences(CONSTANTS.PREF_KEY_Splash, Context.MODE_PRIVATE)
        val editd = preferrd.edit()
        editd.remove(CONSTANTS.PREF_KEY_SplashKey)
        editd.clear()
        editd.apply()

        /*val preferrder = requireActivity().getSharedPreferences(CONSTANTS.Token, Context.MODE_PRIVATE)
        val editder = preferrder.edit()
        editder.remove(CONSTANTS.Token)
        editder.clear()
        editder.apply()*/

        val preferrderd = requireActivity().getSharedPreferences(CONSTANTS.PREF_KEY_SEGMENT_PLAYLIST, Context.MODE_PRIVATE)
        val editderd = preferrderd.edit()
        editderd.remove(CONSTANTS.PREF_KEY_PlaylistID)
        editderd.remove(CONSTANTS.PREF_KEY_PlaylistName)
        editderd.remove(CONSTANTS.PREF_KEY_PlaylistDescription)
        editderd.remove(CONSTANTS.PREF_KEY_PlaylistType)
        editderd.remove(CONSTANTS.PREF_KEY_Totalhour)
        editderd.remove(CONSTANTS.PREF_KEY_Totalminute)
        editderd.remove(CONSTANTS.PREF_KEY_TotalAudio)
        editderd.remove(CONSTANTS.PREF_KEY_ScreenView)
        editderd.clear()
        editderd.apply()

        val pref = requireActivity().getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE)
        val editt = pref.edit()
        editt.remove(CONSTANTS.PREF_KEY_IsDisclimer)
        editt.remove(CONSTANTS.PREF_KEY_Disclimer)
//        editt.remove(CONSTANTS.PREF_KEY_UnLockAudiList)
        editt.clear()
        editt.apply()

        val preferred2 = requireActivity().getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE)
        val edited2 = preferred2.edit()
        edited2.remove(CONSTANTS.PREF_KEY_MainAudioList)
        edited2.remove(CONSTANTS.PREF_KEY_PlayerAudioList)
        edited2.remove(CONSTANTS.PREF_KEY_AudioPlayerFlag)
        edited2.remove(CONSTANTS.PREF_KEY_PlayerPlaylistId)
        edited2.remove(CONSTANTS.PREF_KEY_PlayerPlaylistName)
        edited2.remove(CONSTANTS.PREF_KEY_PlayerPosition)
        edited2.remove(CONSTANTS.PREF_KEY_Cat_Name)
        edited2.remove(CONSTANTS.PREF_KEY_PlayFrom)
        edited2.clear()
        edited2.apply()
        logout = true
        deleteCache(requireActivity())
        callLogoutApi(dialog, progressBar, progressBarHolder)
    }

    private fun callLogoutApi(dialog: Dialog, progressBar: ProgressBar, progressBarHolder: FrameLayout) {
        val sharedPreferences2 = requireActivity().getSharedPreferences(CONSTANTS.Token, Context.MODE_PRIVATE)
        var fcmId = sharedPreferences2.getString(CONSTANTS.Token, "")
        if (TextUtils.isEmpty(fcmId)) {
            FirebaseInstallations.getInstance().getToken(true).addOnCompleteListener(requireActivity()) { task: Task<InstallationTokenResult> ->
                val newToken = task.result.token
                Log.e("newToken", newToken)
                val editor = requireActivity().getSharedPreferences(CONSTANTS.Token, Context.MODE_PRIVATE).edit()
                editor.putString(CONSTANTS.Token, newToken) // Friend
                editor.apply()
            }
            fcmId = sharedPreferences2.getString(CONSTANTS.Token, "")
        }

        APINewClient.client.getLogout(userId, fcmId, CONSTANTS.FLAG_ONE).enqueue(object : Callback<SucessModel?> {
            override fun onResponse(call: Call<SucessModel?>, response: Response<SucessModel?>) {
                val sucessModel = response.body()
                //                try {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return
                }
                mLastClickTime = SystemClock.elapsedRealtime()
                if (sucessModel!!.responseCode.equals(activity?.getString(R.string.ResponseCodesuccess), ignoreCase = true)) {
                    val p1 = Properties()
                    /* var isProf = false
                     var isAss = false
                     isProf = isProfileCompleted.equals("1", ignoreCase = true)
                     isAss = isAssessmentCompleted.equals("1", ignoreCase = true)*/

                    /* p1.putValue("deviceId", deviceId)
                     p1.putValue("deviceType", "Android")
                     p1.putValue("phone", userMobile)
                     p1.putValue("email", userEmail)
                     p1.putValue("isProfileCompleted", isProf)
                     p1.putValue("isAssessmentCompleted", isAss)
                     p1.putValue("WellnessScore", indexScore)
                     p1.putValue("scoreLevel", scoreLevel)
                     p1.putValue("avgSleepTime", avgSleepTime)
                     p1.putValue("areaOfFocus", areaOfFocus)*/
                    addToSegment("User Logout", p1, CONSTANTS.track)
                    hideProgressBar(binding.progressBar, binding.progressBarHolder, requireActivity())
                    hideProgressBar(progressBar, progressBarHolder, requireActivity())
                    dialog.hide()
                    try {
                        analytics.flush()
                        analytics.reset()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    val i = Intent(requireActivity(), SignInActivity::class.java)
                    i.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                    i.putExtra("mobileNo", "")
                    i.putExtra("countryCode", "")
                    i.putExtra("name", "")
                    i.putExtra("email", "")
                    i.putExtra("countryShortName", "")
                    startActivity(i)
                    requireActivity().finish()
                }
                //                } catch (Exception e) {
                //                    e.printStackTrace();
                //                }
            }

            override fun onFailure(call: Call<SucessModel?>, t: Throwable) {
                hideProgressBar(binding.progressBar, binding.progressBarHolder, requireActivity())
            }
        })
    }

    companion object {
        private const val CONTENT_REQUEST = 100
        var ComeScreenReminder = 0
    }
}