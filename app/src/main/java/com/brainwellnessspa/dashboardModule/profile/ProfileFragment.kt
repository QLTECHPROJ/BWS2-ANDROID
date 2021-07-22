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
    var logoutDialog: Dialog? = null
    lateinit var image: File
    private var mRequestPermissionHandler: RequestPermissionHandler? = null
    private lateinit var options: Array<CharSequence>
    var userId: String? = null
    var coUserId: String? = null
    var userEmail: String? = null
    private var userImage: String? = null
    var deviceId: String? = null
    var deviceType: String? = null
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
        if (isNetworkConnected(activity)) {
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
        profileViewData(activity)
        binding.llImageUpload.setOnClickListener {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return@setOnClickListener
            }
            mLastClickTime = SystemClock.elapsedRealtime()
            if (isNetworkConnected(activity)) {
                selectImage()
            } else {
                showToast(getString(R.string.no_server_found), activity)
            }
        }
        val p = Properties()
        addToSegment("Account Screen Viewed", p, CONSTANTS.screen)
        binding.llAcInfo.setOnClickListener {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return@setOnClickListener
            }
            mLastClickTime = SystemClock.elapsedRealtime()
            if (isNetworkConnected(activity)) {
                val i = Intent(activity, AccountInfoActivity::class.java)
                startActivity(i)
                requireActivity().overridePendingTransition(0, 0)
            } else {
                showToast(getString(R.string.no_server_found), activity)
            }
        }

        binding.llDownloads.setOnClickListener {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return@setOnClickListener
            }
            mLastClickTime = SystemClock.elapsedRealtime()
            val i = Intent(activity, DownloadsActivity::class.java)
            startActivity(i)
            requireActivity().overridePendingTransition(0, 0)
        }

        binding.llInvoices.setOnClickListener {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return@setOnClickListener
            }
            mLastClickTime = SystemClock.elapsedRealtime()
            if (isNetworkConnected(activity)) {
                InvoiceActivity.invoiceToRecepit = 1
                val i = Intent(activity, InvoiceActivity::class.java)
                i.putExtra("ComeFrom", "")
                startActivity(i)
                requireActivity().overridePendingTransition(0, 0)
            } else {
                showToast(getString(R.string.no_server_found), activity)
            }
        }

        binding.llBillingOrder.setOnClickListener {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return@setOnClickListener
            }
            mLastClickTime = SystemClock.elapsedRealtime()
            if (isNetworkConnected(activity)) {
                val i = Intent(activity, BillingOrderActivity::class.java)
                startActivity(i)
                requireActivity().overridePendingTransition(0, 0)
            } else {
                showToast(getString(R.string.no_server_found), activity)
            }
        }

        binding.llManageUser.setOnClickListener {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return@setOnClickListener
            }
            mLastClickTime = SystemClock.elapsedRealtime()
            if (isNetworkConnected(activity)) {
                val i = Intent(activity, ManageUserActivity::class.java)
                startActivity(i)
                requireActivity().overridePendingTransition(0, 0)
            } else {
                showToast(getString(R.string.no_server_found), activity)
            }
        }

        binding.llReminder.setOnClickListener {
            ComeScreenReminder = 1
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return@setOnClickListener
            }
            mLastClickTime = SystemClock.elapsedRealtime()
            if (isNetworkConnected(activity)) {
                val i = Intent(activity, ReminderListsActivity::class.java)
                startActivity(i)
                requireActivity().overridePendingTransition(0, 0)
            } else {
                showToast(getString(R.string.no_server_found), activity)
            }
        }

        binding.llPlan.setOnClickListener {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return@setOnClickListener
            }
            mLastClickTime = SystemClock.elapsedRealtime()
            if (isNetworkConnected(activity)) {
                val i = Intent(activity, EnhanceActivity::class.java)
                startActivity(i)
                requireActivity().overridePendingTransition(0, 0)
            } else {
                showToast(getString(R.string.no_server_found), activity)
            }
        }

        binding.llResources.setOnClickListener {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return@setOnClickListener
            }
            mLastClickTime = SystemClock.elapsedRealtime()
            if (isNetworkConnected(activity)) {
                val i = Intent(activity, ResourceActivity::class.java)
                startActivity(i)
                requireActivity().overridePendingTransition(0, 0)
            } else {
                showToast(getString(R.string.no_server_found), activity)
            }
        }

        binding.llFAQ.setOnClickListener {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return@setOnClickListener
            }
            mLastClickTime = SystemClock.elapsedRealtime()
            if (isNetworkConnected(activity)) {
                val i = Intent(activity, FaqActivity::class.java)
                startActivity(i)
                requireActivity().overridePendingTransition(0, 0)
            } else {
                showToast(getString(R.string.no_server_found), activity)
            }
        }

        binding.llLogOut.setOnClickListener {
            if (isNetworkConnected(activity)) {
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
                    showProgressBar(progressBar, progressBarHolder, activity)
                    deleteCall(logoutDialog!!, progressBar, progressBarHolder)
                }
                tvGoBack.setOnClickListener { logoutDialog!!.hide() }
                logoutDialog!!.show()
                logoutDialog!!.setCancelable(false)
            } else {
                showToast(getString(R.string.no_server_found), activity)
            }
        }
        return view
    }

    override fun onResume() {
        profileViewData(activity)
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
        building.setMessage("""To camera allow ${getString(R.string.app_name)} access to your camera. 
Tap Setting > permission, and turn Camera on.""")
        building.setCancelable(true)
        building.setPositiveButton(getString(R.string.Settings)) { dialogs: DialogInterface, _: Int ->
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri = Uri.fromParts("package", requireActivity().packageName, null)
            intent.data = uri
            startActivity(intent)
            dialogs.dismiss()
        }
        building.setNegativeButton(getString(R.string.not_now)) { dialogs: DialogInterface, _: Int -> dialogs.dismiss() }
        val alert11 = building.create()
        alert11.window!!.setBackgroundDrawableResource(R.drawable.dialog_bg)
        alert11.show()
        alert11.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(requireActivity(), R.color.blue))
        alert11.getButton(android.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(requireActivity(), R.color.blue))
    }

    private fun callReadPermission() {
        val buildable = AlertDialog.Builder(requireActivity())
        buildable.setMessage("""To upload image allow ${getString(R.string.app_name)} access to your device's files. 
Tap Setting > permission, and turn "Files and media" on.""")
        buildable.setCancelable(true)
        buildable.setPositiveButton(getString(R.string.Settings)) { dialogs: DialogInterface, _: Int ->
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri = Uri.fromParts("package", requireActivity().packageName, null)
            intent.data = uri
            startActivity(intent)
            dialogs.dismiss()
        }
        buildable.setNegativeButton(getString(R.string.not_now)) { dialogue: DialogInterface, _: Int -> dialogue.dismiss() }
        val alert11 = buildable.create()
        alert11.window!!.setBackgroundDrawableResource(R.drawable.dialog_bg)
        alert11.show()
        alert11.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(requireActivity(), R.color.blue))
        alert11.getButton(android.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(requireActivity(), R.color.blue))
    }

    private fun callProfilePathSet() {
        options = if (profilePicPath == "") {
            arrayOf(arrayOf(getString(R.string.takePhoto), getString(R.string.chooseFromGallary), getString(R.string.cancel)).toString())
        } else {
            arrayOf(arrayOf(getString(R.string.takePhoto), getString(R.string.chooseFromGallary), getString(R.string.removeProfilePicture), getString(R.string.cancel)).toString())
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
                if (isNetworkConnected(activity)) {
                    showProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                    val listCall = APINewClient.client.getRemoveProfile(coUserId)
                    listCall.enqueue(object : Callback<RemoveProfileModel?> {
                        override fun onResponse(call: Call<RemoveProfileModel?>, response: Response<RemoveProfileModel?>) {
                            try {
                                val viewModel = response.body()
                                hideProgressBar(binding.progressBar, binding.progressBarHolder, requireActivity())
                                if (viewModel != null) {
                                    if (viewModel.responseCode.equals(getString(R.string.ResponseCodesuccess), ignoreCase = true)) {
                                        showToast(viewModel.responseMessage, requireActivity())
                                        val shared = requireActivity().getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, Context.MODE_PRIVATE)
                                        val editor = shared.edit()
                                        editor.putString(CONSTANTS.PREFE_ACCESS_IMAGE, "")
                                        editor.apply()
                                        profileViewData(requireActivity())
                                    } else if (viewModel.responseCode.equals(getString(R.string.ResponseCodeDeleted), ignoreCase = true)) {
                                        deleteCall(activity)
                                        showToast(viewModel.responseMessage, activity)
                                        val i = Intent(activity, SignInActivity::class.java)
                                        i.putExtra("mobileNo", "")
                                        i.putExtra("countryCode", "")
                                        i.putExtra("name", "")
                                        i.putExtra("email", "")
                                        i.putExtra("countryShortName", "")
                                        startActivity(i)
                                        activity?.finish()
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
                val p = Properties()
                addToSegment("Profile Photo Cancelled", p, CONSTANTS.track)
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

    fun profileViewData(ctx: Context?) {
        if (isNetworkConnected(ctx)) {
            showProgressBar(binding.progressBar, binding.progressBarHolder, requireActivity())
            val listCall = APINewClient.client.getCoUserDetails(coUserId)
            listCall.enqueue(object : Callback<AuthOtpModel?> {
                override fun onResponse(call: Call<AuthOtpModel?>, response: Response<AuthOtpModel?>) {
                    try {
                        val viewModel = response.body()
                        if (viewModel != null) {
                            if (viewModel.ResponseCode.equals(getString(R.string.ResponseCodesuccess), ignoreCase = true)) {
                                hideProgressBar(binding.progressBar, binding.progressBarHolder, requireActivity())
                                if (viewModel.ResponseData.Name.equals("", ignoreCase = true) || viewModel.ResponseData.Name.equals(" ", ignoreCase = true)) {
                                    binding.tvName.setText(R.string.Guest)
                                } else {
                                    binding.tvName.text = viewModel.ResponseData.Name
                                }

                                if (viewModel.ResponseData.isMainAccount.equals("1", ignoreCase = true)) {
                                    binding.llManageUser.visibility = View.VISIBLE
                                    binding.llPlan.visibility = View.VISIBLE
                                    binding.viewManage.visibility = View.VISIBLE
                                } else {
                                    binding.llManageUser.visibility = View.GONE
                                    binding.llPlan.visibility = View.GONE
                                    binding.viewManage.visibility = View.GONE
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
                            } else if (viewModel.ResponseCode.equals(getString(R.string.ResponseCodeDeleted), ignoreCase = true)) {
                                deleteCall(activity)
                                showToast(viewModel.ResponseMessage, activity)
                                val i = Intent(activity, SignInActivity::class.java)
                                i.putExtra("mobileNo", "")
                                i.putExtra("countryCode", "")
                                i.putExtra("name", "")
                                i.putExtra("email", "")
                                i.putExtra("countryShortName", "")
                                activity?.startActivity(i)
                                activity?.finish()
                            } else {
                                hideProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onFailure(call: Call<AuthOtpModel?>, t: Throwable) {
                    hideProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                }
            })
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CONTENT_REQUEST && resultCode == Activity.RESULT_OK) {
            try {
                setProfilePic(profilePicPath)
                if (isNetworkConnected(requireActivity())) {
                    showProgressBar(binding.progressBar, binding.progressBarHolder, requireActivity())
                    val map = HashMap<String, String?>()
                    map[CONSTANTS.PREF_KEY_UserID] = coUserId
                    val typedFile = TypedFile(CONSTANTS.MULTIPART_FORMAT, image)
                    apiService!!.getAddProfiles(coUserId, typedFile, object : retrofit.Callback<AddProfileModel> {
                        override fun success(addProfileModel: AddProfileModel, response: retrofit.client.Response) {
                            try {
                                if (addProfileModel.responseCode.equals(getString(R.string.ResponseCodesuccess), ignoreCase = true)) {
                                    hideProgressBar(binding.progressBar, binding.progressBarHolder, requireActivity())
                                    setProfilePic(profilePicPath)
                                    showToast(addProfileModel.responseMessage, requireActivity())
                                    val p = Properties()
                                    addToSegment("Camera Photo Added", p, CONSTANTS.track)
                                    profilePicPath = addProfileModel.responseData?.profileImage
                                    val shared = requireActivity().getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, Context.MODE_PRIVATE)
                                    val editor = shared.edit()
                                    editor.putString(CONSTANTS.PREFE_ACCESS_IMAGE, addProfileModel.responseData?.profileImage)
                                    editor.apply()
                                    profileViewData(activity)
                                } else if (addProfileModel.responseCode.equals(getString(R.string.ResponseCodeDeleted), ignoreCase = true)) {
                                    deleteCall(activity)
                                    showToast(addProfileModel.responseMessage, activity)
                                    val i = Intent(activity, SignInActivity::class.java)
                                    i.putExtra("mobileNo", "")
                                    i.putExtra("countryCode", "")
                                    i.putExtra("name", "")
                                    i.putExtra("email", "")
                                    i.putExtra("countryShortName", "")
                                    startActivity(i)
                                    activity?.finish()
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }

                        override fun failure(e: RetrofitError) {
                            hideProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                            showToast(e.message, activity)
                        }
                    })
                } else {
                    showToast(getString(R.string.no_server_found), activity)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("Permission", e.message!!)
            }
        } else if (requestCode == 2 && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                val selectedImageUri = data.data
                //                Glide.with(this).load(selectedImageUri).dontAnimate()
                //                        .apply(RequestOptions.bitmapTransform(new RoundedCorners(126)))
                //                        .into(binding.civProfile);
                setProfilePic(selectedImageUri.toString())
                if (isNetworkConnected(activity)) {
                    showProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                    val map = HashMap<String, String?>()
                    map[CONSTANTS.PREF_KEY_UserID] = coUserId
                    val file = File(Objects.requireNonNull(getPath(selectedImageUri!!, requireActivity())))
                    val typedFile = TypedFile(CONSTANTS.MULTIPART_FORMAT, file)
                    apiService!!.getAddProfiles(coUserId, typedFile, object : retrofit.Callback<AddProfileModel> {
                        override fun success(addProfileModel: AddProfileModel, response: retrofit.client.Response) {
                            if (addProfileModel.responseCode.equals(getString(R.string.ResponseCodesuccess), ignoreCase = true)) {
                                hideProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                                profilePicPath = addProfileModel.responseData?.profileImage
                                setProfilePic(profilePicPath)
                                val p = Properties()
                                addToSegment("Gallery Photo Added", p, CONSTANTS.track)
                                showToast(addProfileModel.responseMessage, activity)
                                val shared = requireActivity().getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, Context.MODE_PRIVATE)
                                val editor = shared.edit()
                                editor.putString(CONSTANTS.PREFE_ACCESS_IMAGE, addProfileModel.responseData?.profileImage)
                                editor.apply()
                                profileViewData(activity)
                            } else if (addProfileModel.responseCode.equals(getString(R.string.ResponseCodeDeleted), ignoreCase = true)) {
                                deleteCall(activity)
                                showToast(addProfileModel.responseMessage, activity)
                                val i = Intent(activity, SignInActivity::class.java)
                                i.putExtra("mobileNo", "")
                                i.putExtra("countryCode", "")
                                i.putExtra("name", "")
                                i.putExtra("email", "")
                                i.putExtra("countryShortName", "")
                                startActivity(i)
                                activity?.finish()
                            }
                        }

                        override fun failure(e: RetrofitError) {
                            hideProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                            showToast(e.message, activity)
                        }
                    })
                } else {
                    showToast(getString(R.string.no_server_found), activity)
                }
            }
        } else if (requestCode == Activity.RESULT_CANCELED) {
            val p = Properties()
            addToSegment("Profile Photo Cancelled", p, CONSTANTS.track)
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
        edit.remove(CONSTANTS.PREFE_ACCESS_USEREMAIL)
        edit.remove(CONSTANTS.PREFE_ACCESS_DeviceType)
        edit.remove(CONSTANTS.PREFE_ACCESS_DeviceID)
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
        deleteCache(activity)
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
                editor.commit()
            }
            fcmId = sharedPreferences2.getString(CONSTANTS.Token, "")
        }
        val listCall = APINewClient.client.getLogout(userId, fcmId, CONSTANTS.FLAG_ONE)
        listCall.enqueue(object : Callback<SucessModel?> {
            override fun onResponse(call: Call<SucessModel?>, response: Response<SucessModel?>) {
                val sucessModel = response.body()
                //                try {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return
                }
                mLastClickTime = SystemClock.elapsedRealtime()
                if (sucessModel!!.responseCode.equals(getString(R.string.ResponseCodesuccess), ignoreCase = true)) {
                    val p1 = Properties()
                    var isProf = false
                    var isAss = false
                    isProf = isProfileCompleted.equals("1", ignoreCase = true)
                    isAss = isAssessmentCompleted.equals("1", ignoreCase = true)

                    p1.putValue("deviceId", deviceId)
                    p1.putValue("deviceType", "Android")
                    p1.putValue("phone", userMobile)
                    p1.putValue("email", userEmail)
                    p1.putValue("isProfileCompleted", isProf)
                    p1.putValue("isAssessmentCompleted", isAss)
                    p1.putValue("WellnessScore", indexScore)
                    p1.putValue("scoreLevel", scoreLevel)
                    p1.putValue("avgSleepTime", avgSleepTime)
                    p1.putValue("areaOfFocus", areaOfFocus)
                    addToSegment("CoUser Logout", p1, CONSTANTS.track)
                    hideProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                    hideProgressBar(progressBar, progressBarHolder, activity)
                    dialog.hide()
                    try {
                        analytics.flush()
                        analytics.reset()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    val i = Intent(activity, SignInActivity::class.java)
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
                hideProgressBar(binding.progressBar, binding.progressBarHolder, activity)
            }
        })
    }

    companion object {
        private const val CONTENT_REQUEST = 100
        var ComeScreenReminder = 0
    }
}